package ru.privetdruk.l2jspace.gameserver.enums;

public enum TeamType {
    NONE(0),
    BLUE(1),
    RED(2);

    private final int id;

    private TeamType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static TeamType fromId(int id) {
        for (TeamType teamType : TeamType.values()) {
            if (teamType.getId() == id) {
                return teamType;
            }
        }

        return null;
    }
}