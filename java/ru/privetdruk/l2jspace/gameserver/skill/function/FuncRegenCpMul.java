package ru.privetdruk.l2jspace.gameserver.skill.function;

import ru.privetdruk.l2jspace.gameserver.enums.skills.Stats;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.skill.Formula;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;
import ru.privetdruk.l2jspace.gameserver.skill.function.base.Func;

/**
 * @see Func
 */
public class FuncRegenCpMul extends Func {
    private static final FuncRegenCpMul INSTANCE = new FuncRegenCpMul();

    private FuncRegenCpMul() {
        super(null, Stats.REGENERATE_CP_RATE, 10, 0, null);
    }

    @Override
    public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value) {
        return value * Formula.CON_BONUS[effector.getStatus().getCON()] * effector.getStatus().getLevelMod();
    }

    public static Func getInstance() {
        return INSTANCE;
    }
}
