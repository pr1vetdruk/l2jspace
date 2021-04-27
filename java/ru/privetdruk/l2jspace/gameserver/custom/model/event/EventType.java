package ru.privetdruk.l2jspace.gameserver.custom.model.event;

import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;

public enum EventType {
    NONE(false, null),
    CTF(true, ru.privetdruk.l2jspace.gameserver.custom.event.CTF.class),
    TVT(true, null),
    DM(false, null);

    private final boolean team;
    private final Class<? extends EventEngine> clazz;

    EventType(boolean team, Class<? extends EventEngine> clazz) {
        this.team = team;
        this.clazz = clazz;
    }

    public boolean isTeam() {
        return team;
    }

    public Class<? extends EventEngine> getClazz() {
        return clazz;
    }
}
