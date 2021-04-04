package ru.privetdruk.l2jspace.gameserver.skill.conditions;

import java.util.List;

import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Item;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class ConditionTargetRaceId extends Condition {
    private final List<Integer> _raceIds;

    public ConditionTargetRaceId(List<Integer> raceId) {
        _raceIds = raceId;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item) {
        return effected instanceof Npc && _raceIds.contains(((Npc) effected).getTemplate().getRace().ordinal());
    }
}