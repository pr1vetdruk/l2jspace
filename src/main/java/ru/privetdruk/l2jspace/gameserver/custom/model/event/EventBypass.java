package ru.privetdruk.l2jspace.gameserver.custom.model.event;

public enum EventBypass {
    INFO("event_info"),
    JOIN("event_join"),
    JOIN_TEAM("event_join_team"),
    LEAVE("event_leave");
    
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
