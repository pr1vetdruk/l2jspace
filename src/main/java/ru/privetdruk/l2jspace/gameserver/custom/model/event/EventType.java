package ru.privetdruk.l2jspace.gameserver.custom.model.event;

import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;

public enum EventType {
    NONE(null, false, null),
    CTF("Capture the Flag", true, ru.privetdruk.l2jspace.gameserver.custom.event.CTF.class),
    LAST_EMPEROR("Last Emperor", false, ru.privetdruk.l2jspace.gameserver.custom.event.LastEmperor.class),
    TVT("Team vs Team",true, ru.privetdruk.l2jspace.gameserver.custom.event.TVT.class),
    DM("Death Match",false, null);

    private final String name;
    private final boolean team;
    private final Class<? extends EventEngine> clazz;

    EventType(String name, boolean team, Class<? extends EventEngine> clazz) {
        this.name = name;
        this.team = team;
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public boolean isTeam() {
        return team;
    }

    public Class<? extends EventEngine> getClazz() {
        return clazz;
    }
}
