package ru.privetdruk.l2jspace.gameserver.skill.function;

import ru.privetdruk.l2jspace.gameserver.enums.Paperdoll;
import ru.privetdruk.l2jspace.gameserver.enums.skills.Stats;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.skill.Formulas;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;
import ru.privetdruk.l2jspace.gameserver.skill.function.base.Func;

/**
 * @see Func
 */
public class FuncMDefMod extends Func {
    private static final FuncMDefMod INSTANCE = new FuncMDefMod();

    private FuncMDefMod() {
        super(null, Stats.MAGIC_DEFENCE, 10, 0, null);
    }

    @Override
    public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value) {
        if (effector instanceof Player) {
            final Player player = (Player) effector;

            if (player.getInventory().hasItemIn(Paperdoll.LFINGER))
                value -= 5;

            if (player.getInventory().hasItemIn(Paperdoll.RFINGER))
                value -= 5;

            if (player.getInventory().hasItemIn(Paperdoll.LEAR))
                value -= 9;

            if (player.getInventory().hasItemIn(Paperdoll.REAR))
                value -= 9;

            if (player.getInventory().hasItemIn(Paperdoll.NECK))
                value -= 13;
        }
        return value * Formulas.MEN_BONUS[effector.getStatus().getMEN()] * effector.getStatus().getLevelMod();
    }

    public static Func getInstance() {
        return INSTANCE;
    }
}