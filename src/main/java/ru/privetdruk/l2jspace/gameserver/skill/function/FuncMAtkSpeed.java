package ru.privetdruk.l2jspace.gameserver.skill.function;

import ru.privetdruk.l2jspace.gameserver.enums.skills.Stats;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.skill.Formula;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;
import ru.privetdruk.l2jspace.gameserver.skill.function.base.Func;

/**
 * @see Func
 */
public class FuncMAtkSpeed extends Func {
    private static final FuncMAtkSpeed INSTANCE = new FuncMAtkSpeed();

    private FuncMAtkSpeed() {
        super(null, Stats.MAGIC_ATTACK_SPEED, 10, 0, null);
    }

    @Override
    public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value) {
        return value * Formula.WIT_BONUS[effector.getStatus().getWIT()];
    }

    public static Func getInstance() {
        return INSTANCE;
    }
}