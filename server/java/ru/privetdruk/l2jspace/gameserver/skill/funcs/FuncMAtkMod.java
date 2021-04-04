package ru.privetdruk.l2jspace.gameserver.skill.funcs;

import ru.privetdruk.l2jspace.gameserver.enums.skills.Stats;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.skill.Formulas;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;
import ru.privetdruk.l2jspace.gameserver.skill.basefuncs.Func;

/**
 * @see Func
 */
public class FuncMAtkMod extends Func {
    private static final FuncMAtkMod INSTANCE = new FuncMAtkMod();

    private FuncMAtkMod() {
        super(null, Stats.MAGIC_ATTACK, 10, 0, null);
    }

    @Override
    public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value) {
        final double intMod = Formulas.INT_BONUS[effector.getStatus().getINT()];
        final double lvlMod = effector.getStatus().getLevelMod();

        return value * ((lvlMod * lvlMod) * (intMod * intMod));
    }

    public static Func getInstance() {
        return INSTANCE;
    }
}