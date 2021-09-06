package ru.privetdruk.l2jspace.gameserver.skill.effect;

import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Monster;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.Formula;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectSpoil extends AbstractEffect {
    public EffectSpoil(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.SPOIL;
    }

    @Override
    public boolean onStart() {
        if (!(getEffector() instanceof Player))
            return false;

        if (!(getEffected() instanceof Monster))
            return false;

        final Monster target = (Monster) getEffected();
        if (target.isDead())
            return false;

        if (target.getSpoilState().isSpoiled()) {
            getEffector().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ALREADY_SPOILED));
            return false;
        }

        if (Formula.calcMagicSuccess(getEffector(), target, getSkill())) {
            target.getSpoilState().setSpoilerId(getEffector().getObjectId());
            getEffector().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SPOIL_SUCCESS));
        }

        return true;
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}