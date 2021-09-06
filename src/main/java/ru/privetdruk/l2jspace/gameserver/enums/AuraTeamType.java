package ru.privetdruk.l2jspace.gameserver.enums;

public enum AuraTeamType {
    NONE(0),
    BLUE(1),
    RED(2);

    private final int id;

    AuraTeamType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static AuraTeamType fromId(int id) {
        for (AuraTeamType auraTeamType : AuraTeamType.values()) {
            if (auraTeamType.getId() == id) {
                return auraTeamType;
            }
        }

        return null;
    }
}