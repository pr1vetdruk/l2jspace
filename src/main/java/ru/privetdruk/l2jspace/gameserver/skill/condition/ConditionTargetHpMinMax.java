package ru.privetdruk.l2jspace.gameserver.skill.condition;

import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Item;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class ConditionTargetHpMinMax extends Condition {
    private final int _minHp;
    private final int _maxHp;

    public ConditionTargetHpMinMax(int minHp, int maxHp) {
        _minHp = minHp;
        _maxHp = maxHp;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item) {
        if (effected == null)
            return false;

        final double currentHp = effected.getStatus().getHpRatio() * 100;
        return currentHp >= _minHp && currentHp <= _maxHp;
    }
}