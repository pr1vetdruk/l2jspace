package ru.privetdruk.l2jspace.gameserver.skill.effect;

import ru.privetdruk.l2jspace.gameserver.enums.AiEventType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectFlag;
import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectRoot extends AbstractEffect {
    public EffectRoot(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.ROOT;
    }

    @Override
    public boolean onStart() {
        // Abort move.
        getEffected().getMove().stop();

        // Refresh abnormal effects.
        getEffected().updateAbnormalEffect();

        return true;
    }

    @Override
    public void onExit() {
        if (!(getEffected() instanceof Player))
            getEffected().getAI().notifyEvent(AiEventType.THINK, null, null);

        // Refresh abnormal effects.
        getEffected().updateAbnormalEffect();
    }

    @Override
    public boolean onActionTime() {
        return false;
    }

    @Override
    public boolean onSameEffect(AbstractEffect effect) {
        return false;
    }

    @Override
    public int getEffectFlags() {
        return EffectFlag.ROOTED.getMask();
    }
}