package ru.privetdruk.l2jspace.gameserver.skill.effect;

import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectFlag;
import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectSilentMove extends AbstractEffect {
    public EffectSilentMove(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.SILENT_MOVE;
    }

    @Override
    public boolean onActionTime() {
        // Only cont skills shouldn't end
        if (getSkill().getSkillType() != SkillType.CONT)
            return false;

        if (getEffected().isDead())
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
        return EffectFlag.SILENT_MOVE.getMask();
    }
}