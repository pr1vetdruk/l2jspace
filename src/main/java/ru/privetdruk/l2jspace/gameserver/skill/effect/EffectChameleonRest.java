package ru.privetdruk.l2jspace.gameserver.skill.effect;

import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectFlag;
import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectChameleonRest extends AbstractEffect {
    public EffectChameleonRest(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.RELAXING;
    }

    @Override
    public boolean onStart() {
        ((Player) getEffected()).sitDown();

        return super.onStart();
    }

    @Override
    public boolean onActionTime() {
        if (getEffected().isDead())
            return false;

        // Only cont skills shouldn't end
        if (getSkill().getSkillType() != SkillType.CONT)
            return false;

        if (getEffected() instanceof Player && !((Player) getEffected()).isSitting())
            return false;

        if (getTemplate().getValue() > getEffected().getStatus().getMp()) {
            getEffected().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP));
            return false;
        }

        getEffected().getStatus().reduceMp(getTemplate().getValue());
        return true;
    }

    @Override
    public int getEffectFlags() {
        return EffectFlag.SILENT_MOVE.getMask() | EffectFlag.RELAXING.getMask();
    }
}