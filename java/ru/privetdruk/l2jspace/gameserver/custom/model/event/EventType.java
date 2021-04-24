package ru.privetdruk.l2jspace.gameserver.custom.model.event;

import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventTeamType.NO;

public enum EventType {
    NONE(false),
    CTF(true),
    TVT(true),
    DM(false);

    private final boolean team;

    EventType(boolean team) {
        this.team = team;
    }

    public boolean isTeam() {
        return team;
    }
}
