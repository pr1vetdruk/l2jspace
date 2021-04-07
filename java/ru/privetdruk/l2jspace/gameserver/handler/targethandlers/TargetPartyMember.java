package ru.privetdruk.l2jspace.gameserver.handler.targethandlers;

import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillTargetType;
import ru.privetdruk.l2jspace.gameserver.handler.ITargetHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Summon;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class TargetPartyMember implements ITargetHandler {
    @Override
    public SkillTargetType getTargetType() {
        return SkillTargetType.PARTY_MEMBER;
    }

    @Override
    public Creature[] getTargetList(Creature caster, Creature target, L2Skill skill) {
        return new Creature[]
                {
                        target
                };
    }

    @Override
    public Creature getFinalTarget(Creature caster, Creature target, L2Skill skill) {
        return target;
    }

    @Override
    public boolean meetCastConditions(Playable caster, Creature target, L2Skill skill, boolean isCtrlPressed) {
        if (caster == target)
            return true;

        final Summon summon = caster.getSummon();
        if (summon != null && target == summon)
            return true;

        if (!(target instanceof Playable) || target.isDead()) {
            caster.sendPacket(SystemMessageId.INVALID_TARGET);
            return false;
        }

        if (!caster.isInParty() || !caster.getParty().containsPlayer(target.getActingPlayer())) {
            caster.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addSkillName(skill));
            return false;
        }

        return true;
    }
}