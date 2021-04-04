package ru.privetdruk.l2jspace.gameserver.skill.conditions;

import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Item;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;
import ru.privetdruk.l2jspace.gameserver.taskmanager.GameTimeTaskManager;

public class ConditionGameTime extends Condition {
    private final boolean _night;

    public ConditionGameTime(boolean night) {
        _night = night;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item) {
        return GameTimeTaskManager.getInstance().isNight() == _night;
    }
}