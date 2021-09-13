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
        public static int DELAY_BEFORE_TELEPORT;
        public static int DELAY_BEFORE_START;
        public static int DELAY_BEFORE_TELEPORT_RETURN;
        public static boolean ALLOW_INTERFERENCE;
        public static boolean ALLOW_SUMMON;
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
        public static boolean ALLOW_POTIONS;
        public static boolean REMOVE_BUFFS_ON_DIE;
        public static int DELAY_BEFORE_REVIVE;
        public static boolean ANNOUNCE_TEAM_STATS;
    }

    public static class LastEmperor {
        public static final String PROPERTIES = "./config/custom/event/last-emperor.properties";
        public static boolean ENABLED;
        public static String[] LAUNCH_TIMES;
        public static int PREPARE_FOR_BATTLE;
        public static int TIME_TO_FIGHT;
        public static EventTeamType TEAM_MODE;
        public static boolean UNSUMMON_PET;
        public static boolean REMOVE_ALL_EFFECTS;
        public static boolean JOIN_CURSED_WEAPON;
        public static boolean REMOVE_BUFFS_ON_DIE;
    }
}
