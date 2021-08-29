package ru.privetdruk.l2jspace.gameserver.skill.effect;

import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.Stats;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectManaHeal extends AbstractEffect {
    public EffectManaHeal(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.MANA_HEAL;
    }

    @Override
    public boolean onStart() {
        if (!getEffected().canBeHealed()) {
            return false;
        }

        double power = getEffected().getStatus().calcStat(Stats.RECHARGE_MP_RATE, getTemplate().getValue(), null, null);
        double amount = getEffected().getStatus().addMp(power);

        getEffected().getStatus().addMp(amount);

        if (getEffected() instanceof Player) {
            if (getEffector() != getEffected()) {
                getEffected().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S2_MP_RESTORED_BY_S1).addCharName(getEffector()).addNumber((int) amount));
            } else {
                getEffected().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_MP_RESTORED).addNumber((int) amount));
            }
        }

        return true;
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}
