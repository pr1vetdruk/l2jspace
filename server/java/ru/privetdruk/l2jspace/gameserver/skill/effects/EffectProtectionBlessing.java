package ru.privetdruk.l2jspace.gameserver.skill.effects;

import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectFlag;
import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectProtectionBlessing extends AbstractEffect {
    public EffectProtectionBlessing(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.PROTECTION_BLESSING;
    }

    @Override
    public boolean onStart() {
        return false;
    }

    @Override
    public void onExit() {
        ((Playable) getEffected()).stopProtectionBlessing(this);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }

    @Override
    public int getEffectFlags() {
        return EffectFlag.PROTECTION_BLESSING.getMask();
    }
}