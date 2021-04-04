package ru.privetdruk.l2jspace.gameserver.handler.targethandlers;

import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillTargetType;
import ru.privetdruk.l2jspace.gameserver.handler.ITargetHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Chest;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Door;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class TargetUnlockable implements ITargetHandler {
    @Override
    public SkillTargetType getTargetType() {
        return SkillTargetType.UNLOCKABLE;
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
        if (!(target instanceof Door) && !(target instanceof Chest)) {
            caster.sendPacket(SystemMessageId.INVALID_TARGET);
            return false;
        }

        if (target instanceof Door && !((Door) target).isUnlockable())
            return false;

        return true;
    }
}