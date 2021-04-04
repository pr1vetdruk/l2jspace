package ru.privetdruk.l2jspace.gameserver.skill.conditions;

import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Item;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class ConditionTargetActiveSkillId extends Condition {
    private final int _skillId;

    public ConditionTargetActiveSkillId(int skillId) {
        _skillId = skillId;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item) {
        return effected.getSkill(_skillId) != null;
    }
}