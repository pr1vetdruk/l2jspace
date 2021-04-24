package ru.privetdruk.l2jspace.config.custom.event;

public class EventConfig {
    private EventConfig() {
    }

    public static class Engine {
        public static final String EVENT_ENGINE_FILE = "./config/event/engine.properties";
        public static boolean ANNOUNCE_REWARD;
        public static boolean REGISTRATION_BY_COMMANDS;
        public static boolean LOG_STATISTICS;
        public static int WAIT_TELEPORT_SECONDS;
    }
}
