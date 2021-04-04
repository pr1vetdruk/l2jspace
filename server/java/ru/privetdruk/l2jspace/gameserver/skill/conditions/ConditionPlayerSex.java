package ru.privetdruk.l2jspace.gameserver.skill.conditions;

import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Item;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class ConditionPlayerSex extends Condition {
    private final int _sex;

    public ConditionPlayerSex(int sex) {
        _sex = sex;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item) {
        return effector instanceof Player && ((Player) effector).getAppearance().getSex().ordinal() == _sex;
    }
}