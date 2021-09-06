package ru.privetdruk.l2jspace.gameserver.model.holder;

import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

/**
 * A class extending {@link IntIntHolder} containing all neccessary information to maintain valid effects duration.
 */
public final class EffectHolder extends IntIntHolder {
    private final int _duration;

    public EffectHolder(L2Skill skill, int duration) {
        super(skill.getId(), skill.getLevel());

        _duration = duration;
    }

    public int getDuration() {
        return _duration;
    }
}