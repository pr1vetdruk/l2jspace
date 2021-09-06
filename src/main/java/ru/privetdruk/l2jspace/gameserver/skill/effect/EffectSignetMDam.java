package ru.privetdruk.l2jspace.gameserver.skill.effect;

import ru.privetdruk.l2jspace.gameserver.data.xml.NpcData;
import ru.privetdruk.l2jspace.gameserver.enums.AiEventType;
import ru.privetdruk.l2jspace.gameserver.enums.ZoneId;
import ru.privetdruk.l2jspace.gameserver.enums.items.ShotType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.ShieldDefense;
import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillTargetType;
import ru.privetdruk.l2jspace.gameserver.idfactory.IdFactory;
import ru.privetdruk.l2jspace.gameserver.model.actor.*;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Door;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.EffectPoint;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.model.location.Location;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.MagicSkillLaunched;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.Formula;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;
import ru.privetdruk.l2jspace.gameserver.skill.l2skill.L2SkillSignetCasttime;

import java.util.ArrayList;
import java.util.List;

public class EffectSignetMDam extends AbstractEffect {
    private boolean _srcInArena;
    private int _state = 0;
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
        // on offi the zone get created and the first wave starts later
        // there is also an first hit animation to the caster
        switch (_state) {
            case 0, 2 -> {
                _state++;
                return true;
            }
            case 1 -> {
                getEffected().broadcastPacket(new MagicSkillLaunched(_actor, getSkill(), new Creature[]
                        {
                                getEffected()
                        }));
                _state++;
                return true;
            }
        }

        int mpConsume = getSkill().getMpConsume();
        Player caster = (Player) getEffected();

        boolean ss = false;
        boolean bss = false;

        caster.rechargeShots(false, true);

        ArrayList<Creature> targets = new ArrayList<>();

        List<Creature> knownTypeInRadius = _actor.getKnownTypeInRadius(
                Creature.class,
                _skill.getSkillRadius(),
                creature -> !creature.isDead() && !(creature instanceof Door) && !creature.isInsideZone(ZoneId.PEACE)
        );

        for (Creature creature : knownTypeInRadius) {
            if (creature == null || creature == getEffected()) {
                continue;
            }

            if (creature instanceof Attackable || creature instanceof Playable) {
                if (creature.isAlikeDead()) {
                    continue;
                }

                // isSignetOffensiveSkill only really checks for Day of Doom, the other signets ahve different Effects
                if (_skill.isOffensive() && !_skill.checkForAreaOffensiveSkill(_actor, creature, true, _srcInArena)) {
                    continue;
                }

                if (mpConsume > caster.getStatus().getMp()) {
                    caster.sendPacket(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP);
                    return false;
                }

                caster.getStatus().reduceMp(mpConsume);

                targets.add(creature);
            }
        }

        if (targets.size() > 0) {
            caster.broadcastPacket(new MagicSkillLaunched(caster, getSkill(), targets.toArray(new Creature[targets.size()])));

            for (Creature target : targets) {
                boolean isCrit = Formula.calcMCrit(caster, target, getSkill());
                ShieldDefense sDef = Formula.calcShieldUse(caster, target, getSkill(), false);
                boolean sps = caster.isChargedShot(ShotType.SPIRITSHOT);
                boolean bsps = caster.isChargedShot(ShotType.BLESSED_SPIRITSHOT);
                int damage = (int) Formula.calcMagicDam(caster, target, getSkill(), sDef, sps, bsps, isCrit);

                if (target instanceof Summon) {
                    target.getStatus().broadcastStatusUpdate();
                }

                if (damage > 0) {
                    // Manage cast break of the target (calculating rate, sending message...)
                    Formula.calcCastBreak(target, damage);

                    caster.sendDamageMessage(target, damage, isCrit, false, false);
                    target.reduceCurrentHp(damage, caster, getSkill());
                }

                target.getAI().notifyEvent(AiEventType.ATTACKED, caster, target);
            }
        }

        return true;
    }

    @Override
    public void onExit() {
        if (_actor != null)
            _actor.deleteMe();
    }
}