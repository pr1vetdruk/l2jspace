package ru.privetdruk.l2jspace.gameserver.model;

import ru.privetdruk.l2jspace.common.data.StatSet;

public class PlayerLevel {
    private final long _requiredExpToLevelUp;
    private final double _karmaModifier;
    private final double _expLossAtDeath;

    public PlayerLevel(StatSet set) {
        _requiredExpToLevelUp = set.getLong("requiredExpToLevelUp");
        _karmaModifier = set.getDouble("karmaModifier", 0.);
        _expLossAtDeath = set.getDouble("expLossAtDeath", 0.);
    }

    public long getRequiredExpToLevelUp() {
        return _requiredExpToLevelUp;
    }

    public double getKarmaModifier() {
        return _karmaModifier;
    }

    public double getExpLossAtDeath() {
        return _expLossAtDeath;
    }
}