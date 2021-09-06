package ru.privetdruk.l2jspace.gameserver.skill.function;

import ru.privetdruk.l2jspace.gameserver.enums.skills.Stats;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.skill.Formula;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;
import ru.privetdruk.l2jspace.gameserver.skill.function.base.Func;

/**
 * @see Func
 */
public class FuncMaxCpMul extends Func {
    private static final FuncMaxCpMul INSTANCE = new FuncMaxCpMul();

    private FuncMaxCpMul() {
        super(null, Stats.MAX_CP, 10, 0, null);
    }

    @Override
    public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value) {
        return value * Formula.CON_BONUS[effector.getStatus().getCON()];
    }

    public static Func getInstance() {
        return INSTANCE;
    }
}