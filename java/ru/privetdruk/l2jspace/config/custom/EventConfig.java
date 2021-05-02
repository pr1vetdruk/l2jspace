package ru.privetdruk.l2jspace.config.custom;

import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventLoadingMode;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventTeamType;

public class EventConfig {
    private EventConfig() {
    }

    public static class Engine {
        public static final String PROPERTIES = "./config/custom/event/engine.properties";
        public static boolean ANNOUNCE_REWARD;
        public static boolean REGISTRATION_BY_COMMANDS;
        public static boolean LOG_STATISTICS;
        public static int WAIT_TELEPORT_SECONDS;
    }

    public static class CTF {
        public static final String PROPERTIES = "./config/custom/event/ctf.properties";
        public static boolean ENABLED;
        public static boolean AURA;
        public static String[] LAUNCH_TIMES;
        public static EventLoadingMode LOADING_MODE;
        public static EventTeamType TEAM_MODE;
        public static boolean UNSUMMON_PET;
        public static boolean REMOVE_ALL_EFFECTS;
        public static boolean JOIN_CURSED_WEAPON;
    }
}
