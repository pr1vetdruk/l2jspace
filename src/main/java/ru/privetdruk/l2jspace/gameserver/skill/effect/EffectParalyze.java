package ru.privetdruk.l2jspace.gameserver.skill.effect;

import ru.privetdruk.l2jspace.gameserver.enums.AiEventType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.AbnormalEffect;
import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectFlag;
import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectParalyze extends AbstractEffect {
    public EffectParalyze(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.PARALYZE;
    }

    @Override
    public boolean onStart() {
        getEffected().startAbnormalEffect(AbnormalEffect.HOLD_1);

        // Abort attack, cast and move.
        getEffected().abortAll(false);

        return true;
    }

    @Override
    public void onExit() {
        getEffected().stopAbnormalEffect(AbnormalEffect.HOLD_1);

        if (!(getEffected() instanceof Player))
            getEffected().getAI().notifyEvent(AiEventType.THINK, null, null);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }

    @Override
    public int getEffectFlags() {
        return EffectFlag.PARALYZED.getMask();
    }
}