package ru.privetdruk.l2jspace.gameserver.skill.effect;

import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Attackable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectRandomizeHate extends AbstractEffect {
    public EffectRandomizeHate(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.RANDOMIZE_HATE;
    }

    @Override
    public boolean onStart() {
        if (!(getEffected() instanceof Attackable))
            return false;

        // if (getEffected().isUnresponsive()) TODO
        // return false;

        ((Attackable) getEffected()).getAggroList().randomizeAttack();

        return true;
    }

    @Override
    public void onExit() {
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}