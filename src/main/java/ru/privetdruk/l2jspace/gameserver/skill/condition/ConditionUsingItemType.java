package ru.privetdruk.l2jspace.gameserver.skill.condition;

import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Item;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public final class ConditionUsingItemType extends Condition {
    private final int _mask;

    public ConditionUsingItemType(int mask) {
        _mask = mask;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item) {
        return effector instanceof Player && ((Player) effector).getInventory().isWearingType(_mask);
    }
}