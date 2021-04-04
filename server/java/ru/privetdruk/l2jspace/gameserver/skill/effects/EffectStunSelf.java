package ru.privetdruk.l2jspace.gameserver.skill.effects;

import ru.privetdruk.l2jspace.gameserver.enums.AiEventType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectFlag;
import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectStunSelf extends AbstractEffect {
    public EffectStunSelf(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.STUN_SELF;
    }

    @Override
    public boolean onStart() {
        // Trigger onAttacked event.
        getEffector().getAI().notifyEvent(AiEventType.ATTACKED, getEffector(), null);

        getEffector().getAI().tryToIdle();

        // Refresh abnormal effects.
        getEffector().updateAbnormalEffect();

        return true;
    }

    @Override
    public void onExit() {
        // TODO This never occurs in interlude. Besides punch of doom (a player skill), in IL there is no other skill. this is here for <skill id="5183" levels="1" name="Production: Dimensional Stun">
        if (!(getEffector() instanceof Player))
            getEffector().getAI().notifyEvent(AiEventType.THINK, null, null);

        // Refresh abnormal effects.
        getEffector().updateAbnormalEffect();
    }

    @Override
    public boolean onActionTime() {
        return false;
    }

    @Override
    public boolean isSelfEffectType() {
        return true;
    }

    @Override
    public int getEffectFlags() {
        return EffectFlag.STUNNED.getMask();
    }
}