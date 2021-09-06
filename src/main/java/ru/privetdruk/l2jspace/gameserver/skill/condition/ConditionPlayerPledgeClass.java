package ru.privetdruk.l2jspace.gameserver.skill.condition;

import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Item;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public final class ConditionPlayerPledgeClass extends Condition {
    private final int _pledgeClass;

    public ConditionPlayerPledgeClass(int pledgeClass) {
        _pledgeClass = pledgeClass;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item) {
        if (!(effector instanceof Player))
            return false;

        final Player player = (Player) effector;

        if (player.getClan() == null)
            return false;

        if (_pledgeClass == -1)
            return player.isClanLeader();

        return player.getPledgeClass() >= _pledgeClass;
    }
}