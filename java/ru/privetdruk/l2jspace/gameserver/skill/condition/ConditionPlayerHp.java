package ru.privetdruk.l2jspace.gameserver.skill.condition;

import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Item;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class ConditionPlayerHp extends Condition {
    private final int _hp;

    public ConditionPlayerHp(int hp) {
        _hp = hp;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item) {
        return effector.getStatus().getHpRatio() * 100 <= _hp;
    }
}