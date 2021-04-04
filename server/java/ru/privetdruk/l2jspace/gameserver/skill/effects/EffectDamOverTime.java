package ru.privetdruk.l2jspace.gameserver.skill.effects;

import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectDamOverTime extends AbstractEffect {
    public EffectDamOverTime(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.DMG_OVER_TIME;
    }

    @Override
    public boolean onActionTime() {
        if (getEffected().isDead())
            return false;

        double damage = getTemplate().getValue();
        if (damage >= getEffected().getStatus().getHp()) {
            if (getSkill().isToggle()) {
                getEffected().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SKILL_REMOVED_DUE_LACK_HP));
                return false;
            }

            // For DOT skills that will not kill effected player.
            if (!getSkill().killByDOT()) {
                // Fix for players dying by DOTs if HP < 1 since reduceCurrentHP method will kill them
                if (getEffected().getStatus().getHp() <= 1)
                    return true;

                damage = getEffected().getStatus().getHp() - 1;
            }
        }
        getEffected().reduceCurrentHpByDOT(damage, getEffector(), getSkill());

        return true;
    }
}