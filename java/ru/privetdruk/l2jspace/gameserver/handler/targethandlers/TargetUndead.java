package ru.privetdruk.l2jspace.gameserver.handler.targethandlers;

import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillTargetType;
import ru.privetdruk.l2jspace.gameserver.handler.ITargetHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Monster;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Servitor;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class TargetUndead implements ITargetHandler {
    @Override
    public SkillTargetType getTargetType() {
        return SkillTargetType.UNDEAD;
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
        if ((!(target instanceof Monster) && !(target instanceof Servitor)) || target.isDead()) {
            caster.sendPacket(SystemMessageId.INVALID_TARGET);
            return false;
        }

        if (!target.isUndead()) {
            caster.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addSkillName(skill));
            return false;
        }
        return true;
    }
}