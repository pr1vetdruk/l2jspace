package ru.privetdruk.l2jspace.gameserver.skill.function;

import ru.privetdruk.l2jspace.gameserver.enums.skills.Stats;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.skill.Formula;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;
import ru.privetdruk.l2jspace.gameserver.skill.function.base.Func;

/**
 * @see Func
 */
public class FuncPAtkMod extends Func {
    private static final FuncPAtkMod INSTANCE = new FuncPAtkMod();

    private FuncPAtkMod() {
        super(null, Stats.POWER_ATTACK, 10, 0, null);
    }

    @Override
    public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value) {
        return value * Formula.STR_BONUS[effector.getStatus().getSTR()] * effector.getStatus().getLevelMod();
    }

    public static Func getInstance() {
        return INSTANCE;
    }
}