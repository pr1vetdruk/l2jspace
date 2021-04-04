package ru.privetdruk.l2jspace.gameserver.skill.effects;

import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectManaDamOverTime extends AbstractEffect {
    public EffectManaDamOverTime(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.MANA_DMG_OVER_TIME;
    }

    @Override
    public boolean onActionTime() {
        if (getEffected().isDead())
            return false;

        if (getSkill().isToggle() && getTemplate().getValue() > getEffected().getStatus().getMp()) {
            getEffected().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP));
            return false;
        }

        getEffected().getStatus().reduceMp(getTemplate().getValue());
        return true;
    }
}