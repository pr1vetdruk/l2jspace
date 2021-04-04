package ru.privetdruk.l2jspace.gameserver.skill.funcs;

import ru.privetdruk.l2jspace.gameserver.enums.skills.Stats;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.skill.Formulas;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;
import ru.privetdruk.l2jspace.gameserver.skill.basefuncs.Func;

/**
 * @see Func
 */
public class FuncPAtkSpeed extends Func {
    private static final FuncPAtkSpeed INSTANCE = new FuncPAtkSpeed();

    private FuncPAtkSpeed() {
        super(null, Stats.POWER_ATTACK_SPEED, 10, 0, null);
    }

    @Override
    public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value) {
        return value * Formulas.DEX_BONUS[effector.getStatus().getDEX()];
    }

    public static Func getInstance() {
        return INSTANCE;
    }
}