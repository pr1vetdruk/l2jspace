package ru.privetdruk.l2jspace.gameserver.skill.effect;

import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Door;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ExRegenMax;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectHealOverTime extends AbstractEffect {
    public EffectHealOverTime(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.HEAL_OVER_TIME;
    }

    @Override
    public boolean onStart() {
        // If effected is a player, send a hp regen effect packet.
        if (getEffected() instanceof Player && getTemplate().getCounter() > 0 && getPeriod() > 0)
            getEffected().sendPacket(new ExRegenMax(getTemplate().getCounter() * getPeriod(), getPeriod(), getTemplate().getValue()));

        return true;
    }

    @Override
    public boolean onActionTime() {
        // Doesn't affect doors and dead characters.
        if (getEffected().isDead() || getEffected() instanceof Door)
            return false;

        getEffected().getStatus().addHp(getTemplate().getValue());
        return true;
    }
}