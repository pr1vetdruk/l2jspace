package ru.privetdruk.l2jspace.gameserver.skill.effects;

import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectTargetMe extends AbstractEffect {
    public EffectTargetMe(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.TARGET_ME;
    }

    @Override
    public boolean onStart() {
        if (getEffected() instanceof Player) {
            if (getEffected().getTarget() == getEffector())
                getEffected().getAI().tryToAttack(getEffector());
            else
                getEffected().setTarget(getEffector());

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