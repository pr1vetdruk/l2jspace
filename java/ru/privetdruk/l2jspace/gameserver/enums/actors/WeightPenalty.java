package ru.privetdruk.l2jspace.gameserver.enums.actors;

public enum WeightPenalty {
    NONE(1, 1),
    LEVEL_1(1, 0.5),
    LEVEL_2(0.5, 0.5),
    LEVEL_3(0.5, 0.5),
    LEVEL_4(0, 0.1);

    private final double speedMultiplier;
    private final double regenerationMultiplier;

    private WeightPenalty(double speedMultiplier, double regenerationMultiplier) {
        this.speedMultiplier = speedMultiplier;
        this.regenerationMultiplier = regenerationMultiplier;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public double getRegenerationMultiplier() {
        return regenerationMultiplier;
    }
}
