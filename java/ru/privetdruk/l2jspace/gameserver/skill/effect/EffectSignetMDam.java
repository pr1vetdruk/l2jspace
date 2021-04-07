package ru.privetdruk.l2jspace.gameserver.skill.effect;

import java.util.List;

import ru.privetdruk.l2jspace.gameserver.data.xml.NpcData;
import ru.privetdruk.l2jspace.gameserver.enums.ZoneId;
import ru.privetdruk.l2jspace.gameserver.enums.items.ShotType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillTargetType;
import ru.privetdruk.l2jspace.gameserver.idfactory.IdFactory;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.Summon;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Door;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.EffectPoint;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.model.location.Location;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.MagicSkillLaunched;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.MagicSkillUse;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.Formulas;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;
import ru.privetdruk.l2jspace.gameserver.skill.l2skill.L2SkillSignetCasttime;

public class EffectSignetMDam extends AbstractEffect {
    private EffectPoint _actor;

    public EffectSignetMDam(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.SIGNET_GROUND;
    }

    @Override
    public boolean onStart() {
        if (!(_skill instanceof L2SkillSignetCasttime))
            return false;

        final NpcTemplate template = NpcData.getInstance().getTemplate(((L2SkillSignetCasttime) getSkill()).effectNpcId);
        if (template == null)
            return false;

        final EffectPoint effectPoint = new EffectPoint(IdFactory.getInstance().getNextId(), template, getEffector());
        effectPoint.getStatus().setMaxHpMp();

        Location worldPosition = null;
        if (getEffector() instanceof Player && getSkill().getTargetType() == SkillTargetType.GROUND)
            worldPosition = ((Player) getEffector()).getCast().getSignetLocation();

        effectPoint.setInvul(true);
        effectPoint.spawnMe((worldPosition != null) ? worldPosition : getEffector().getPosition());

        _actor = effectPoint;
        return true;

    }

    @Override
    public boolean onActionTime() {
        if (getCount() >= getTemplate().getCounter() - 2)
            return true; // do nothing first 2 times

        final Player caster = (Player) getEffector();
        final int mpConsume = getSkill().getMpConsume();

        if (mpConsume > caster.getStatus().getMp()) {
            caster.sendPacket(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP);
            return false;
        }

        caster.getStatus().reduceMp(mpConsume);

        final List<Creature> list = _actor.getKnownTypeInRadius(Creature.class, _skill.getSkillRadius(), creature -> !creature.isDead() && !(creature instanceof Door) && !creature.isInsideZone(ZoneId.PEACE));
        if (list.isEmpty())
            return true;

        final Creature[] targets = list.toArray(new Creature[list.size()]);
        for (Creature target : targets) {
            final boolean mcrit = Formulas.calcMCrit(caster, target, getSkill());
            final byte shld = Formulas.calcShldUse(caster, target, getSkill());
            final boolean sps = caster.isChargedShot(ShotType.SPIRITSHOT);
            final boolean bsps = caster.isChargedShot(ShotType.BLESSED_SPIRITSHOT);
            final int mdam = (int) Formulas.calcMagicDam(caster, target, getSkill(), shld, sps, bsps, mcrit);

            if (target instanceof Summon)
                target.getStatus().broadcastStatusUpdate();

            if (mdam > 0) {
                // Manage cast break of the target (calculating rate, sending message...)
                Formulas.calcCastBreak(target, mdam);

                caster.sendDamageMessage(target, mdam, mcrit, false, false);
                target.reduceCurrentHp(mdam, caster, getSkill());
            }

            _actor.broadcastPacket(new MagicSkillUse(_actor, target, _skill.getId(), _skill.getLevel(), 0, 0));
        }
        _actor.broadcastPacket(new MagicSkillLaunched(_actor, _skill, targets));
        return true;
    }

    @Override
    public void onExit() {
        if (_actor != null)
            _actor.deleteMe();
    }
}