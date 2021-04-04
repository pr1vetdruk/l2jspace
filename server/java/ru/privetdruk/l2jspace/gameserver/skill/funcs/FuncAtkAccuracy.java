package ru.privetdruk.l2jspace.gameserver.skill.funcs;

import ru.privetdruk.l2jspace.gameserver.enums.skills.Stats;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Summon;
import ru.privetdruk.l2jspace.gameserver.skill.Formulas;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;
import ru.privetdruk.l2jspace.gameserver.skill.basefuncs.Func;

/**
 * @see Func
 */
public class FuncAtkAccuracy extends Func {
    private static final FuncAtkAccuracy INSTANCE = new FuncAtkAccuracy();

    private FuncAtkAccuracy() {
        super(null, Stats.ACCURACY_COMBAT, 10, 0, null);
    }

    @Override
    public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value) {
        final int level = effector.getStatus().getLevel();

        value += Formulas.BASE_EVASION_ACCURACY[effector.getStatus().getDEX()] + level;
        if (effector instanceof Summon)
            value += (level < 60) ? 4 : 5;

        return value;
    }

    public static Func getInstance() {
        return INSTANCE;
    }
}