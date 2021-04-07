package ru.privetdruk.l2jspace.gameserver.skill.condition;

import java.util.List;

import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Door;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Item;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class ConditionTargetNpcId extends Condition {
    private final List<Integer> _npcIds;

    public ConditionTargetNpcId(List<Integer> npcIds) {
        _npcIds = npcIds;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item) {
        if (effected instanceof Npc)
            return _npcIds.contains(((Npc) effected).getNpcId());

        if (effected instanceof Door)
            return _npcIds.contains(((Door) effected).getDoorId());

        return false;
    }
}