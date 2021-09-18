package ru.privetdruk.l2jspace.gameserver.enums;

public enum TeamAura {
    NONE(0),
    BLUE(1),
    RED(2);

    private final int id;

    TeamAura(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static TeamAura fromId(int id) {
        for (TeamAura teamAura : TeamAura.values()) {
            if (teamAura.getId() == id) {
                return teamAura;
            }
        }

        return null;
    }
}