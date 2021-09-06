package ru.privetdruk.l2jspace.gameserver.skill.function.base;

import ru.privetdruk.l2jspace.gameserver.enums.skills.Stats;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;
import ru.privetdruk.l2jspace.gameserver.skill.condition.Condition;

/**
 * @see Func
 */
public class FuncBaseMul extends Func {
    public FuncBaseMul(Object owner, Stats stat, double value, Condition cond) {
        super(owner, stat, 1, value, cond);
    }

    @Override
    public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value) {
        // Condition does not exist or it fails, no change.
        if (getCond() != null && !getCond().test(effector, effected, skill))
            return value;

        // Update value.
        return value + base * getValue();
    }
}