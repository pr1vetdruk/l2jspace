package ru.privetdruk.l2jspace.gameserver.skill.effects;

import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectImobileBuff extends AbstractEffect {
    public EffectImobileBuff(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.BUFF;
    }

    @Override
    public boolean onStart() {
        getEffector().setIsImmobilized(true);
        return true;
    }

    @Override
    public void onExit() {
        getEffector().setIsImmobilized(false);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}