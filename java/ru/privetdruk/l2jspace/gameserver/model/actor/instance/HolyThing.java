package ru.privetdruk.l2jspace.gameserver.model.actor.instance;

import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public final class HolyThing extends Folk {
    public HolyThing(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public boolean isAttackableBy(Creature attacker) {
        return false;
    }

    @Override
    public void reduceCurrentHp(double damage, Creature attacker, L2Skill skill) {
    }

    @Override
    public void reduceCurrentHp(double damage, Creature attacker, boolean awake, boolean isDOT, L2Skill skill) {
    }

    @Override
    public void onInteract(Player player) {
    }
}
