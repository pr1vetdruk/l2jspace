package ru.privetdruk.l2jspace.gameserver.skill.function;

import ru.privetdruk.l2jspace.gameserver.enums.skills.Stats;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Summon;
import ru.privetdruk.l2jspace.gameserver.skill.Formulas;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;
import ru.privetdruk.l2jspace.gameserver.skill.function.base.Func;

/**
 * @see Func
 */
public class FuncAtkCritical extends Func {
    private static final FuncAtkCritical INSTANCE = new FuncAtkCritical();

    private FuncAtkCritical() {
        super(null, Stats.CRITICAL_RATE, 10, 0, null);
    }

    @Override
    public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value) {
        if (!(effector instanceof Summon))
            value *= Formulas.DEX_BONUS[effector.getStatus().getDEX()];

        return value * 10;
    }

    public static Func getInstance() {
        return INSTANCE;
    }
}