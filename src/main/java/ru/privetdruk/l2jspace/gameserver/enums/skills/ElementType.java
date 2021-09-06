package ru.privetdruk.l2jspace.gameserver.enums.skills;

public enum ElementType {
    NONE(null, null),
    WIND(Stats.WIND_POWER, Stats.WIND_RES),
    FIRE(Stats.FIRE_POWER, Stats.FIRE_RES),
    WATER(Stats.WATER_POWER, Stats.WATER_RES),
    EARTH(Stats.EARTH_POWER, Stats.EARTH_RES),
    HOLY(Stats.HOLY_POWER, Stats.HOLY_RES),
    DARK(Stats.DARK_POWER, Stats.DARK_RES),
	VALAKAS(Stats.VALAKAS_POWER, Stats.VALAKAS_RES);

    public static final ElementType[] VALUES = values();

    ElementType(Stats atkStat, Stats resStat) {
        this.atkStat = atkStat;
        this.resStat = resStat;
    }

    private final Stats atkStat;
    private final Stats resStat;

    public Stats getAtkStat() {
        return atkStat;
    }

    public Stats getResStat() {
        return resStat;
    }
}