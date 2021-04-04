package ru.privetdruk.l2jspace.gameserver.skill.conditions;

import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Item;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class ConditionPlayerPkCount extends Condition {
    public final int _pk;

    public ConditionPlayerPkCount(int pk) {
        _pk = pk;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item) {
        return effector instanceof Player && ((Player) effector).getPkKills() <= _pk;
    }
}