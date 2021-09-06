package ru.privetdruk.l2jspace.gameserver.handler.targethandlers;

import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillTargetType;
import ru.privetdruk.l2jspace.gameserver.handler.ITargetHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Summon;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class TargetOwnerPet implements ITargetHandler {
    @Override
    public SkillTargetType getTargetType() {
        return SkillTargetType.OWNER_PET;
    }

    @Override
    public Creature[] getTargetList(Creature caster, Creature target, L2Skill skill) {
        if (!(caster instanceof Summon))
            return EMPTY_TARGET_ARRAY;

        return new Creature[]
                {
                        caster.getActingPlayer()
                };
    }

    @Override
    public Creature getFinalTarget(Creature caster, Creature target, L2Skill skill) {
        if (!(caster instanceof Summon))
            return null;

        return caster.getActingPlayer();
    }

    @Override
    public boolean meetCastConditions(Playable caster, Creature target, L2Skill skill, boolean isCtrlPressed) {
        if (target == null || caster.getActingPlayer() != target) {
            caster.sendPacket(SystemMessageId.INVALID_TARGET);
            return false;
        }

        if (target.isDead()) {
            caster.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addSkillName(skill));
            return false;
        }
        return true;
    }
}