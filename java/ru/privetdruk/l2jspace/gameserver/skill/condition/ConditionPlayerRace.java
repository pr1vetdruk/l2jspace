package ru.privetdruk.l2jspace.gameserver.skill.condition;

import ru.privetdruk.l2jspace.gameserver.enums.actors.ClassRace;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Item;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class ConditionPlayerRace extends Condition {
    private final ClassRace _race;

    public ConditionPlayerRace(ClassRace race) {
        _race = race;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item) {
        return effector instanceof Player && ((Player) effector).getRace() == _race;
    }
}