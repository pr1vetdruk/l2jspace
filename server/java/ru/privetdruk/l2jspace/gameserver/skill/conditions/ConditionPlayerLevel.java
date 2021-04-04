package ru.privetdruk.l2jspace.gameserver.skill.conditions;

import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Item;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class ConditionPlayerLevel extends Condition {
    private final int _level;

    public ConditionPlayerLevel(int level) {
        _level = level;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item) {
        return effector.getStatus().getLevel() >= _level;
    }
}