package ru.privetdruk.l2jspace.gameserver.skill.effects;

import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectSeed extends AbstractEffect {
    private int _power = 1;

    public EffectSeed(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.SEED;
    }

    @Override
    public boolean onActionTime() {
        return false;
    }

    public int getPower() {
        return _power;
    }

    public void increasePower() {
        _power++;
    }
}