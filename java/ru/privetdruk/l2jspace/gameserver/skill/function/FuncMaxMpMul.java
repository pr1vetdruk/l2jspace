package ru.privetdruk.l2jspace.gameserver.skill.function;

import ru.privetdruk.l2jspace.gameserver.enums.skills.Stats;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.skill.Formulas;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;
import ru.privetdruk.l2jspace.gameserver.skill.function.base.Func;

/**
 * @see Func
 */
public class FuncMaxMpMul extends Func {
    private static final FuncMaxMpMul INSTANCE = new FuncMaxMpMul();

    private FuncMaxMpMul() {
        super(null, Stats.MAX_MP, 10, 0, null);
    }

    @Override
    public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value) {
        return value * Formulas.MEN_BONUS[effector.getStatus().getMEN()];
    }

    public static Func getInstance() {
        return INSTANCE;
    }
}