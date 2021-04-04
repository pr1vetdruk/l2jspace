package ru.privetdruk.l2jspace.gameserver.skill.effects;

import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectRecovery extends AbstractEffect {
    public EffectRecovery(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.BUFF;
    }

    @Override
    public boolean onStart() {
        if (getEffected() instanceof Player) {
            ((Player) getEffected()).reduceDeathPenaltyBuffLevel();
            return true;
        }
        return false;
    }

    @Override
    public void onExit() {
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}