package ru.privetdruk.l2jspace.gameserver.skill.condition;

import ru.privetdruk.l2jspace.gameserver.enums.skills.Stats;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Item;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class ConditionSkillStats extends Condition {
    private final Stats _stat;

    public ConditionSkillStats(Stats stat) {
        super();

        _stat = stat;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item) {
        return skill != null && skill.getStat() == _stat;
    }
}