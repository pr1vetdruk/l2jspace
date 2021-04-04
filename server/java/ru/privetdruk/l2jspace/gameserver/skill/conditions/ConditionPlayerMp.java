package ru.privetdruk.l2jspace.gameserver.skill.conditions;

import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Item;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class ConditionPlayerMp extends Condition {
    private final int _mp;

    public ConditionPlayerMp(int mp) {
        _mp = mp;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item) {
        return effector.getStatus().getMpRatio() * 100 <= _mp;
    }
}