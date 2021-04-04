package ru.privetdruk.l2jspace.gameserver.handler.targethandlers;

import java.util.ArrayList;
import java.util.List;

import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillTargetType;
import ru.privetdruk.l2jspace.gameserver.handler.ITargetHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.Summon;
import ru.privetdruk.l2jspace.gameserver.model.group.Party;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class TargetParty implements ITargetHandler {
    @Override
    public SkillTargetType getTargetType() {
        return SkillTargetType.PARTY;
    }

    @Override
    public Creature[] getTargetList(Creature caster, Creature target, L2Skill skill) {
        final List<Creature> list = new ArrayList<>();
        list.add(caster);

        final Player player = caster.getActingPlayer();
        if (caster instanceof Summon && skill.addCharacter(caster, player, false))
            list.add(player);
        else if (caster instanceof Player && skill.addSummon(caster, player, false))
            list.add(player.getSummon());

        final Party party = caster.getParty();
        if (party != null) {
            for (Player member : party.getMembers()) {
                if (member == player)
                    continue;

                if (skill.addCharacter(caster, member, false))
                    list.add(member);

                if (skill.addSummon(caster, member, false))
                    list.add(member.getSummon());
            }
        }

        return list.toArray(new Creature[list.size()]);
    }

    @Override
    public Creature getFinalTarget(Creature caster, Creature target, L2Skill skill) {
        return caster;
    }

    @Override
    public boolean meetCastConditions(Playable caster, Creature target, L2Skill skill, boolean isCtrlPressed) {
        return true;
    }
}