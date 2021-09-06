package ru.privetdruk.l2jspace.gameserver.skill.effect;

import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectFlag;
import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectCharmOfLuck extends AbstractEffect {
    public EffectCharmOfLuck(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.CHARM_OF_LUCK;
    }

    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public void onExit() {
        ((Playable) getEffected()).stopCharmOfLuck(this);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }

    @Override
    public int getEffectFlags() {
        return EffectFlag.CHARM_OF_LUCK.getMask();
    }
}