package ru.privetdruk.l2jspace.gameserver.skill.effect;

import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.Summon;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectImobilePetBuff extends AbstractEffect {
    public EffectImobilePetBuff(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.BUFF;
    }

    @Override
    public boolean onStart() {
        if (getEffected() instanceof Summon && getEffector() instanceof Player && ((Summon) getEffected()).getOwner() == getEffector()) {
            getEffected().setIsImmobilized(true);
            return true;
        }
        return false;
    }

    @Override
    public void onExit() {
        getEffected().setIsImmobilized(false);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}