package ru.privetdruk.l2jspace.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import ru.privetdruk.l2jspace.common.config.ExProperties;
import ru.privetdruk.l2jspace.common.logging.CLogger;
import ru.privetdruk.l2jspace.common.math.MathUtil;

import ru.privetdruk.l2jspace.config.custom.EventConfig;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventLoadingMode;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventTeamType;
import ru.privetdruk.l2jspace.gameserver.enums.GeoType;
import ru.privetdruk.l2jspace.gameserver.model.holder.IntIntHolder;
import ru.privetdruk.l2jspace.gameserver.model.olympiad.enums.OlympiadPeriod;

/**
 * This class contains global server configuration.<br>
 * It has static final fields initialized from configuration files.
 */
public final class Config {
    private static final CLogger LOGGER = new CLogger(Config.class.getName());

    public static final String CLAN_FILE = "./config/clan.properties";
    public static final String EVENT_FILE = "./config/event.properties";
    public static final String GEO_ENGINE_FILE = "./config/geo-engine.properties";
    public static final String HEX_ID_FILE = "./config/hexid.txt";
    public static final String LOGIN_SERVER_FILE = "./config/login-server.properties";
    public static final String NPC_FILE = "./config/npc.properties";
    public static final String OFFLINE_FILE = "./config/offline-trade.properties";
    public static final String PLAYER_FILE = "./config/player.properties";
    public static final String RATE_FILE = "./config/rate.properties";
    public static final String ADDON_FILE = "./config/addon.properties";
    public static final String GAME_SERVER_FILE = "./config/game-server.properties";
    public static final String SIEGE_FILE = "./config/siege.properties";

    public static final EventConfig.Engine EVENT_ENGINE = new EventConfig.Engine();


    /* ######################################################
                         Clans settings
       ###################################################### */

    // Clan
    public static int CLAN_JOIN_DAYS;
    public static int CLAN_CREATE_DAYS;
    public static int CLAN_DISSOLVE_DAYS;
    public static int ALLY_JOIN_DAYS_WHEN_LEAVED;
    public static int ALLY_JOIN_DAYS_WHEN_DISMISSED;
    public static int ACCEPT_CLAN_DAYS_WHEN_DISMISSED;
    public static int CREATE_ALLY_DAYS_WHEN_DISSOLVED;
    public static int MAX_NUM_OF_CLANS_IN_ALLY;
    public static int CLAN_MEMBERS_FOR_WAR;
    public static int CLAN_WAR_PENALTY_WHEN_ENDED;
    public static boolean MEMBERS_CAN_WITHDRAW_FROM_CLANWH;

    // Manor
    public static int MANOR_REFRESH_TIME;
    public static int MANOR_REFRESH_MIN;
    public static int MANOR_APPROVE_TIME;
    public static int MANOR_APPROVE_MIN;
    public static int MANOR_MAINTENANCE_MIN;
    public static int MANOR_SAVE_PERIOD_RATE;

    // Clan Hall function
    public static long CH_TELE_FEE_RATIO;
    public static int CH_TELE1_FEE;
    public static int CH_TELE2_FEE;
    public static long CH_ITEM_FEE_RATIO;
    public static int CH_ITEM1_FEE;
    public static int CH_ITEM2_FEE;
    public static int CH_ITEM3_FEE;
    public static long CH_MPREG_FEE_RATIO;
    public static int CH_MPREG1_FEE;
    public static int CH_MPREG2_FEE;
    public static int CH_MPREG3_FEE;
    public static int CH_MPREG4_FEE;
    public static int CH_MPREG5_FEE;
    public static long CH_HPREG_FEE_RATIO;
    public static int CH_HPREG1_FEE;
    public static int CH_HPREG2_FEE;
    public static int CH_HPREG3_FEE;
    public static int CH_HPREG4_FEE;
    public static int CH_HPREG5_FEE;
    public static int CH_HPREG6_FEE;
    public static int CH_HPREG7_FEE;
    public static int CH_HPREG8_FEE;
    public static int CH_HPREG9_FEE;
    public static int CH_HPREG10_FEE;
    public static int CH_HPREG11_FEE;
    public static int CH_HPREG12_FEE;
    public static int CH_HPREG13_FEE;
    public static long CH_EXPREG_FEE_RATIO;
    public static int CH_EXPREG1_FEE;
    public static int CH_EXPREG2_FEE;
    public static int CH_EXPREG3_FEE;
    public static int CH_EXPREG4_FEE;
    public static int CH_EXPREG5_FEE;
    public static int CH_EXPREG6_FEE;
    public static int CH_EXPREG7_FEE;
    public static long CH_SUPPORT_FEE_RATIO;
    public static int CH_SUPPORT1_FEE;
    public static int CH_SUPPORT2_FEE;
    public static int CH_SUPPORT3_FEE;
    public static int CH_SUPPORT4_FEE;
    public static int CH_SUPPORT5_FEE;
    public static int CH_SUPPORT6_FEE;
    public static int CH_SUPPORT7_FEE;
    public static int CH_SUPPORT8_FEE;
    public static long CH_CURTAIN_FEE_RATIO;
    public static int CH_CURTAIN1_FEE;
    public static int CH_CURTAIN2_FEE;
    public static long CH_FRONT_FEE_RATIO;
    public static int CH_FRONT1_FEE;
    public static int CH_FRONT2_FEE;


    /* ######################################################
                         Events settings
       ###################################################### */

    // Olympiad
    public static int OLY_START_TIME;
    public static int OLY_MIN;
    public static long OLY_CPERIOD;
    public static long OLY_BATTLE;
    public static long OLY_WPERIOD;
    public static long OLY_VPERIOD;
    public static int OLY_WAIT_TIME;
    public static int OLY_WAIT_BATTLE;
    public static int OLY_WAIT_END;
    public static int OLY_START_POINTS;
    public static int OLY_WEEKLY_POINTS;
    public static int OLY_MIN_MATCHES;
    public static int OLY_CLASSED;
    public static int OLY_NONCLASSED;
    public static IntIntHolder[] OLY_CLASSED_REWARD;
    public static IntIntHolder[] OLY_NONCLASSED_REWARD;
    public static int OLY_GP_PER_POINT;
    public static int OLY_HERO_POINTS;
    public static int OLY_RANK1_POINTS;
    public static int OLY_RANK2_POINTS;
    public static int OLY_RANK3_POINTS;
    public static int OLY_RANK4_POINTS;
    public static int OLY_RANK5_POINTS;
    public static int OLY_MAX_POINTS;
    public static int OLY_DIVIDER_CLASSED;
    public static int OLY_DIVIDER_NON_CLASSED;
    public static boolean OLY_ANNOUNCE_GAMES;
    public static int OLY_ENCHANT_LIMIT;

    // SevenSigns Festival
    public static boolean SEVEN_SIGNS_BYPASS_PREREQUISITES;
    public static int FESTIVAL_MIN_PLAYER;
    public static int MAXIMUM_PLAYER_CONTRIB;
    public static long FESTIVAL_MANAGER_START;
    public static long FESTIVAL_LENGTH;
    public static long FESTIVAL_CYCLE_LENGTH;
    public static long FESTIVAL_FIRST_SPAWN;
    public static long FESTIVAL_FIRST_SWARM;
    public static long FESTIVAL_SECOND_SPAWN;
    public static long FESTIVAL_SECOND_SWARM;
    public static long FESTIVAL_CHEST_SPAWN;

    // Four Sepulchers
    public static int FS_TIME_ENTRY;
    public static int FS_TIME_END;
    public static int FS_PARTY_MEMBER_COUNT;

    // Dimensional rift
    public static int RIFT_MIN_PARTY_SIZE;
    public static int RIFT_SPAWN_DELAY;
    public static int RIFT_MAX_JUMPS;
    public static int RIFT_AUTO_JUMPS_TIME_MIN;
    public static int RIFT_AUTO_JUMPS_TIME_MAX;
    public static int RIFT_ENTER_COST_RECRUIT;
    public static int RIFT_ENTER_COST_SOLDIER;
    public static int RIFT_ENTER_COST_OFFICER;
    public static int RIFT_ENTER_COST_CAPTAIN;
    public static int RIFT_ENTER_COST_COMMANDER;
    public static int RIFT_ENTER_COST_HERO;
    public static double RIFT_BOSS_ROOM_TIME_MUTIPLY;

    // Wedding system
    public static boolean ALLOW_WEDDING;
    public static int WEDDING_PRICE;
    public static boolean WEDDING_SAMESEX;
    public static boolean WEDDING_FORMALWEAR;

    // Lottery
    public static int LOTTERY_PRIZE;
    public static int LOTTERY_TICKET_PRICE;
    public static double LOTTERY_5_NUMBER_RATE;
    public static double LOTTERY_4_NUMBER_RATE;
    public static double LOTTERY_3_NUMBER_RATE;
    public static int LOTTERY_2_AND_1_NUMBER_PRIZE;

    // Fishing tournament
    public static boolean ALLOW_FISH_CHAMPIONSHIP;
    public static int FISH_CHAMPIONSHIP_REWARD_ITEM;
    public static int FISH_CHAMPIONSHIP_REWARD_1;
    public static int FISH_CHAMPIONSHIP_REWARD_2;
    public static int FISH_CHAMPIONSHIP_REWARD_3;
    public static int FISH_CHAMPIONSHIP_REWARD_4;
    public static int FISH_CHAMPIONSHIP_REWARD_5;


    /* ######################################################
                           Geo Engine
       ###################################################### */

    // Geodata
    public static String GEODATA_PATH;
    public static GeoType GEODATA_TYPE;

    // Path checking
    public static int PART_OF_CHARACTER_HEIGHT;
    public static int MAX_OBSTACLE_HEIGHT;

    // Path finding
    public static String PATH_FIND_BUFFERS;
    public static int MOVE_WEIGHT;
    public static int MOVE_WEIGHT_DIAG;
    public static int OBSTACLE_WEIGHT;
    public static int HEURISTIC_WEIGHT;
    public static int HEURISTIC_WEIGHT_DIAG;
    public static int MAX_ITERATIONS;
    public static boolean DEBUG_GEO_NODE;


    /* ######################################################
                             Hex ID
       ###################################################### */

    public static int SERVER_ID;
    public static byte[] HEX_ID;


    /* ######################################################
                          Login Server
       ###################################################### */

    public static String LOGINSERVER_HOSTNAME;
    public static int LOGINSERVER_PORT;
    public static int LOGIN_TRY_BEFORE_BAN;
    public static int LOGIN_BLOCK_AFTER_BAN;
    public static boolean ACCEPT_NEW_GAMESERVER;
    public static boolean SHOW_LICENCE;
    public static boolean AUTO_CREATE_ACCOUNTS;
    public static boolean FLOOD_PROTECTION;
    public static int FAST_CONNECTION_LIMIT;
    public static int NORMAL_CONNECTION_TIME;
    public static int FAST_CONNECTION_TIME;
    public static int MAX_CONNECTION_PER_IP;


    /* ######################################################
                          NPCs / Monsters
       ###################################################### */

    // Buffer
    public static int BUFFER_MAX_SCHEMES;
    public static int BUFFER_STATIC_BUFF_COST;

    // Class Master
    public static boolean ALLOW_CLASS_MASTERS;
    public static boolean ALLOW_ENTIRE_TREE;
    public static ClassMasterSettings CLASS_MASTER_SETTINGS;

    // Misc
    public static boolean FREE_TELEPORT;
    public static int LVL_FREE_TELEPORT;
    public static boolean ANNOUNCE_MAMMON_SPAWN;
    public static boolean MOB_AGGRO_IN_PEACEZONE;
    public static boolean SHOW_NPC_LVL;
    public static boolean SHOW_NPC_CREST;
    public static boolean SHOW_SUMMON_CREST;

    // Wyvern Manager
    public static boolean WYVERN_ALLOW_UPGRADER;
    public static int WYVERN_REQUIRED_LEVEL;
    public static int WYVERN_REQUIRED_CRYSTALS;

    // Raid Boss
    public static double RAID_HP_REGEN_MULTIPLIER;
    public static double RAID_MP_REGEN_MULTIPLIER;
    public static double RAID_DEFENCE_MULTIPLIER;
    public static int RAID_MINION_RESPAWN_TIMER;
    public static boolean RAID_DISABLE_CURSE;

    // Grand Boss
    public static int SPAWN_INTERVAL_AQ;
    public static int RANDOM_SPAWN_TIME_AQ;
    public static int SPAWN_INTERVAL_ANTHARAS;
    public static int RANDOM_SPAWN_TIME_ANTHARAS;
    public static int WAIT_TIME_ANTHARAS;
    public static int SPAWN_INTERVAL_BAIUM;
    public static int RANDOM_SPAWN_TIME_BAIUM;
    public static int SPAWN_INTERVAL_CORE;
    public static int RANDOM_SPAWN_TIME_CORE;
    public static int SPAWN_INTERVAL_FRINTEZZA;
    public static int RANDOM_SPAWN_TIME_FRINTEZZA;
    public static int WAIT_TIME_FRINTEZZA;
    public static int SPAWN_INTERVAL_ORFEN;
    public static int RANDOM_SPAWN_TIME_ORFEN;
    public static int SPAWN_INTERVAL_SAILREN;
    public static int RANDOM_SPAWN_TIME_SAILREN;
    public static int WAIT_TIME_SAILREN;
    public static int SPAWN_INTERVAL_VALAKAS;
    public static int RANDOM_SPAWN_TIME_VALAKAS;
    public static int WAIT_TIME_VALAKAS;
    public static int SPAWN_INTERVAL_ZAKEN;
    public static int RANDOM_SPAWN_TIME_ZAKEN;

    // AI
    public static boolean GUARD_ATTACK_AGGRO_MOB;
    public static int RANDOM_WALK_RATE;
    public static int MAX_DRIFT_RANGE;
    public static int MIN_NPC_ANIMATION;
    public static int MAX_NPC_ANIMATION;
    public static int MIN_MONSTER_ANIMATION;
    public static int MAX_MONSTER_ANIMATION;


    /* ######################################################
                             Offline
       ###################################################### */

    public static boolean OFFLINE_TRADE_ENABLE;
    public static boolean OFFLINE_CRAFT_ENABLE;
    public static boolean OFFLINE_MODE_IN_PEACE_ZONE;
    public static boolean OFFLINE_MODE_NO_DAMAGE;
    public static boolean RESTORE_OFFLINERS;
    public static int OFFLINE_MAX_DAYS;
    public static boolean OFFLINE_DISCONNECT_FINISHED;
    public static boolean OFFLINE_SLEEP_EFFECT;


    /* ######################################################
                             Players
       ###################################################### */

    // Misc
    public static boolean EFFECT_CANCELING;
    public static double HP_REGEN_MULTIPLIER;
    public static double MP_REGEN_MULTIPLIER;
    public static double CP_REGEN_MULTIPLIER;
    public static int PLAYER_SPAWN_PROTECTION;
    public static int PLAYER_FAKEDEATH_UP_PROTECTION;
    public static double RESPAWN_RESTORE_HP;
    public static int MAX_PVTSTORE_SLOTS_DWARF;
    public static int MAX_PVTSTORE_SLOTS_OTHER;
    public static boolean DEEPBLUE_DROP_RULES;
    public static boolean ALLOW_DELEVEL;
    public static int DEATH_PENALTY_CHANCE;

    // Inventory & WH
    public static int INVENTORY_MAXIMUM_NO_DWARF;
    public static int INVENTORY_MAXIMUM_DWARF;
    public static int INVENTORY_MAXIMUM_PET;
    public static int MAX_ITEM_IN_PACKET;
    public static double WEIGHT_LIMIT;
    public static int WAREHOUSE_SLOTS_NO_DWARF;
    public static int WAREHOUSE_SLOTS_DWARF;
    public static int WAREHOUSE_SLOTS_CLAN;
    public static int FREIGHT_SLOTS;
    public static boolean REGION_BASED_FREIGHT;
    public static int FREIGHT_PRICE;

    // Enchant
    public static double ENCHANT_CHANCE_WEAPON_MAGIC;
    public static double ENCHANT_CHANCE_WEAPON_MAGIC_15PLUS;
    public static double ENCHANT_CHANCE_WEAPON_NONMAGIC;
    public static double ENCHANT_CHANCE_WEAPON_NONMAGIC_15PLUS;
    public static double ENCHANT_CHANCE_ARMOR;
    public static int ENCHANT_MAX_WEAPON;
    public static int ENCHANT_MAX_ARMOR;
    public static int ENCHANT_SAFE_MAX;
    public static int ENCHANT_SAFE_MAX_FULL;

    // Augmentations
    public static int AUGMENTATION_NG_SKILL_CHANCE;
    public static int AUGMENTATION_NG_GLOW_CHANCE;
    public static int AUGMENTATION_MID_SKILL_CHANCE;
    public static int AUGMENTATION_MID_GLOW_CHANCE;
    public static int AUGMENTATION_HIGH_SKILL_CHANCE;
    public static int AUGMENTATION_HIGH_GLOW_CHANCE;
    public static int AUGMENTATION_TOP_SKILL_CHANCE;
    public static int AUGMENTATION_TOP_GLOW_CHANCE;
    public static int AUGMENTATION_BASESTAT_CHANCE;

    // Karma & PvP
    public static boolean KARMA_PLAYER_CAN_BE_KILLED_IN_PZ;
    public static boolean KARMA_PLAYER_CAN_SHOP;
    public static boolean KARMA_PLAYER_CAN_USE_GK;
    public static boolean KARMA_PLAYER_CAN_TELEPORT;
    public static boolean KARMA_PLAYER_CAN_TRADE;
    public static boolean KARMA_PLAYER_CAN_USE_WH;
    public static boolean KARMA_DROP_GM;
    public static boolean KARMA_AWARD_PK_KILL;
    public static int KARMA_PK_LIMIT;
    public static int[] KARMA_NONDROPPABLE_PET_ITEMS;
    public static int[] KARMA_NONDROPPABLE_ITEMS;
    public static int PVP_NORMAL_TIME;
    public static int PVP_PVP_TIME;

    // Party
    public static String PARTY_XP_CUTOFF_METHOD;
    public static int PARTY_XP_CUTOFF_LEVEL;
    public static double PARTY_XP_CUTOFF_PERCENT;
    public static int PARTY_RANGE;

    // GMs & Admin Stuff
    public static int DEFAULT_ACCESS_LEVEL;
    public static boolean GM_HERO_AURA;
    public static boolean GM_STARTUP_INVULNERABLE;
    public static boolean GM_STARTUP_INVISIBLE;
    public static boolean GM_STARTUP_BLOCK_ALL;
    public static boolean GM_STARTUP_AUTO_LIST;

    // Petitions
    public static boolean PETITIONING_ALLOWED;
    public static int MAX_PETITIONS_PER_PLAYER;
    public static int MAX_PETITIONS_PENDING;

    // Crafting
    public static boolean IS_CRAFTING_ENABLED;
    public static int DWARF_RECIPE_LIMIT;
    public static int COMMON_RECIPE_LIMIT;
    public static boolean BLACKSMITH_USE_RECIPES;

    // Skills & Classes
    public static boolean AUTO_LEARN_SKILLS;
    public static int LVL_AUTO_LEARN_SKILLS;
    public static boolean MAGIC_FAILURES;
    public static int PERFECT_SHIELD_BLOCK_RATE;
    public static boolean LIFE_CRYSTAL_NEEDED;
    public static boolean SP_BOOK_NEEDED;
    public static boolean ES_SP_BOOK_NEEDED;
    public static boolean DIVINE_SP_BOOK_NEEDED;
    public static boolean SUBCLASS_WITHOUT_QUESTS;

    // Buffs
    public static boolean STORE_SKILL_COOLTIME;
    public static int MAX_BUFFS_AMOUNT;


    /* ######################################################
                             Sieges
       ###################################################### */

    public static int SIEGE_LENGTH;
    public static int MINIMUM_CLAN_LEVEL;
    public static int MAX_ATTACKERS_NUMBER;
    public static int MAX_DEFENDERS_NUMBER;
    public static int ATTACKERS_RESPAWN_DELAY;
    public static int CH_MINIMUM_CLAN_LEVEL;
    public static int CH_MAX_ATTACKERS_NUMBER;


    /* ######################################################
                           Game Server
       ###################################################### */

    public static String HOSTNAME;
    public static String GAME_SERVER_HOSTNAME;
    public static int GAME_SERVER_PORT;
    public static String GAME_SERVER_LOGIN_HOSTNAME;
    public static int GAME_SERVER_LOGIN_PORT;
    public static int REQUEST_ID;
    public static boolean ACCEPT_ALTERNATE_ID;
    public static boolean USE_BLOWFISH_CIPHER;

    // Access to database
    public static String DATABASE_URL;
    public static String DATABASE_LOGIN;
    public static String DATABASE_PASSWORD;
    public static int DATABASE_MAX_CONNECTIONS;

    // ServerList & Test
    public static boolean SERVER_LIST_BRACKET;
    public static boolean SERVER_LIST_CLOCK;
    public static int SERVER_LIST_AGE;
    public static boolean SERVER_LIST_TEST_SERVER;
    public static boolean SERVER_LIST_PVP_SERVER;
    public static boolean SERVER_GM_ONLY;

    // Clients related
    public static int DELETE_DAYS;
    public static int MAXIMUM_ONLINE_USERS;

    // Auto-loot
    public static boolean AUTO_LOOT;
    public static boolean AUTO_LOOT_HERBS;
    public static boolean AUTO_LOOT_RAID;

    // Items Management
    public static boolean ALLOW_DISCARD_ITEM;
    public static boolean MULTIPLE_ITEM_DROP;
    public static int HERB_AUTO_DESTROY_TIME;
    public static int ITEM_AUTO_DESTROY_TIME;
    public static int EQUIPABLE_ITEM_AUTO_DESTROY_TIME;
    public static Map<Integer, Integer> SPECIAL_ITEM_DESTROY_TIME;
    public static int PLAYER_DROPPED_ITEM_MULTIPLIER;

    // Rate control
    public static double RATE_XP;
    public static double RATE_SP;
    public static double RATE_PARTY_XP;
    public static double RATE_PARTY_SP;
    public static double RATE_DROP_ADENA;
    public static double RATE_DROP_ITEMS;
    public static double RATE_DROP_ITEMS_BY_RAID;
    public static double RATE_DROP_ITEMS_BY_GRAND;
    public static double RATE_DROP_SPOIL;
    public static int RATE_DROP_MANOR;
    public static double RATE_QUEST_DROP;
    public static double RATE_QUEST_REWARD;
    public static double RATE_QUEST_REWARD_XP;
    public static double RATE_QUEST_REWARD_SP;
    public static double RATE_QUEST_REWARD_ADENA;
    public static double RATE_KARMA_EXP_LOST;
    public static double RATE_SIEGE_GUARDS_PRICE;
    public static int PLAYER_DROP_LIMIT;
    public static int PLAYER_RATE_DROP;
    public static int PLAYER_RATE_DROP_ITEM;
    public static int PLAYER_RATE_DROP_EQUIP;
    public static int PLAYER_RATE_DROP_EQUIP_WEAPON;
    public static int KARMA_DROP_LIMIT;
    public static int KARMA_RATE_DROP;
    public static int KARMA_RATE_DROP_ITEM;
    public static int KARMA_RATE_DROP_EQUIP;
    public static int KARMA_RATE_DROP_EQUIP_WEAPON;
    public static double PET_XP_RATE;
    public static int PET_FOOD_RATE;
    public static double SINEATER_XP_RATE;
    public static double RATE_DROP_COMMON_HERBS;
    public static double RATE_DROP_HP_HERBS;
    public static double RATE_DROP_MP_HERBS;
    public static double RATE_DROP_SPECIAL_HERBS;

    // Allow types
    public static boolean ALLOW_FREIGHT;
    public static boolean ALLOW_WAREHOUSE;
    public static boolean ALLOW_WEAR;
    public static int WEAR_DELAY;
    public static int WEAR_PRICE;
    public static boolean ALLOW_LOTTERY;
    public static boolean ALLOW_WATER;
    public static boolean ALLOW_BOAT;
    public static boolean ALLOW_CURSED_WEAPONS;
    public static boolean ALLOW_MANOR;
    public static boolean ENABLE_FALLING_DAMAGE;

    // Debug & Dev
    public static boolean NO_SPAWNS;
    public static boolean DEVELOPER;
    public static boolean PACKET_HANDLER_DEBUG;

    // Deadlock Detector
    public static boolean DEADLOCK_DETECTOR;
    public static int DEADLOCK_CHECK_INTERVAL;
    public static boolean RESTART_ON_DEADLOCK;

    // Logs
    public static boolean LOG_CHAT;
    public static boolean LOG_ITEMS;
    public static boolean GM_AUDIT;

    // Community Board
    public static boolean ENABLE_COMMUNITY_BOARD;
    public static String BBS_DEFAULT;

    // Flood Protectors
    public static int ROLL_DICE_TIME;
    public static int HERO_VOICE_TIME;
    public static int SUBCLASS_TIME;
    public static int DROP_ITEM_TIME;
    public static int SERVER_BYPASS_TIME;
    public static int MULTISELL_TIME;
    public static int MANUFACTURE_TIME;
    public static int MANOR_TIME;
    public static int SENDMAIL_TIME;
    public static int CHARACTER_SELECT_TIME;
    public static int GLOBAL_CHAT_TIME;
    public static int TRADE_CHAT_TIME;
    public static int SOCIAL_TIME;
    public static int MOVE_TIME;

    // ThreadPool
    public static int SCHEDULED_THREAD_POOL_COUNT;
    public static int THREADS_PER_SCHEDULED_THREAD_POOL;
    public static int INSTANT_THREAD_POOL_COUNT;
    public static int THREADS_PER_INSTANT_THREAD_POOL;

    // Misc
    public static boolean L2WALKER_PROTECTION;
    public static boolean SERVER_NEWS;
    public static int ZONE_TOWN;

    
    /* ###############################################################################
       Those "hidden" settings haven't configs to avoid admins to fuck their server.
       You still can experiment changing values here. But don't say I didn't warn you.
       ############################################################################### */

    // Reserve Host on LoginServerThread
    public static boolean RESERVE_HOST_ON_LOGIN = false;

    // MMO settings
    public static int MMO_SELECTOR_SLEEP_TIME = 20; // default 20
    public static int MMO_MAX_SEND_PER_PASS = 80; // default 80
    public static int MMO_MAX_READ_PER_PASS = 80; // default 80
    public static int MMO_HELPER_BUFFER_COUNT = 20; // default 20

    // Client Packets Queue settings
    public static int CLIENT_PACKET_QUEUE_SIZE = MMO_MAX_READ_PER_PASS + 2; // default MMO_MAX_READ_PER_PASS + 2
    public static int CLIENT_PACKET_QUEUE_MAX_BURST_SIZE = MMO_MAX_READ_PER_PASS + 1; // default MMO_MAX_READ_PER_PASS + 1
    public static int CLIENT_PACKET_QUEUE_MAX_PACKETS_PER_SECOND = 160; // default 160
    public static int CLIENT_PACKET_QUEUE_MEASURE_INTERVAL = 5; // default 5
    public static int CLIENT_PACKET_QUEUE_MAX_AVERAGE_PACKETS_PER_SECOND = 80; // default 80
    public static int CLIENT_PACKET_QUEUE_MAX_FLOODS_PER_MIN = 2; // default 2
    public static int CLIENT_PACKET_QUEUE_MAX_OVERFLOWS_PER_MIN = 1; // default 1
    public static int CLIENT_PACKET_QUEUE_MAX_UNDER_FLOWS_PER_MIN = 1; // default 1
    public static int CLIENT_PACKET_QUEUE_MAX_UNKNOWN_PER_MIN = 5; // default 5
    
    
    /* ######################################################
                             Addons
       ###################################################### */

    // Infinity SS and Arrows
    public static boolean INFINITY_SS;
    public static boolean INFINITY_ARROWS;

    // Olympiad Period
    public static OlympiadPeriod OLY_PERIOD;
    public static int OLY_PERIOD_MULTIPLIER;

    public static boolean ENABLE_MODIFY_SKILL_DURATION;
    public static HashMap<Integer, Integer> SKILL_DURATION_LIST;
    public static String GLOBAL_CHAT;
    public static String TRADE_CHAT;
    public static int CHAT_ALL_LEVEL;
    public static int CHAT_TELL_LEVEL;
    public static int CHAT_SHOUT_LEVEL;
    public static int CHAT_TRADE_LEVEL;
    public static boolean ENABLE_MENU;
    public static boolean ENABLE_ONLINE_COMMAND;
    public static boolean BOTS_PREVENTION;
    public static int KILLS_COUNTER;
    public static int KILLS_COUNTER_RANDOMIZATION;
    public static int VALIDATION_TIME;
    public static int PUNISHMENT;
    public static boolean USE_PREMIUM_SERVICE;
    public static double PREMIUM_RATE_XP;
    public static double PREMIUM_RATE_SP;
    public static double PREMIUM_RATE_DROP_ADENA;
    public static double PREMIUM_RATE_DROP_SPOIL;
    public static double PREMIUM_RATE_DROP_ITEMS;
    public static double PREMIUM_RATE_DROP_ITEMS_BY_RAID;
    public static boolean ATTACK_PTS;
    public static boolean SUBCLASS_SKILLS;
    public static boolean GAME_SUBCLASS_EVERYWHERE;
    public static boolean SHOW_NPC_INFO;
    public static boolean ALLOW_GRAND_BOSSES_TELEPORT;


    /**
     * Initialize {@link ExProperties} from specified configuration file.
     *
     * @param filename : File name to be loaded.
     * @return ExProperties : Initialized {@link ExProperties}.
     */
    public static ExProperties initProperties(String filename) {
        ExProperties result = new ExProperties();

        try {
            result.load(new File(filename));
        } catch (Exception e) {
            LOGGER.error("An error occured loading '{}' config.", e, filename);
        }

        return result;
    }

    /**
     * Loads offline shop settings
     */
    private static void loadOfflineShop() {
        ExProperties offline = initProperties(OFFLINE_FILE);

        OFFLINE_TRADE_ENABLE = offline.getProperty("OfflineTradeEnable", false);
        OFFLINE_CRAFT_ENABLE = offline.getProperty("OfflineCraftEnable", false);
        OFFLINE_MODE_IN_PEACE_ZONE = offline.getProperty("OfflineModeInPeaceZone", false);
        OFFLINE_MODE_NO_DAMAGE = offline.getProperty("OfflineModeNoDamage", false);
        RESTORE_OFFLINERS = offline.getProperty("RestoreOffliners", false);
        OFFLINE_MAX_DAYS = offline.getProperty("OfflineMaxDays", 10);
        OFFLINE_DISCONNECT_FINISHED = offline.getProperty("OfflineDisconnectFinished", true);
        OFFLINE_SLEEP_EFFECT = offline.getProperty("OfflineSleepEffect", true);
    }

    /**
     * Loads clan and clan hall settings.
     */
    private static void loadClans() {
        ExProperties clans = initProperties(CLAN_FILE);

        CLAN_JOIN_DAYS = clans.getProperty("DaysBeforeJoinAClan", 5);
        CLAN_CREATE_DAYS = clans.getProperty("DaysBeforeCreateAClan", 10);
        MAX_NUM_OF_CLANS_IN_ALLY = clans.getProperty("MaxNumOfClansInAlly", 3);
        CLAN_MEMBERS_FOR_WAR = clans.getProperty("ClanMembersForWar", 15);
        CLAN_WAR_PENALTY_WHEN_ENDED = clans.getProperty("ClanWarPenaltyWhenEnded", 5);
        CLAN_DISSOLVE_DAYS = clans.getProperty("DaysToPassToDissolveAClan", 7);
        ALLY_JOIN_DAYS_WHEN_LEAVED = clans.getProperty("DaysBeforeJoinAllyWhenLeaved", 1);
        ALLY_JOIN_DAYS_WHEN_DISMISSED = clans.getProperty("DaysBeforeJoinAllyWhenDismissed", 1);
        ACCEPT_CLAN_DAYS_WHEN_DISMISSED = clans.getProperty("DaysBeforeAcceptNewClanWhenDismissed", 1);
        CREATE_ALLY_DAYS_WHEN_DISSOLVED = clans.getProperty("DaysBeforeCreateNewAllyWhenDissolved", 10);
        MEMBERS_CAN_WITHDRAW_FROM_CLANWH = clans.getProperty("MembersCanWithdrawFromClanWH", false);

        MANOR_REFRESH_TIME = clans.getProperty("ManorRefreshTime", 20);
        MANOR_REFRESH_MIN = clans.getProperty("ManorRefreshMin", 0);
        MANOR_APPROVE_TIME = clans.getProperty("ManorApproveTime", 6);
        MANOR_APPROVE_MIN = clans.getProperty("ManorApproveMin", 0);
        MANOR_MAINTENANCE_MIN = clans.getProperty("ManorMaintenanceMin", 6);
        MANOR_SAVE_PERIOD_RATE = clans.getProperty("ManorSavePeriodRate", 2) * 3600000;

        CH_TELE_FEE_RATIO = clans.getProperty("ClanHallTeleportFunctionFeeRatio", 86400000);
        CH_TELE1_FEE = clans.getProperty("ClanHallTeleportFunctionFeeLvl1", 7000);
        CH_TELE2_FEE = clans.getProperty("ClanHallTeleportFunctionFeeLvl2", 14000);
        CH_SUPPORT_FEE_RATIO = clans.getProperty("ClanHallSupportFunctionFeeRatio", 86400000);
        CH_SUPPORT1_FEE = clans.getProperty("ClanHallSupportFeeLvl1", 17500);
        CH_SUPPORT2_FEE = clans.getProperty("ClanHallSupportFeeLvl2", 35000);
        CH_SUPPORT3_FEE = clans.getProperty("ClanHallSupportFeeLvl3", 49000);
        CH_SUPPORT4_FEE = clans.getProperty("ClanHallSupportFeeLvl4", 77000);
        CH_SUPPORT5_FEE = clans.getProperty("ClanHallSupportFeeLvl5", 147000);
        CH_SUPPORT6_FEE = clans.getProperty("ClanHallSupportFeeLvl6", 252000);
        CH_SUPPORT7_FEE = clans.getProperty("ClanHallSupportFeeLvl7", 259000);
        CH_SUPPORT8_FEE = clans.getProperty("ClanHallSupportFeeLvl8", 364000);
        CH_MPREG_FEE_RATIO = clans.getProperty("ClanHallMpRegenerationFunctionFeeRatio", 86400000);
        CH_MPREG1_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl1", 14000);
        CH_MPREG2_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl2", 26250);
        CH_MPREG3_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl3", 45500);
        CH_MPREG4_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl4", 96250);
        CH_MPREG5_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl5", 140000);
        CH_HPREG_FEE_RATIO = clans.getProperty("ClanHallHpRegenerationFunctionFeeRatio", 86400000);
        CH_HPREG1_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl1", 4900);
        CH_HPREG2_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl2", 5600);
        CH_HPREG3_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl3", 7000);
        CH_HPREG4_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl4", 8166);
        CH_HPREG5_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl5", 10500);
        CH_HPREG6_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl6", 12250);
        CH_HPREG7_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl7", 14000);
        CH_HPREG8_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl8", 15750);
        CH_HPREG9_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl9", 17500);
        CH_HPREG10_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl10", 22750);
        CH_HPREG11_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl11", 26250);
        CH_HPREG12_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl12", 29750);
        CH_HPREG13_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl13", 36166);
        CH_EXPREG_FEE_RATIO = clans.getProperty("ClanHallExpRegenerationFunctionFeeRatio", 86400000);
        CH_EXPREG1_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl1", 21000);
        CH_EXPREG2_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl2", 42000);
        CH_EXPREG3_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl3", 63000);
        CH_EXPREG4_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl4", 105000);
        CH_EXPREG5_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl5", 147000);
        CH_EXPREG6_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl6", 163331);
        CH_EXPREG7_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl7", 210000);
        CH_ITEM_FEE_RATIO = clans.getProperty("ClanHallItemCreationFunctionFeeRatio", 86400000);
        CH_ITEM1_FEE = clans.getProperty("ClanHallItemCreationFunctionFeeLvl1", 210000);
        CH_ITEM2_FEE = clans.getProperty("ClanHallItemCreationFunctionFeeLvl2", 490000);
        CH_ITEM3_FEE = clans.getProperty("ClanHallItemCreationFunctionFeeLvl3", 980000);
        CH_CURTAIN_FEE_RATIO = clans.getProperty("ClanHallCurtainFunctionFeeRatio", 86400000);
        CH_CURTAIN1_FEE = clans.getProperty("ClanHallCurtainFunctionFeeLvl1", 2002);
        CH_CURTAIN2_FEE = clans.getProperty("ClanHallCurtainFunctionFeeLvl2", 2625);
        CH_FRONT_FEE_RATIO = clans.getProperty("ClanHallFrontPlatformFunctionFeeRatio", 86400000);
        CH_FRONT1_FEE = clans.getProperty("ClanHallFrontPlatformFunctionFeeLvl1", 3031);
        CH_FRONT2_FEE = clans.getProperty("ClanHallFrontPlatformFunctionFeeLvl2", 9331);
    }

    /**
     * Loads event settings.<br>
     * Such as olympiad, seven signs festival, four sepulchures, dimensional rift, weddings, lottery, fishing championship.
     */
    private static void loadEvents() {
        ExProperties events = initProperties(EVENT_FILE);

        OLY_START_TIME = events.getProperty("OlyStartTime", 18);
        OLY_MIN = events.getProperty("OlyMin", 0);
        OLY_CPERIOD = events.getProperty("OlyCPeriod", 21600000);
        OLY_BATTLE = events.getProperty("OlyBattle", 180000);
        OLY_WPERIOD = events.getProperty("OlyWPeriod", 604800000);
        OLY_VPERIOD = events.getProperty("OlyVPeriod", 86400000);
        OLY_WAIT_TIME = events.getProperty("OlyWaitTime", 30);
        OLY_WAIT_BATTLE = events.getProperty("OlyWaitBattle", 60);
        OLY_WAIT_END = events.getProperty("OlyWaitEnd", 40);
        OLY_START_POINTS = events.getProperty("OlyStartPoints", 18);
        OLY_WEEKLY_POINTS = events.getProperty("OlyWeeklyPoints", 3);
        OLY_MIN_MATCHES = events.getProperty("OlyMinMatchesToBeClassed", 5);
        OLY_CLASSED = events.getProperty("OlyClassedParticipants", 5);
        OLY_NONCLASSED = events.getProperty("OlyNonClassedParticipants", 9);
        OLY_CLASSED_REWARD = events.parseIntIntList("OlyClassedReward", "6651-50");
        OLY_NONCLASSED_REWARD = events.parseIntIntList("OlyNonClassedReward", "6651-30");
        OLY_GP_PER_POINT = events.getProperty("OlyGPPerPoint", 1000);
        OLY_HERO_POINTS = events.getProperty("OlyHeroPoints", 300);
        OLY_RANK1_POINTS = events.getProperty("OlyRank1Points", 100);
        OLY_RANK2_POINTS = events.getProperty("OlyRank2Points", 75);
        OLY_RANK3_POINTS = events.getProperty("OlyRank3Points", 55);
        OLY_RANK4_POINTS = events.getProperty("OlyRank4Points", 40);
        OLY_RANK5_POINTS = events.getProperty("OlyRank5Points", 30);
        OLY_MAX_POINTS = events.getProperty("OlyMaxPoints", 10);
        OLY_DIVIDER_CLASSED = events.getProperty("OlyDividerClassed", 3);
        OLY_DIVIDER_NON_CLASSED = events.getProperty("OlyDividerNonClassed", 5);
        OLY_ANNOUNCE_GAMES = events.getProperty("OlyAnnounceGames", true);
        OLY_ENCHANT_LIMIT = events.getProperty("OlyMaxEnchant", -1);

        SEVEN_SIGNS_BYPASS_PREREQUISITES = events.getProperty("SevenSignsBypassPrerequisites", false);
        FESTIVAL_MIN_PLAYER = MathUtil.limit(events.getProperty("FestivalMinPlayer", 5), 2, 9);
        MAXIMUM_PLAYER_CONTRIB = events.getProperty("MaxPlayerContrib", 1000000);
        FESTIVAL_MANAGER_START = events.getProperty("FestivalManagerStart", 120000);
        FESTIVAL_LENGTH = events.getProperty("FestivalLength", 1080000);
        FESTIVAL_CYCLE_LENGTH = events.getProperty("FestivalCycleLength", 2280000);
        FESTIVAL_FIRST_SPAWN = events.getProperty("FestivalFirstSpawn", 120000);
        FESTIVAL_FIRST_SWARM = events.getProperty("FestivalFirstSwarm", 300000);
        FESTIVAL_SECOND_SPAWN = events.getProperty("FestivalSecondSpawn", 540000);
        FESTIVAL_SECOND_SWARM = events.getProperty("FestivalSecondSwarm", 720000);
        FESTIVAL_CHEST_SPAWN = events.getProperty("FestivalChestSpawn", 900000);

        FS_TIME_ENTRY = events.getProperty("EntryTime", 55);
        FS_TIME_END = events.getProperty("EndTime", 50);
        FS_PARTY_MEMBER_COUNT = MathUtil.limit(events.getProperty("NeededPartyMembers", 4), 2, 9);

        RIFT_MIN_PARTY_SIZE = events.getProperty("RiftMinPartySize", 2);
        RIFT_MAX_JUMPS = events.getProperty("MaxRiftJumps", 4);
        RIFT_SPAWN_DELAY = events.getProperty("RiftSpawnDelay", 10000);
        RIFT_AUTO_JUMPS_TIME_MIN = events.getProperty("AutoJumpsDelayMin", 480);
        RIFT_AUTO_JUMPS_TIME_MAX = events.getProperty("AutoJumpsDelayMax", 600);
        RIFT_ENTER_COST_RECRUIT = events.getProperty("RecruitCost", 18);
        RIFT_ENTER_COST_SOLDIER = events.getProperty("SoldierCost", 21);
        RIFT_ENTER_COST_OFFICER = events.getProperty("OfficerCost", 24);
        RIFT_ENTER_COST_CAPTAIN = events.getProperty("CaptainCost", 27);
        RIFT_ENTER_COST_COMMANDER = events.getProperty("CommanderCost", 30);
        RIFT_ENTER_COST_HERO = events.getProperty("HeroCost", 33);
        RIFT_BOSS_ROOM_TIME_MUTIPLY = events.getProperty("BossRoomTimeMultiply", 1.);

        ALLOW_WEDDING = events.getProperty("AllowWedding", false);
        WEDDING_PRICE = events.getProperty("WeddingPrice", 1000000);
        WEDDING_SAMESEX = events.getProperty("WeddingAllowSameSex", false);
        WEDDING_FORMALWEAR = events.getProperty("WeddingFormalWear", true);

        LOTTERY_PRIZE = events.getProperty("LotteryPrize", 50000);
        LOTTERY_TICKET_PRICE = events.getProperty("LotteryTicketPrice", 2000);
        LOTTERY_5_NUMBER_RATE = events.getProperty("Lottery5NumberRate", 0.6);
        LOTTERY_4_NUMBER_RATE = events.getProperty("Lottery4NumberRate", 0.2);
        LOTTERY_3_NUMBER_RATE = events.getProperty("Lottery3NumberRate", 0.2);
        LOTTERY_2_AND_1_NUMBER_PRIZE = events.getProperty("Lottery2and1NumberPrize", 200);

        ALLOW_FISH_CHAMPIONSHIP = events.getProperty("AllowFishChampionship", true);
        FISH_CHAMPIONSHIP_REWARD_ITEM = events.getProperty("FishChampionshipRewardItemId", 57);
        FISH_CHAMPIONSHIP_REWARD_1 = events.getProperty("FishChampionshipReward1", 800000);
        FISH_CHAMPIONSHIP_REWARD_2 = events.getProperty("FishChampionshipReward2", 500000);
        FISH_CHAMPIONSHIP_REWARD_3 = events.getProperty("FishChampionshipReward3", 300000);
        FISH_CHAMPIONSHIP_REWARD_4 = events.getProperty("FishChampionshipReward4", 200000);
        FISH_CHAMPIONSHIP_REWARD_5 = events.getProperty("FishChampionshipReward5", 100000);
    }

    /**
     * Loads GeoEngine settings.
     */
    private static void loadGeoEngine() {
        ExProperties geoEngine = initProperties(GEO_ENGINE_FILE);

        GEODATA_PATH = geoEngine.getProperty("GeoDataPath", "./data/geodata/");
        GEODATA_TYPE = Enum.valueOf(GeoType.class, geoEngine.getProperty("GeoDataType", "L2OFF"));

        PART_OF_CHARACTER_HEIGHT = geoEngine.getProperty("PartOfCharacterHeight", 75);
        MAX_OBSTACLE_HEIGHT = geoEngine.getProperty("MaxObstacleHeight", 32);

        PATH_FIND_BUFFERS = geoEngine.getProperty("PathFindBuffers", "500x10;1000x10;3000x5;5000x3;10000x3");
        MOVE_WEIGHT = geoEngine.getProperty("MoveWeight", 10);
        MOVE_WEIGHT_DIAG = geoEngine.getProperty("MoveWeightDiag", 14);
        OBSTACLE_WEIGHT = geoEngine.getProperty("ObstacleWeight", 30);
        HEURISTIC_WEIGHT = geoEngine.getProperty("HeuristicWeight", 12);
        HEURISTIC_WEIGHT_DIAG = geoEngine.getProperty("HeuristicWeightDiag", 18);
        MAX_ITERATIONS = geoEngine.getProperty("MaxIterations", 3500);
        DEBUG_GEO_NODE = geoEngine.getProperty("DebugGeoNode", false);
    }

    /**
     * Loads hex ID settings.
     */
    private static void loadHexId() {
        ExProperties hexId = initProperties(HEX_ID_FILE);

        SERVER_ID = Integer.parseInt(hexId.getProperty("ServerID"));
        HEX_ID = new BigInteger(hexId.getProperty("HexID"), 16).toByteArray();
    }

    /**
     * Saves hex ID file.
     *
     * @param serverId : The ID of server.
     * @param hexId    : The hex ID of server.
     */
    public static void saveHexId(int serverId, String hexId) {
        saveHexId(serverId, hexId, HEX_ID_FILE);
    }

    /**
     * Saves hexID file.
     *
     * @param serverId : The ID of server.
     * @param hexId    : The hexID of server.
     * @param filename : The file name.
     */
    public static void saveHexId(int serverId, String hexId, String filename) {
        try {
            final File file = new File(filename);
            file.createNewFile();

            final Properties hexSetting = new Properties();
            hexSetting.setProperty("ServerID", String.valueOf(serverId));
            hexSetting.setProperty("HexID", hexId);

            try (OutputStream out = new FileOutputStream(file)) {
                hexSetting.store(out, "the hexID to auth into login");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to save hex ID to '{}' file.", e, filename);
        }
    }

    /**
     * Loads NPC settings.<br>
     * Such as champion monsters, NPC buffer, class master, wyvern, raid bosses and grand bosses, AI.
     */
    private static void loadNpc() {
        ExProperties npc = initProperties(NPC_FILE);

        BUFFER_MAX_SCHEMES = npc.getProperty("BufferMaxSchemesPerChar", 4);
        BUFFER_STATIC_BUFF_COST = npc.getProperty("BufferStaticCostPerBuff", -1);
        ALLOW_CLASS_MASTERS = npc.getProperty("AllowClassMasters", false);
        ALLOW_ENTIRE_TREE = npc.getProperty("AllowEntireTree", false);
        if (ALLOW_CLASS_MASTERS) {
            CLASS_MASTER_SETTINGS = new ClassMasterSettings(npc.getProperty("ConfigClassMaster"));
        }
        FREE_TELEPORT = npc.getProperty("FreeTeleport", false);
        LVL_FREE_TELEPORT = npc.getProperty("LvlFreeTeleport", 40);
        ANNOUNCE_MAMMON_SPAWN = npc.getProperty("AnnounceMammonSpawn", true);
        MOB_AGGRO_IN_PEACEZONE = npc.getProperty("MobAggroInPeaceZone", true);
        SHOW_NPC_LVL = npc.getProperty("ShowNpcLevel", false);
        SHOW_NPC_CREST = npc.getProperty("ShowNpcCrest", false);
        SHOW_SUMMON_CREST = npc.getProperty("ShowSummonCrest", false);
        WYVERN_ALLOW_UPGRADER = npc.getProperty("AllowWyvernUpgrader", true);
        WYVERN_REQUIRED_LEVEL = npc.getProperty("RequiredStriderLevel", 55);
        WYVERN_REQUIRED_CRYSTALS = npc.getProperty("RequiredCrystalsNumber", 10);
        RAID_HP_REGEN_MULTIPLIER = npc.getProperty("RaidHpRegenMultiplier", 1.);
        RAID_MP_REGEN_MULTIPLIER = npc.getProperty("RaidMpRegenMultiplier", 1.);
        RAID_DEFENCE_MULTIPLIER = npc.getProperty("RaidDefenceMultiplier", 1.);
        RAID_MINION_RESPAWN_TIMER = npc.getProperty("RaidMinionRespawnTime", 300000);
        RAID_DISABLE_CURSE = npc.getProperty("DisableRaidCurse", false);
        SPAWN_INTERVAL_AQ = npc.getProperty("AntQueenSpawnInterval", 36);
        RANDOM_SPAWN_TIME_AQ = npc.getProperty("AntQueenRandomSpawn", 17);
        SPAWN_INTERVAL_ANTHARAS = npc.getProperty("AntharasSpawnInterval", 264);
        RANDOM_SPAWN_TIME_ANTHARAS = npc.getProperty("AntharasRandomSpawn", 72);
        WAIT_TIME_ANTHARAS = npc.getProperty("AntharasWaitTime", 30) * 60000;
        SPAWN_INTERVAL_BAIUM = npc.getProperty("BaiumSpawnInterval", 168);
        RANDOM_SPAWN_TIME_BAIUM = npc.getProperty("BaiumRandomSpawn", 48);
        SPAWN_INTERVAL_CORE = npc.getProperty("CoreSpawnInterval", 60);
        RANDOM_SPAWN_TIME_CORE = npc.getProperty("CoreRandomSpawn", 23);
        SPAWN_INTERVAL_FRINTEZZA = npc.getProperty("FrintezzaSpawnInterval", 48);
        RANDOM_SPAWN_TIME_FRINTEZZA = npc.getProperty("FrintezzaRandomSpawn", 8);
        WAIT_TIME_FRINTEZZA = npc.getProperty("FrintezzaWaitTime", 1) * 60000;
        SPAWN_INTERVAL_ORFEN = npc.getProperty("OrfenSpawnInterval", 48);
        RANDOM_SPAWN_TIME_ORFEN = npc.getProperty("OrfenRandomSpawn", 20);
        SPAWN_INTERVAL_SAILREN = npc.getProperty("SailrenSpawnInterval", 36);
        RANDOM_SPAWN_TIME_SAILREN = npc.getProperty("SailrenRandomSpawn", 24);
        WAIT_TIME_SAILREN = npc.getProperty("SailrenWaitTime", 5) * 60000;
        SPAWN_INTERVAL_VALAKAS = npc.getProperty("ValakasSpawnInterval", 264);
        RANDOM_SPAWN_TIME_VALAKAS = npc.getProperty("ValakasRandomSpawn", 72);
        WAIT_TIME_VALAKAS = npc.getProperty("ValakasWaitTime", 30) * 60000;
        SPAWN_INTERVAL_ZAKEN = npc.getProperty("ZakenSpawnInterval", 60);
        RANDOM_SPAWN_TIME_ZAKEN = npc.getProperty("ZakenRandomSpawn", 20);
        GUARD_ATTACK_AGGRO_MOB = npc.getProperty("GuardAttackAggroMob", false);
        RANDOM_WALK_RATE = npc.getProperty("RandomWalkRate", 30);
        MAX_DRIFT_RANGE = npc.getProperty("MaxDriftRange", 200);
        MIN_NPC_ANIMATION = npc.getProperty("MinNPCAnimation", 20);
        MAX_NPC_ANIMATION = npc.getProperty("MaxNPCAnimation", 40);
        MIN_MONSTER_ANIMATION = npc.getProperty("MinMonsterAnimation", 10);
        MAX_MONSTER_ANIMATION = npc.getProperty("MaxMonsterAnimation", 40);
    }

    /**
     * Loads player settings.<br>
     * Such as stats, inventory/warehouse, enchant, augmentation, karma, party, admin, petition, skill learn.
     */
    private static void loadPlayers() {
        ExProperties players = initProperties(PLAYER_FILE);

        EFFECT_CANCELING = players.getProperty("CancelLesserEffect", true);
        HP_REGEN_MULTIPLIER = players.getProperty("HpRegenMultiplier", 1.);
        MP_REGEN_MULTIPLIER = players.getProperty("MpRegenMultiplier", 1.);
        CP_REGEN_MULTIPLIER = players.getProperty("CpRegenMultiplier", 1.);
        PLAYER_SPAWN_PROTECTION = players.getProperty("PlayerSpawnProtection", 0);
        PLAYER_FAKEDEATH_UP_PROTECTION = players.getProperty("PlayerFakeDeathUpProtection", 0);
        RESPAWN_RESTORE_HP = players.getProperty("RespawnRestoreHP", 0.7);
        MAX_PVTSTORE_SLOTS_DWARF = players.getProperty("MaxPvtStoreSlotsDwarf", 5);
        MAX_PVTSTORE_SLOTS_OTHER = players.getProperty("MaxPvtStoreSlotsOther", 4);
        DEEPBLUE_DROP_RULES = players.getProperty("UseDeepBlueDropRules", true);
        ALLOW_DELEVEL = players.getProperty("AllowDelevel", true);
        DEATH_PENALTY_CHANCE = players.getProperty("DeathPenaltyChance", 20);

        INVENTORY_MAXIMUM_NO_DWARF = players.getProperty("MaximumSlotsForNoDwarf", 80);
        INVENTORY_MAXIMUM_DWARF = players.getProperty("MaximumSlotsForDwarf", 100);
        INVENTORY_MAXIMUM_PET = players.getProperty("MaximumSlotsForPet", 12);
        MAX_ITEM_IN_PACKET = Math.max(INVENTORY_MAXIMUM_NO_DWARF, INVENTORY_MAXIMUM_DWARF);
        WEIGHT_LIMIT = players.getProperty("WeightLimit", 1.);
        WAREHOUSE_SLOTS_NO_DWARF = players.getProperty("MaximumWarehouseSlotsForNoDwarf", 100);
        WAREHOUSE_SLOTS_DWARF = players.getProperty("MaximumWarehouseSlotsForDwarf", 120);
        WAREHOUSE_SLOTS_CLAN = players.getProperty("MaximumWarehouseSlotsForClan", 150);
        FREIGHT_SLOTS = players.getProperty("MaximumFreightSlots", 20);
        REGION_BASED_FREIGHT = players.getProperty("RegionBasedFreight", true);
        FREIGHT_PRICE = players.getProperty("FreightPrice", 1000);

        ENCHANT_CHANCE_WEAPON_MAGIC = players.getProperty("EnchantChanceMagicWeapon", 0.4);
        ENCHANT_CHANCE_WEAPON_MAGIC_15PLUS = players.getProperty("EnchantChanceMagicWeapon15Plus", 0.2);
        ENCHANT_CHANCE_WEAPON_NONMAGIC = players.getProperty("EnchantChanceNonMagicWeapon", 0.7);
        ENCHANT_CHANCE_WEAPON_NONMAGIC_15PLUS = players.getProperty("EnchantChanceNonMagicWeapon15Plus", 0.35);
        ENCHANT_CHANCE_ARMOR = players.getProperty("EnchantChanceArmor", 0.66);
        ENCHANT_MAX_WEAPON = players.getProperty("EnchantMaxWeapon", 0);
        ENCHANT_MAX_ARMOR = players.getProperty("EnchantMaxArmor", 0);
        ENCHANT_SAFE_MAX = players.getProperty("EnchantSafeMax", 3);
        ENCHANT_SAFE_MAX_FULL = players.getProperty("EnchantSafeMaxFull", 4);

        AUGMENTATION_NG_SKILL_CHANCE = players.getProperty("AugmentationNGSkillChance", 15);
        AUGMENTATION_NG_GLOW_CHANCE = players.getProperty("AugmentationNGGlowChance", 0);
        AUGMENTATION_MID_SKILL_CHANCE = players.getProperty("AugmentationMidSkillChance", 30);
        AUGMENTATION_MID_GLOW_CHANCE = players.getProperty("AugmentationMidGlowChance", 40);
        AUGMENTATION_HIGH_SKILL_CHANCE = players.getProperty("AugmentationHighSkillChance", 45);
        AUGMENTATION_HIGH_GLOW_CHANCE = players.getProperty("AugmentationHighGlowChance", 70);
        AUGMENTATION_TOP_SKILL_CHANCE = players.getProperty("AugmentationTopSkillChance", 60);
        AUGMENTATION_TOP_GLOW_CHANCE = players.getProperty("AugmentationTopGlowChance", 100);
        AUGMENTATION_BASESTAT_CHANCE = players.getProperty("AugmentationBaseStatChance", 1);

        KARMA_PLAYER_CAN_BE_KILLED_IN_PZ = players.getProperty("KarmaPlayerCanBeKilledInPeaceZone", false);
        KARMA_PLAYER_CAN_SHOP = players.getProperty("KarmaPlayerCanShop", false);
        KARMA_PLAYER_CAN_USE_GK = players.getProperty("KarmaPlayerCanUseGK", false);
        KARMA_PLAYER_CAN_TELEPORT = players.getProperty("KarmaPlayerCanTeleport", true);
        KARMA_PLAYER_CAN_TRADE = players.getProperty("KarmaPlayerCanTrade", true);
        KARMA_PLAYER_CAN_USE_WH = players.getProperty("KarmaPlayerCanUseWareHouse", true);
        KARMA_DROP_GM = players.getProperty("CanGMDropEquipment", false);
        KARMA_AWARD_PK_KILL = players.getProperty("AwardPKKillPVPPoint", true);
        KARMA_PK_LIMIT = players.getProperty("MinimumPKRequiredToDrop", 5);
        KARMA_NONDROPPABLE_PET_ITEMS = players.getProperty("ListOfPetItems", new int[]
                {
                        2375,
                        3500,
                        3501,
                        3502,
                        4422,
                        4423,
                        4424,
                        4425,
                        6648,
                        6649,
                        6650
                });
        KARMA_NONDROPPABLE_ITEMS = players.getProperty("ListOfNonDroppableItemsForPK", new int[]
                {
                        1147,
                        425,
                        1146,
                        461,
                        10,
                        2368,
                        7,
                        6,
                        2370,
                        2369
                });

        PVP_NORMAL_TIME = players.getProperty("PvPVsNormalTime", 40000);
        PVP_PVP_TIME = players.getProperty("PvPVsPvPTime", 20000);

        PARTY_XP_CUTOFF_METHOD = players.getProperty("PartyXpCutoffMethod", "level");
        PARTY_XP_CUTOFF_PERCENT = players.getProperty("PartyXpCutoffPercent", 3.);
        PARTY_XP_CUTOFF_LEVEL = players.getProperty("PartyXpCutoffLevel", 20);
        PARTY_RANGE = players.getProperty("PartyRange", 1500);

        DEFAULT_ACCESS_LEVEL = players.getProperty("DefaultAccessLevel", 0);
        GM_HERO_AURA = players.getProperty("GMHeroAura", false);
        GM_STARTUP_INVULNERABLE = players.getProperty("GMStartupInvulnerable", false);
        GM_STARTUP_INVISIBLE = players.getProperty("GMStartupInvisible", false);
        GM_STARTUP_BLOCK_ALL = players.getProperty("GMStartupBlockAll", false);
        GM_STARTUP_AUTO_LIST = players.getProperty("GMStartupAutoList", true);

        PETITIONING_ALLOWED = players.getProperty("PetitioningAllowed", true);
        MAX_PETITIONS_PER_PLAYER = players.getProperty("MaxPetitionsPerPlayer", 5);
        MAX_PETITIONS_PENDING = players.getProperty("MaxPetitionsPending", 25);

        IS_CRAFTING_ENABLED = players.getProperty("CraftingEnabled", true);
        DWARF_RECIPE_LIMIT = players.getProperty("DwarfRecipeLimit", 50);
        COMMON_RECIPE_LIMIT = players.getProperty("CommonRecipeLimit", 50);
        BLACKSMITH_USE_RECIPES = players.getProperty("BlacksmithUseRecipes", true);

        AUTO_LEARN_SKILLS = players.getProperty("AutoLearnSkills", false);
        LVL_AUTO_LEARN_SKILLS = players.getProperty("LvlAutoLearnSkills", 40);
        MAGIC_FAILURES = players.getProperty("MagicFailures", true);
        PERFECT_SHIELD_BLOCK_RATE = players.getProperty("PerfectShieldBlockRate", 5);
        LIFE_CRYSTAL_NEEDED = players.getProperty("LifeCrystalNeeded", true);
        SP_BOOK_NEEDED = players.getProperty("SpBookNeeded", true);
        ES_SP_BOOK_NEEDED = players.getProperty("EnchantSkillSpBookNeeded", true);
        DIVINE_SP_BOOK_NEEDED = players.getProperty("DivineInspirationSpBookNeeded", true);
        SUBCLASS_WITHOUT_QUESTS = players.getProperty("SubClassWithoutQuests", false);

        MAX_BUFFS_AMOUNT = players.getProperty("MaxBuffsAmount", 20);
        STORE_SKILL_COOLTIME = players.getProperty("StoreSkillCooltime", true);
    }

    /**
     * Loads siege settings.
     */
    private static void loadSieges() {
        ExProperties sieges = initProperties(Config.SIEGE_FILE);

        SIEGE_LENGTH = sieges.getProperty("SiegeLength", 120);
        MINIMUM_CLAN_LEVEL = sieges.getProperty("SiegeClanMinLevel", 4);
        MAX_ATTACKERS_NUMBER = sieges.getProperty("AttackerMaxClans", 10);
        MAX_DEFENDERS_NUMBER = sieges.getProperty("DefenderMaxClans", 10);
        ATTACKERS_RESPAWN_DELAY = sieges.getProperty("AttackerRespawn", 10000);

        CH_MINIMUM_CLAN_LEVEL = sieges.getProperty("ChSiegeClanMinLevel", 4);
        CH_MAX_ATTACKERS_NUMBER = sieges.getProperty("ChAttackerMaxClans", 10);
    }

    /**
     * Loads GameServer settings.<br>
     * IP addresses, database, rates, feature enabled/disabled, misc.
     */
    private static void loadGame() {
        ExProperties server = initProperties(GAME_SERVER_FILE);

        HOSTNAME = server.getProperty("Hostname", "*");
        GAME_SERVER_HOSTNAME = server.getProperty("GameserverHostname");
        GAME_SERVER_PORT = server.getProperty("GameserverPort", 7777);
        GAME_SERVER_LOGIN_HOSTNAME = server.getProperty("LoginHost", "127.0.0.1");
        GAME_SERVER_LOGIN_PORT = server.getProperty("LoginPort", 9014);
        REQUEST_ID = server.getProperty("RequestServerID", 0);
        ACCEPT_ALTERNATE_ID = server.getProperty("AcceptAlternateID", true);
        USE_BLOWFISH_CIPHER = server.getProperty("UseBlowfishCipher", true);

        DATABASE_URL = server.getProperty("URL", "jdbc:mariadb://localhost/l2jspace");
        DATABASE_LOGIN = server.getProperty("Login", "root");
        DATABASE_PASSWORD = server.getProperty("Password", "");
        DATABASE_MAX_CONNECTIONS = server.getProperty("MaximumDbConnections", 10);

        SERVER_LIST_BRACKET = server.getProperty("ServerListBrackets", false);
        SERVER_LIST_CLOCK = server.getProperty("ServerListClock", false);
        SERVER_GM_ONLY = server.getProperty("ServerGMOnly", false);
        SERVER_LIST_AGE = server.getProperty("ServerListAgeLimit", 0);
        SERVER_LIST_TEST_SERVER = server.getProperty("TestServer", false);
        SERVER_LIST_PVP_SERVER = server.getProperty("PvpServer", true);

        DELETE_DAYS = server.getProperty("DeleteCharAfterDays", 7);
        MAXIMUM_ONLINE_USERS = server.getProperty("MaximumOnlineUsers", 100);

        AUTO_LOOT = server.getProperty("AutoLoot", false);
        AUTO_LOOT_HERBS = server.getProperty("AutoLootHerbs", false);
        AUTO_LOOT_RAID = server.getProperty("AutoLootRaid", false);

        ALLOW_DISCARD_ITEM = server.getProperty("AllowDiscardItem", true);
        MULTIPLE_ITEM_DROP = server.getProperty("MultipleItemDrop", true);
        HERB_AUTO_DESTROY_TIME = server.getProperty("AutoDestroyHerbTime", 15) * 1000;
        ITEM_AUTO_DESTROY_TIME = server.getProperty("AutoDestroyItemTime", 600) * 1000;
        EQUIPABLE_ITEM_AUTO_DESTROY_TIME = server.getProperty("AutoDestroyEquipableItemTime", 0) * 1000;
        SPECIAL_ITEM_DESTROY_TIME = new HashMap<>();
        String[] data = server.getProperty("AutoDestroySpecialItemTime", (String[]) null, ",");
        if (data != null) {
            for (String itemData : data) {
                String[] item = itemData.split("-");
                SPECIAL_ITEM_DESTROY_TIME.put(Integer.parseInt(item[0]), Integer.parseInt(item[1]) * 1000);
            }
        }
        PLAYER_DROPPED_ITEM_MULTIPLIER = server.getProperty("PlayerDroppedItemMultiplier", 1);

        ALLOW_FREIGHT = server.getProperty("AllowFreight", true);
        ALLOW_WAREHOUSE = server.getProperty("AllowWarehouse", true);
        ALLOW_WEAR = server.getProperty("AllowWear", true);
        WEAR_DELAY = server.getProperty("WearDelay", 5);
        WEAR_PRICE = server.getProperty("WearPrice", 10);
        ALLOW_LOTTERY = server.getProperty("AllowLottery", true);
        ALLOW_WATER = server.getProperty("AllowWater", true);
        ALLOW_MANOR = server.getProperty("AllowManor", true);
        ALLOW_BOAT = server.getProperty("AllowBoat", true);
        ALLOW_CURSED_WEAPONS = server.getProperty("AllowCursedWeapons", true);

        ENABLE_FALLING_DAMAGE = server.getProperty("EnableFallingDamage", true);

        NO_SPAWNS = server.getProperty("NoSpawns", false);
        DEVELOPER = server.getProperty("Developer", false);
        PACKET_HANDLER_DEBUG = server.getProperty("PacketHandlerDebug", false);

        DEADLOCK_DETECTOR = server.getProperty("DeadLockDetector", false);
        DEADLOCK_CHECK_INTERVAL = server.getProperty("DeadLockCheckInterval", 20);
        RESTART_ON_DEADLOCK = server.getProperty("RestartOnDeadlock", false);

        LOG_CHAT = server.getProperty("LogChat", false);
        LOG_ITEMS = server.getProperty("LogItems", false);
        GM_AUDIT = server.getProperty("GMAudit", false);

        ENABLE_COMMUNITY_BOARD = server.getProperty("EnableCommunityBoard", false);
        BBS_DEFAULT = server.getProperty("BBSDefault", "_bbshome");

        ROLL_DICE_TIME = server.getProperty("RollDiceTime", 4200);
        HERO_VOICE_TIME = server.getProperty("HeroVoiceTime", 10000);
        SUBCLASS_TIME = server.getProperty("SubclassTime", 2000);
        DROP_ITEM_TIME = server.getProperty("DropItemTime", 1000);
        SERVER_BYPASS_TIME = server.getProperty("ServerBypassTime", 100);
        MULTISELL_TIME = server.getProperty("MultisellTime", 100);
        MANUFACTURE_TIME = server.getProperty("ManufactureTime", 300);
        MANOR_TIME = server.getProperty("ManorTime", 3000);
        SENDMAIL_TIME = server.getProperty("SendMailTime", 10000);
        CHARACTER_SELECT_TIME = server.getProperty("CharacterSelectTime", 3000);
        GLOBAL_CHAT_TIME = server.getProperty("GlobalChatTime", 0);
        TRADE_CHAT_TIME = server.getProperty("TradeChatTime", 0);
        SOCIAL_TIME = server.getProperty("SocialTime", 2000);
        MOVE_TIME = server.getProperty("MoveTime", 100);

        SCHEDULED_THREAD_POOL_COUNT = server.getProperty("ScheduledThreadPoolCount", -1);
        THREADS_PER_SCHEDULED_THREAD_POOL = server.getProperty("ThreadsPerScheduledThreadPool", 4);
        INSTANT_THREAD_POOL_COUNT = server.getProperty("InstantThreadPoolCount", -1);
        THREADS_PER_INSTANT_THREAD_POOL = server.getProperty("ThreadsPerInstantThreadPool", 2);

        L2WALKER_PROTECTION = server.getProperty("L2WalkerProtection", false);
        ZONE_TOWN = server.getProperty("ZoneTown", 0);
        SERVER_NEWS = server.getProperty("ShowServerNews", false);
    }

    private static void loadRates() {
        ExProperties rates = initProperties(RATE_FILE);
        RATE_XP = rates.getProperty("RateXp", 1.);
        RATE_SP = rates.getProperty("RateSp", 1.);
        RATE_PARTY_XP = rates.getProperty("RatePartyXp", 1.);
        RATE_PARTY_SP = rates.getProperty("RatePartySp", 1.);
        RATE_DROP_ADENA = rates.getProperty("RateDropAdena", 1.);
        RATE_DROP_ITEMS = rates.getProperty("RateDropItems", 1.);
        RATE_DROP_ITEMS_BY_RAID = rates.getProperty("RateRaidDropItems", 1.);
        RATE_DROP_ITEMS_BY_GRAND = rates.getProperty("RateGrandDropItems", 1.);
        RATE_DROP_SPOIL = rates.getProperty("RateDropSpoil", 1.);
        RATE_DROP_MANOR = rates.getProperty("RateDropManor", 1);
        RATE_QUEST_DROP = rates.getProperty("RateQuestDrop", 1.);
        RATE_QUEST_REWARD = rates.getProperty("RateQuestReward", 1.);
        RATE_QUEST_REWARD_XP = rates.getProperty("RateQuestRewardXP", 1.);
        RATE_QUEST_REWARD_SP = rates.getProperty("RateQuestRewardSP", 1.);
        RATE_QUEST_REWARD_ADENA = rates.getProperty("RateQuestRewardAdena", 1.);
        RATE_KARMA_EXP_LOST = rates.getProperty("RateKarmaExpLost", 1.);
        RATE_SIEGE_GUARDS_PRICE = rates.getProperty("RateSiegeGuardsPrice", 1.);
        RATE_DROP_COMMON_HERBS = rates.getProperty("RateCommonHerbs", 1.);
        RATE_DROP_HP_HERBS = rates.getProperty("RateHpHerbs", 1.);
        RATE_DROP_MP_HERBS = rates.getProperty("RateMpHerbs", 1.);
        RATE_DROP_SPECIAL_HERBS = rates.getProperty("RateSpecialHerbs", 1.);
        PLAYER_DROP_LIMIT = rates.getProperty("PlayerDropLimit", 3);
        PLAYER_RATE_DROP = rates.getProperty("PlayerRateDrop", 5);
        PLAYER_RATE_DROP_ITEM = rates.getProperty("PlayerRateDropItem", 70);
        PLAYER_RATE_DROP_EQUIP = rates.getProperty("PlayerRateDropEquip", 25);
        PLAYER_RATE_DROP_EQUIP_WEAPON = rates.getProperty("PlayerRateDropEquipWeapon", 5);
        PET_XP_RATE = rates.getProperty("PetXpRate", 1.);
        PET_FOOD_RATE = rates.getProperty("PetFoodRate", 1);
        SINEATER_XP_RATE = rates.getProperty("SinEaterXpRate", 1.);
        KARMA_DROP_LIMIT = rates.getProperty("KarmaDropLimit", 10);
        KARMA_RATE_DROP = rates.getProperty("KarmaRateDrop", 70);
        KARMA_RATE_DROP_ITEM = rates.getProperty("KarmaRateDropItem", 50);
        KARMA_RATE_DROP_EQUIP = rates.getProperty("KarmaRateDropEquip", 40);
        KARMA_RATE_DROP_EQUIP_WEAPON = rates.getProperty("KarmaRateDropEquipWeapon", 10);
    }

    private static void loadCustomEvent() {
        ExProperties engine = initProperties(EventConfig.Engine.PROPERTIES);

        EventConfig.Engine.ANNOUNCE_REWARD = engine.getProperty("AnnounceReward", true);
        EventConfig.Engine.REGISTRATION_BY_COMMANDS = engine.getProperty("RegistrationByCommands", true);
        EventConfig.Engine.LOG_STATISTICS = engine.getProperty("LogStatistics", true);
        EventConfig.Engine.WAIT_TELEPORT_SECONDS = engine.getProperty("WaitTeleportSeconds", 5);

        ExProperties ctf = initProperties(EventConfig.CTF.PROPERTIES);

        EventConfig.CTF.ENABLED = ctf.getProperty("Enabled", false);
        EventConfig.CTF.AURA = ctf.getProperty("Aura", true);
        EventConfig.CTF.LAUNCH_TIMES = ctf.getProperty("LaunchTimes", "").split(";");
        EventConfig.CTF.LOADING_MODE = EventLoadingMode.valueOf(ctf.getProperty("LoadingMode", EventLoadingMode.RANDOMLY.name()));
        EventConfig.CTF.TEAM_MODE = EventTeamType.valueOf(ctf.getProperty("TeamMode", EventTeamType.SHUFFLE.name()));
        EventConfig.CTF.UNSUMMON_PET = ctf.getProperty("UnsummonPet", true);
        EventConfig.CTF.REMOVE_ALL_EFFECTS = ctf.getProperty("RemoveAllEffects", false);
        EventConfig.CTF.JOIN_CURSED_WEAPON = ctf.getProperty("JoinCursedWeapon", false);
    }

    private static void loadAddon() {
        ExProperties addon = initProperties(ADDON_FILE);

        INFINITY_SS = addon.getProperty("InfinitySS", false);
        INFINITY_ARROWS = addon.getProperty("InfinityArrows", false);
        OLY_PERIOD = OlympiadPeriod.valueOf(addon.getProperty("OlyPeriod", "MONTH"));
        OLY_PERIOD_MULTIPLIER = addon.getProperty("OlyPeriodMultiplier", 1);
        ENABLE_MODIFY_SKILL_DURATION = addon.getProperty("EnableModifySkillDuration", false);

        if (ENABLE_MODIFY_SKILL_DURATION) {
            SKILL_DURATION_LIST = new HashMap<>();
            String[] propertySplit = addon.getProperty("SkillDurationList", "").split(";");

            for (String skill : propertySplit) {
                String[] skillSplit = skill.split(",");
                if (skillSplit.length != 2) {
                    System.out.println("[SkillDurationList]: invalid config property -> SkillDurationList \"" + skill + "\"");
                } else {
                    try {
                        SKILL_DURATION_LIST.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
                    } catch (NumberFormatException e) {
                        LOGGER.error(e.getMessage());

                        if (!skill.equals("")) {
                            LOGGER.error("[SkillDurationList]: invalid config property -> SkillList \"" + skillSplit[0] + "\"" + skillSplit[1]);
                        }
                    }
                }
            }
        }

        GLOBAL_CHAT = addon.getProperty("GlobalChat", "ON");
        TRADE_CHAT = addon.getProperty("TradeChat", "ON");
        CHAT_ALL_LEVEL = addon.getProperty("AllChatLevel", 1);
        CHAT_TELL_LEVEL = addon.getProperty("TellChatLevel", 1);
        CHAT_SHOUT_LEVEL = addon.getProperty("ShoutChatLevel", 1);
        CHAT_TRADE_LEVEL = addon.getProperty("TradeChatLevel", 1);
        ENABLE_MENU = addon.getProperty("EnableMenu", false);
        ENABLE_ONLINE_COMMAND = addon.getProperty("EnabledOnlineCommand", false);
        BOTS_PREVENTION = addon.getProperty("EnableBotsPrevention", false);
        KILLS_COUNTER = addon.getProperty("KillsCounter", 60);
        KILLS_COUNTER_RANDOMIZATION = addon.getProperty("KillsCounterRandomization", 50);
        VALIDATION_TIME = addon.getProperty("ValidationTime", 60);
        PUNISHMENT = addon.getProperty("Punishment", 0);
        USE_PREMIUM_SERVICE = addon.getProperty("UsePremiumServices", false);
        PREMIUM_RATE_XP = addon.getProperty("PremiumRateXp", 2.);
        PREMIUM_RATE_SP = addon.getProperty("PremiumRateSp", 2.);
        PREMIUM_RATE_DROP_ADENA = addon.getProperty("PremiumRateDropAdena", 2.);
        PREMIUM_RATE_DROP_SPOIL = addon.getProperty("PremiumRateDropSpoil", 2.);
        PREMIUM_RATE_DROP_ITEMS = addon.getProperty("PremiumRateDropItems", 2.);
        PREMIUM_RATE_DROP_ITEMS_BY_RAID = addon.getProperty("PremiumRateRaidDropItems", 2.);
        ATTACK_PTS = addon.getProperty("AttackPTS", true);
        SUBCLASS_SKILLS = addon.getProperty("SubClassSkills", false);
        GAME_SUBCLASS_EVERYWHERE = addon.getProperty("SubclassEverywhere", false);
        SHOW_NPC_INFO = addon.getProperty("ShowNpcInfo", false);
        ALLOW_GRAND_BOSSES_TELEPORT = addon.getProperty("AllowGrandBossesTeleport", false);
    }

    /**
     * Loads LoginServer settings.<br>
     * IP addresses, database, account, misc.
     */
    private static void loadLogin() {
        ExProperties server = initProperties(LOGIN_SERVER_FILE);

        HOSTNAME = server.getProperty("Hostname", "localhost");
        LOGINSERVER_HOSTNAME = server.getProperty("LoginserverHostname", "*");
        LOGINSERVER_PORT = server.getProperty("LoginserverPort", 2106);
        GAME_SERVER_LOGIN_HOSTNAME = server.getProperty("LoginHostname", "*");
        GAME_SERVER_LOGIN_PORT = server.getProperty("LoginPort", 9014);
        LOGIN_TRY_BEFORE_BAN = server.getProperty("LoginTryBeforeBan", 3);
        LOGIN_BLOCK_AFTER_BAN = server.getProperty("LoginBlockAfterBan", 600);
        ACCEPT_NEW_GAMESERVER = server.getProperty("AcceptNewGameServer", false);
        SHOW_LICENCE = server.getProperty("ShowLicence", true);

        DATABASE_URL = server.getProperty("URL", "jdbc:mariadb://localhost/l2jspace");
        DATABASE_LOGIN = server.getProperty("Login", "root");
        DATABASE_PASSWORD = server.getProperty("Password", "");
        DATABASE_MAX_CONNECTIONS = server.getProperty("MaximumDbConnections", 5);

        AUTO_CREATE_ACCOUNTS = server.getProperty("AutoCreateAccounts", true);

        FLOOD_PROTECTION = server.getProperty("EnableFloodProtection", true);
        FAST_CONNECTION_LIMIT = server.getProperty("FastConnectionLimit", 15);
        NORMAL_CONNECTION_TIME = server.getProperty("NormalConnectionTime", 700);
        FAST_CONNECTION_TIME = server.getProperty("FastConnectionTime", 350);
        MAX_CONNECTION_PER_IP = server.getProperty("MaxConnectionPerIP", 50);
    }

    public static void loadGameServer() {
        LOGGER.info("Loading GameServer configuration files.");

        loadOfflineShop();
        loadClans();
        loadEvents();
        loadGeoEngine();
        loadHexId();
        loadNpc();
        loadPlayers();
        loadSieges();
        loadGame();
        loadRates();
        loadAddon();
        loadCustomEvent();
    }

    public static void loadLoginServer() {
        LOGGER.info("Loading LoginServer configuration files.");

        loadLogin();
    }

    public static void loadAccountManager() {
        LOGGER.info("Loading account manager configuration files.");

        loadLogin();
    }

    public static void loadGameServerRegistration() {
        LOGGER.info("Loading GameServer registration configuration files.");

        loadLogin();
    }

    public static final class ClassMasterSettings {
        private final Map<Integer, Boolean> allowedClassChange;
        private final Map<Integer, List<IntIntHolder>> claimItems;
        private final Map<Integer, List<IntIntHolder>> rewardItems;

        public ClassMasterSettings(String configLine) {
            allowedClassChange = new HashMap<>(3);
            claimItems = new HashMap<>(3);
            rewardItems = new HashMap<>(3);

            if (configLine != null)
                parseConfigLine(configLine.trim());
        }

        private void parseConfigLine(String configLine) {
            StringTokenizer param = new StringTokenizer(configLine, ";");

            while (param.hasMoreTokens()) {
                // Get allowed class change.
                int job = Integer.parseInt(param.nextToken());

                allowedClassChange.put(job, true);

                List<IntIntHolder> items = new ArrayList<>();

                // Parse items needed for class change.
                addItem(param, items);

                // Feed the map, and clean the list.
                claimItems.put(job, items);
                items.clear();

                // Parse gifts after class change.
                addItem(param, items);

                rewardItems.put(job, items);
            }
        }

        private void addItem(StringTokenizer tokenizer, List<IntIntHolder> items) {
            if (tokenizer.hasMoreTokens()) {
                StringTokenizer array = new StringTokenizer(tokenizer.nextToken(), "[],");

                while (array.hasMoreTokens()) {
                    StringTokenizer item = new StringTokenizer(array.nextToken(), "()");

                    items.add(new IntIntHolder(Integer.parseInt(item.nextToken()), Integer.parseInt(item.nextToken())));
                }
            }
        }

        public boolean isAllowed(int job) {
            if (allowedClassChange == null) {
                return false;
            }

            if (allowedClassChange.containsKey(job)) {
                return allowedClassChange.get(job);
            }

            return false;
        }

        public List<IntIntHolder> getRewardItems(int job) {
            return rewardItems.get(job);
        }

        public List<IntIntHolder> getRequiredItems(int job) {
            return claimItems.get(job);
        }
    }
}