package ru.privetdruk.l2jspace.gameserver;

import ru.privetdruk.l2jspace.common.lang.StringUtil;
import ru.privetdruk.l2jspace.common.logging.CLogger;
import ru.privetdruk.l2jspace.common.mmocore.SelectorConfig;
import ru.privetdruk.l2jspace.common.mmocore.SelectorThread;
import ru.privetdruk.l2jspace.common.network.IPv4Filter;
import ru.privetdruk.l2jspace.common.pool.ConnectionPool;
import ru.privetdruk.l2jspace.common.pool.ThreadPool;
import ru.privetdruk.l2jspace.common.util.DeadLockDetector;
import ru.privetdruk.l2jspace.common.util.SysUtil;
import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.communitybbs.CommunityBoard;
import ru.privetdruk.l2jspace.gameserver.custom.service.EventService;
import ru.privetdruk.l2jspace.gameserver.custom.service.SchemeBufferService;
import ru.privetdruk.l2jspace.gameserver.custom.service.WeddingService;
import ru.privetdruk.l2jspace.gameserver.data.SkillTable;
import ru.privetdruk.l2jspace.gameserver.data.cache.CrestCache;
import ru.privetdruk.l2jspace.gameserver.data.cache.HtmCache;
import ru.privetdruk.l2jspace.gameserver.data.manager.*;
import ru.privetdruk.l2jspace.gameserver.data.sql.*;
import ru.privetdruk.l2jspace.gameserver.data.xml.*;
import ru.privetdruk.l2jspace.gameserver.geoengine.GeoEngine;
import ru.privetdruk.l2jspace.gameserver.handler.*;
import ru.privetdruk.l2jspace.gameserver.idfactory.IdFactory;
import ru.privetdruk.l2jspace.gameserver.model.World;
import ru.privetdruk.l2jspace.gameserver.model.boat.*;
import ru.privetdruk.l2jspace.gameserver.model.olympiad.Olympiad;
import ru.privetdruk.l2jspace.gameserver.model.olympiad.OlympiadGameManager;
import ru.privetdruk.l2jspace.gameserver.network.GameClient;
import ru.privetdruk.l2jspace.gameserver.network.GamePacketHandler;
import ru.privetdruk.l2jspace.gameserver.taskmanager.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.logging.LogManager;

public class GameServer {
    private static final CLogger LOGGER = new CLogger(GameServer.class.getName());

    private final SelectorThread<GameClient> _selectorThread;

    private static GameServer _gameServer;
    public long serverLoadStart = System.currentTimeMillis();

    public static void main(String[] args) throws Exception {
        _gameServer = new GameServer();
    }

    public GameServer() throws Exception {
        // Create log folder
        new File("./log").mkdir();
        new File("./log/chat").mkdir();
        new File("./log/console").mkdir();
        new File("./log/error").mkdir();
        new File("./log/gmaudit").mkdir();
        new File("./log/item").mkdir();
        new File("./data/crests").mkdirs();

        // Create input stream for log file -- or store file data into memory
        try (InputStream is = new FileInputStream(new File("config/logging.properties"))) {
            LogManager.getLogManager().readConfiguration(is);
        }

        StringUtil.printSection("Config");
        Config.loadGameServer();

        StringUtil.printSection("Poolers");
        ConnectionPool.init();
        ThreadPool.init();

        StringUtil.printSection("IdFactory");
        IdFactory.getInstance();

        StringUtil.printSection("Cache");
        HtmCache.getInstance();
        CrestCache.getInstance();

        StringUtil.printSection("World");
        World.getInstance();
        MapRegionData.getInstance();
        AnnouncementData.getInstance();
        ServerMemoTable.getInstance();

        StringUtil.printSection("Skills");
        SkillTable.getInstance();
        SkillTreeData.getInstance();

        StringUtil.printSection("Items");
        ItemData.getInstance();
        SummonItemData.getInstance();
        HennaData.getInstance();
        BuyListManager.getInstance();
        MultisellData.getInstance();
        RecipeData.getInstance();
        ArmorSetData.getInstance();
        FishData.getInstance();
        SpellbookData.getInstance();
        SoulCrystalData.getInstance();
        AugmentationData.getInstance();
        CursedWeaponManager.getInstance();

        StringUtil.printSection("Admins");
        AdminData.getInstance();
        BookmarkTable.getInstance();
        PetitionManager.getInstance();

        StringUtil.printSection("Characters");
        PlayerData.getInstance();
        PlayerInfoTable.getInstance();
        PlayerLevelData.getInstance();
        PartyMatchRoomManager.getInstance();
        RaidPointManager.getInstance();

        StringUtil.printSection("Community server");
        CommunityBoard.getInstance();

        StringUtil.printSection("Clans");
        ClanTable.getInstance();

        StringUtil.printSection("Geodata & Pathfinding");
        GeoEngine.getInstance();

        StringUtil.printSection("Zones");
        ZoneManager.getInstance();

        StringUtil.printSection("Castles & Clan Halls");
        CastleManager.getInstance();
        ClanHallManager.getInstance();

        StringUtil.printSection("Task Managers");
        AttackStanceTaskManager.getInstance();
        DecayTaskManager.getInstance();
        GameTimeTaskManager.getInstance();
        ItemsOnGroundTaskManager.getInstance();
        PvpFlagTaskManager.getInstance();
        RandomAnimationTaskManager.getInstance();
        ShadowItemTaskManager.getInstance();
        WaterTaskManager.getInstance();
        DelayedItemsManager.getInstance();

        StringUtil.printSection("Auto Spawns");
        AutoSpawnTable.getInstance();

        StringUtil.printSection("Seven Signs");
        SevenSignsManager.getInstance().spawnSevenSignsNPC();
        FestivalOfDarknessManager.getInstance();

        StringUtil.printSection("Manor Manager");
        CastleManorManager.getInstance();

        StringUtil.printSection("NPCs");
        SchemeBufferService.getInstance();
        HerbDropData.getInstance();
        NpcData.getInstance();
        WalkerRouteData.getInstance();
        DoorData.getInstance().spawn();
        StaticObjectData.getInstance();
        SpawnTable.getInstance();
        RaidBossManager.getInstance();
        GrandBossManager.getInstance();
        SevenSignsSpawnManager.getInstance().notifyChangeMode();
        DayNightManager.getInstance().notifyChangeMode();
        DimensionalRiftManager.getInstance();
        NewbieBuffData.getInstance();
        InstantTeleportData.getInstance();
        TeleportData.getInstance();

        StringUtil.printSection("Olympiads & Heroes");
        OlympiadGameManager.getInstance();
        Olympiad.getInstance();
        HeroManager.getInstance();

        StringUtil.printSection("Four Sepulchers");
        FourSepulchersManager.getInstance();

        StringUtil.printSection("Quests & Scripts");
        ScriptData.getInstance();

        if (Config.ALLOW_BOAT) {
            BoatManager.getInstance();
            BoatGiranTalking.load();
            BoatGludinRune.load();
            BoatInnadrilTour.load();
            BoatRunePrimeval.load();
            BoatTalkingGludin.load();
        }

        StringUtil.printSection("Events");
        EventService.getInstance();
        DerbyTrackManager.getInstance();
        LotteryManager.getInstance();

        if (Config.ALLOW_WEDDING)
            WeddingService.getInstance();

        if (Config.ALLOW_FISH_CHAMPIONSHIP)
            FishingChampionshipManager.getInstance();

        if ((Config.OFFLINE_TRADE_ENABLE || Config.OFFLINE_CRAFT_ENABLE) && Config.RESTORE_OFFLINERS)
            OfflineTradersTable.restoreOfflineTraders();

        StringUtil.printSection("Handlers");
        LOGGER.info("Loaded {} admin command handlers.", AdminCommandHandler.getInstance().size());
        LOGGER.info("Loaded {} chat handlers.", ChatHandler.getInstance().size());
        LOGGER.info("Loaded {} item handlers.", ItemHandler.getInstance().size());
        LOGGER.info("Loaded {} skill handlers.", SkillHandler.getInstance().size());
        LOGGER.info("Loaded {} target handlers.", TargetHandler.getInstance().size());
        LOGGER.info("Loaded {} user command handlers.", UserCommandHandler.getInstance().size());
        LOGGER.info("Loaded {} voiced command handlers.", VoicedCommandHandler.getInstance().size());

        StringUtil.printSection("System");
        Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());

        if (Config.DEADLOCK_DETECTOR) {
            LOGGER.info("Deadlock detector is enabled. Timer: {}s.", Config.DEADLOCK_CHECK_INTERVAL);

            final DeadLockDetector deadDetectThread = new DeadLockDetector();
            deadDetectThread.setDaemon(true);
            deadDetectThread.start();
        } else
            LOGGER.info("Deadlock detector is disabled.");

        LOGGER.info("Gameserver has started, used memory: {} / {} Mo.", SysUtil.getUsedMemory(), SysUtil.getMaxMemory());
        LOGGER.info("Maximum allowed players: {}.", Config.MAXIMUM_ONLINE_USERS);
        LOGGER.info("Server loaded in " + (System.currentTimeMillis() - serverLoadStart) / 1000 + " seconds");

        StringUtil.printSection("Login");
        LoginServerThread.getInstance().start();

        final SelectorConfig sc = new SelectorConfig();
        sc.MAX_READ_PER_PASS = Config.MMO_MAX_READ_PER_PASS;
        sc.MAX_SEND_PER_PASS = Config.MMO_MAX_SEND_PER_PASS;
        sc.SLEEP_TIME = Config.MMO_SELECTOR_SLEEP_TIME;
        sc.HELPER_BUFFER_COUNT = Config.MMO_HELPER_BUFFER_COUNT;

        final GamePacketHandler handler = new GamePacketHandler();
        _selectorThread = new SelectorThread<>(sc, handler, handler, handler, new IPv4Filter());

        InetAddress bindAddress = null;
        if (!Config.GAME_SERVER_HOSTNAME.equals("*")) {
            try {
                bindAddress = InetAddress.getByName(Config.GAME_SERVER_HOSTNAME);
            } catch (Exception e) {
                LOGGER.error("The GameServer bind address is invalid, using all available IPs.", e);
            }
        }

        try {
            _selectorThread.openServerSocket(bindAddress, Config.GAME_SERVER_PORT);
        } catch (Exception e) {
            LOGGER.error("Failed to open server socket.", e);
            System.exit(1);
        }
        _selectorThread.start();
    }

    public static GameServer getInstance() {
        return _gameServer;
    }

    public SelectorThread<GameClient> getSelectorThread() {
        return _selectorThread;
    }
}