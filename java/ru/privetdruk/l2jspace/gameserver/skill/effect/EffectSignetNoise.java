package ru.privetdruk.l2jspace.gameserver.skill.effect;

import ru.privetdruk.l2jspace.gameserver.data.SkillTable;
import ru.privetdruk.l2jspace.gameserver.enums.ZoneId;
import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Door;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.EffectPoint;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.MagicSkillLaunched;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.MagicSkillUse;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;
import ru.privetdruk.l2jspace.gameserver.skill.l2skill.L2SkillSignet;

import java.util.List;

public class EffectSignetNoise extends AbstractEffect {
    private EffectPoint _actor;

    public EffectSignetNoise(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.SIGNET_GROUND;
    }

    @Override
    public boolean onStart() {
        if (!(_skill instanceof L2SkillSignet))
            return false;

        _actor = (EffectPoint) getEffected();
        return true;
    }

    @Override
    public boolean onActionTime() {
        if (getCount() == getTemplate().getCounter() - 1)
            return true; // do nothing first time

        final List<Creature> list = _actor.getKnownTypeInRadius(Creature.class, _skill.getSkillRadius(), creature -> !creature.isDead() && !(creature instanceof Door) && !creature.isInsideZone(ZoneId.PEACE));
        if (list.isEmpty())
            return true;

        final L2Skill signetSkill = SkillTable.getInstance().getInfo(((L2SkillSignet) _skill).effectId, _skill.getLevel());
        final Creature[] targets = list.toArray(new Creature[list.size()]);
        for (Creature creature : targets) {
            for (AbstractEffect effect : creature.getAllEffects()) {
                if (effect.getSkill().isDance())
                    effect.exit();
            }
            _actor.broadcastPacket(new MagicSkillUse(_actor, creature, signetSkill.getId(), signetSkill.getLevel(), 0, 0));
        }
        _actor.broadcastPacket(new MagicSkillLaunched(_actor, signetSkill, targets));
        return true;
    }

    @Override
    public void onExit() {
        if (_actor != null)
            _actor.deleteMe();
    }
}