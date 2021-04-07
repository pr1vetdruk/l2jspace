package ru.privetdruk.l2jspace.gameserver.skill.condition;

import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Item;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class ConditionPlayerActiveSkillId extends Condition {
    private final int _skillId;
    private final int _skillLevel;

    public ConditionPlayerActiveSkillId(int skillId) {
        _skillId = skillId;
        _skillLevel = -1;
    }

    public ConditionPlayerActiveSkillId(int skillId, int skillLevel) {
        _skillId = skillId;
        _skillLevel = skillLevel;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item) {
        final L2Skill activeSkill = effector.getSkill(_skillId);
        return activeSkill != null && _skillLevel <= activeSkill.getLevel();
    }
}