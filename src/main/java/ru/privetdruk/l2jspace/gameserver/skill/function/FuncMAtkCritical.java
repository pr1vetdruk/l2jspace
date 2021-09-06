package ru.privetdruk.l2jspace.gameserver.skill.function;

import ru.privetdruk.l2jspace.gameserver.enums.skills.Stats;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.skill.Formula;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;
import ru.privetdruk.l2jspace.gameserver.skill.function.base.Func;

/**
 * @see Func
 */
public class FuncMAtkCritical extends Func {
    private static final FuncMAtkCritical INSTANCE = new FuncMAtkCritical();

    private FuncMAtkCritical() {
        super(null, Stats.MCRITICAL_RATE, 10, 0, null);
    }

    @Override
    public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value) {
        if (!(effector instanceof Player) || (effector.getActiveWeaponInstance() != null))
            return value * Formula.WIT_BONUS[effector.getStatus().getWIT()];

        return value;
    }

    public static Func getInstance() {
        return INSTANCE;
    }
}