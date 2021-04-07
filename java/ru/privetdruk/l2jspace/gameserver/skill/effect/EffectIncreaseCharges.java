package ru.privetdruk.l2jspace.gameserver.skill.effect;

import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectIncreaseCharges extends AbstractEffect {
    public EffectIncreaseCharges(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.INCREASE_CHARGES;
    }

    @Override
    public boolean onStart() {
        if (!(getEffected() instanceof Player))
            return false;

        ((Player) getEffected()).increaseCharges((int) getTemplate().getValue(), getCount());
        return true;
    }

    @Override
    public boolean onActionTime() {
        return false; // abort effect even if count > 1
    }
}