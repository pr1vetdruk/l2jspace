package ru.privetdruk.l2jspace.gameserver.skill.effect;

import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectManaHealOverTime extends AbstractEffect {
    public EffectManaHealOverTime(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.MANA_HEAL_OVER_TIME;
    }

    @Override
    public boolean onActionTime() {
        if (getEffected().isDead())
            return false;

        getEffected().getStatus().addMp(getTemplate().getValue());
        return true;
    }
}