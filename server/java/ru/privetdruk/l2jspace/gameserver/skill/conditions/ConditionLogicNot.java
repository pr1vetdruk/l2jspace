package ru.privetdruk.l2jspace.gameserver.skill.conditions;

import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Item;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class ConditionLogicNot extends Condition {
    private final Condition _condition;

    public ConditionLogicNot(Condition condition) {
        _condition = condition;

        if (getListener() != null)
            _condition.setListener(this);
    }

    @Override
    void setListener(ConditionListener listener) {
        if (listener != null)
            _condition.setListener(this);
        else
            _condition.setListener(null);

        super.setListener(listener);
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item) {
        return !_condition.test(effector, effected, skill, item);
    }
}