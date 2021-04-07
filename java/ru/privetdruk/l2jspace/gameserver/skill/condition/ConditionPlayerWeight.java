package ru.privetdruk.l2jspace.gameserver.skill.condition;

import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Item;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class ConditionPlayerWeight extends Condition {
    private final int _weight;

    public ConditionPlayerWeight(int weight) {
        _weight = weight;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item) {
        if (effector instanceof Player)
            return ((Player) effector).getWeightPenalty() < _weight;

        return true;
    }
}