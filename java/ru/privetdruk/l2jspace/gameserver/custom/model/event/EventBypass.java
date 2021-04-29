package ru.privetdruk.l2jspace.gameserver.custom.model.event;

public enum EventBypass {
    INFO("_event_info"),
    JOIN("_event_join"),
    JOIN_TEAM("_event_join_team "),
    LEAVE("_event_leave");
    
    private final String bypass;

    EventBypass(String bypass) {
        this.bypass = bypass;
    }

    public static EventBypass fromBypass(String bypass) {
        for (EventBypass eventBypass : EventBypass.values()) {
            if (eventBypass.getBypass().equals(bypass)) {
                return eventBypass;
            }
        }

        return null;
    }

    public String getBypass() {
        return bypass;
    }
}
