package ru.privetdruk.l2jspace.gameserver.skill.effects;

import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectAbortCast extends AbstractEffect {
    public EffectAbortCast(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.ABORT_CAST;
    }

    @Override
    public boolean onStart() {
        if (getEffected() == null || getEffected() == getEffector() || getEffected().isRaidRelated())
            return false;

        if (getEffected().getCast().isCastingNow())
            getEffected().getCast().interrupt();

        return true;
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}