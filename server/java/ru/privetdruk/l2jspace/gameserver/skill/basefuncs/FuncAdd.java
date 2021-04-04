package ru.privetdruk.l2jspace.gameserver.skill.basefuncs;

import ru.privetdruk.l2jspace.gameserver.enums.skills.Stats;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;
import ru.privetdruk.l2jspace.gameserver.skill.conditions.Condition;

/**
 * @see Func
 */
public class FuncAdd extends Func {
    public FuncAdd(Object owner, Stats stat, double value, Condition cond) {
        super(owner, stat, 30, value, cond);
    }

    @Override
    public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value) {
        // Condition does not exist or it fails, no change.
        if (getCond() != null && !getCond().test(effector, effected, skill))
            return value;

        // Update value.
        return value + getValue();
    }
}