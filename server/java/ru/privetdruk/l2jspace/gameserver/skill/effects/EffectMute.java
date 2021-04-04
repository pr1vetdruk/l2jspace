package ru.privetdruk.l2jspace.gameserver.skill.effects;

import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectFlag;
import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectMute extends AbstractEffect {
    public EffectMute(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.MUTE;
    }

    @Override
    public boolean onStart() {
        // Abort cast.
        getEffected().getCast().stop();

        // Refresh abnormal effects.
        getEffected().updateAbnormalEffect();

        return true;
    }

    @Override
    public boolean onActionTime() {
        return false;
    }

    @Override
    public void onExit() {
        // Refresh abnormal effects.
        getEffected().updateAbnormalEffect();
    }

    @Override
    public int getEffectFlags() {
        return EffectFlag.MUTED.getMask();
    }
}