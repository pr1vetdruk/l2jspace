package ru.privetdruk.l2jspace.gameserver.custom.engine;

import ru.privetdruk.l2jspace.gameserver.custom.model.NpcInfoShort;
import ru.privetdruk.l2jspace.gameserver.custom.model.SkillEnum;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventBorder;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventType;
import ru.privetdruk.l2jspace.gameserver.custom.service.AnnouncementService;
import ru.privetdruk.l2jspace.gameserver.data.manager.CastleManager;
import ru.privetdruk.l2jspace.gameserver.data.sql.SpawnTable;
import ru.privetdruk.l2jspace.gameserver.data.xml.ItemData;
import ru.privetdruk.l2jspace.gameserver.data.xml.NpcData;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.model.entity.Castle;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventSetting;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Item;
import ru.privetdruk.l2jspace.gameserver.model.spawn.Spawn;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.MagicSkillUse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState.ABORT;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState.INACTIVE;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState.READY_TO_START;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState.REGISTRATION;

public abstract class EventEngine implements Runnable {
    protected static final Logger LOGGER = Logger.getLogger(EventEngine.class.getName());
    protected static final AnnouncementService announcementService = AnnouncementService.getInstance();

    protected final EventSetting settings = new EventSetting();
    protected final List<Player> players = Collections.synchronizedList(new ArrayList<>());

    protected EventType eventType;
    protected EventState eventState;
    protected EventBorder eventBorder;
    protected String eventStartTime;

    public EventEngine() {
        eventType = EventType.NONE;
        eventState = INACTIVE;
    }

    @Override
    public void run() {
        try {
            players.clear();

            logInfo("Event notification start.");

            preLaunchChecks();

            switch (eventState) {
                case ABORT -> logInfo("Failed to start the event because failed to pass prelaunch checks.");
                case READY_TO_START -> {
                    registrationPlayer();

                    /*if (teleportPlayer()) {
                        waiter(1);

                        startEvent();

                        waiter(generalSetting.getDurationEvent());

                        finishEvent();
                    } else {
                        abortEvent();
                    }*/
                }
                default -> logInfo("Failed to start the event because the state of the event is incorrect.");
            }
        } catch (Exception e) {
            logError("run", e.getMessage());
        } finally {
            eventState = INACTIVE;
        }

    }

    private void preLaunchChecks() {
        if (eventState != INACTIVE
                || settings.getTimeRegistration() <= 0
                || !customPreLaunchChecks()
                || isSiegesLaunched()) {
            eventState = ABORT;
        } else {
            eventState = READY_TO_START;
        }
    }

    private boolean isSiegesLaunched() {
        for (Castle castle : CastleManager.getInstance().getCastles()) {
            if (castle != null && castle.getSiege() != null && castle.getSiege().isInProgress()) {
                return true;
            }
        }

        return false;
    }

    private void registrationPlayer() {
        eventState = REGISTRATION;

        spawnMainEventNpc();

        String name = settings.getEventName();

        Announcements announce = Announcements.getInstance();

        announce.criticalAnnounceToAll(name + ": Event " + name + "!");

        Item rewardTemplate = ItemTable.getInstance().getTemplate(generalSetting.getReward().getId());

        if (Config.CTF_ANNOUNCE_REWARD && rewardTemplate != null) {
            announce.criticalAnnounceToAll(name + ": Reward: " + generalSetting.getReward().getAmount() + " " + rewardTemplate.getName());
        }

        announce.criticalAnnounceToAll(name + ": Recruiting levels: " + generalSetting.getMinLevel() + " to " + generalSetting.getMaxLevel());
        announce.criticalAnnounceToAll(name + ": Registration in " + generalSetting.getRegistrationLocationName() + ".");

        if (Config.CTF_COMMAND) {
            announcementService.criticalToAll(name + ": Commands .ctfjoin .ctfleave .ctfinfo!");
        }

        waiter(generalSetting.getTimeRegistration());*/
    }

    protected void spawnMainEventNpc() {
        NpcInfoShort npcInfo = settings.getMainNpc();

        NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(npcInfo.getId());

        try {
            Spawn spawn = new Spawn(npcTemplate);

            spawn.setLoc(npcInfo.getSpawnLocation());
            spawn.setRespawnDelay(1);
            SpawnTable.getInstance().addSpawn(spawn, false);
            spawn.doSpawn(true);

            Npc npc = spawn.getNpc();

            npc.getStatus().setHp(999999999);
            npc.setTitle(settings.getEventName());
            npc.isAggressive();
            npc.decayMe();
            npc.spawnMe(npc.getPosition());

            npc.broadcastPacket(
                    new MagicSkillUse(npc, npc, SkillEnum.Bishop.REPOSE.getId(), 1, 1, 1)
            );

            /* TODO switch (event) {
                case CTF -> spawn.getLastSpawn().isCtfMainNpc = true;
                case TVT -> spawn.getLastSpawn()._isEventMobTvT = true;
                case DM -> spawn.getLastSpawn()._isEventMobDM = true;
            }*/

            settings.setSpawnMainNpc(spawn);
        } catch (Exception e) {
            LOGGER.warning(settings.getEventName() + " spawnMainEventNpc() exception: " + e.getMessage());
        }
    }
    
    protected void announceCritical(String text) {
        announcementService.criticalToAll(settings.getEventName() + ": " + text);
    }

    protected void logInfo(String text) {
        LOGGER.info(settings.getEventName() + ": " + text);
    }

    protected void logError(String method, String text) {
        LOGGER.severe(settings.getEventName() + "." + method + "(): " + text);
    }

    protected abstract boolean customPreLaunchChecks();

/*


    protected void sitPlayer() {
        for (PlayerInstance player : players) {
            if (player != null) {
                if (player.isSitting()) {
                    player.standUp();
                } else {
                    player.stopMove(null, false);
                    player.abortAttack();
                    player.abortCast();
                    player.sitDown();
                }
            }
        }
    }

    protected abstract void customUnspawnEventNpc();

    protected void unspawnEventNpc() {
        customUnspawnEventNpc();

        Spawn spawnMainNpc = generalSetting.getSpawnMainNpc();

        if (spawnMainNpc == null || spawnMainNpc.getLastSpawn() == null) {
            return;
        }

        spawnMainNpc.getLastSpawn().deleteMe();
        spawnMainNpc.stopRespawn();

        SpawnTable.getInstance().deleteSpawn(spawnMainNpc, true);
    }

    protected void abortEvent() {
        unspawnEventNpc();
        restorePlayerData();

        if (eventState != REGISTRATION) {
            customAbortEvent();
            returnPlayer();
        }

        eventState = ABORT;

        Announcements.getInstance().criticalAnnounceToAll(generalSetting.getEventName() + ": Match aborted!");
    }

    protected void returnPlayer() {
        Announcements.getInstance().criticalAnnounceToAll(generalSetting.getEventName() + ": Teleport back to participation NPC in 20 seconds!");

        sitPlayer();

        ThreadPool.schedule(() -> {
            // TODO Реализовать возврат на исходную позицию перед эвентом.
            Location spawnPosition = generalSetting.getMainNpc().getSpawnPosition();

            for (PlayerInstance player : players) {
                if (player != null) {
                    if (player.isOnline()) {
                        player.teleToLocation(
                                spawnPosition.getX(),
                                spawnPosition.getY(),
                                spawnPosition.getZ(),
                                false
                        );
                    }
                }
            }

            restorePlayerData();
            sitPlayer();
        }, 20000);
    }

    protected void waiter(int intervalMinutes) {
        long interval = TimeUnit.MINUTES.toMillis(intervalMinutes);
        final long startWaiterTime = Chronos.currentTimeMillis();
        int seconds = (int) (interval / 1000);

        String eventName = generalSetting.getEventName();
        String registrationLocationName = generalSetting.getRegistrationLocationName();

        while (((startWaiterTime + interval) > Chronos.currentTimeMillis()) && eventState != ABORT) {
            seconds--; // Here because we don't want to see two time announce at the same time

            if (eventState == REGISTRATION || eventState == START || eventState == TELEPORTATION) {
                switch (seconds) {
                    case 3600: // 1 hour left
                    case 1800: // 30 minutes left
                    case 900: // 15 minutes left
                    case 600: // 10 minutes left
                    case 300: // 5 minutes left
                    case 240: // 4 minutes left
                    case 180: // 3 minutes left
                    case 120: // 2 minutes left
                    case 60: { // 1 minute left
                        if (seconds == 3600) {
                            removeOfflinePlayers();
                        }

                        if (eventState == REGISTRATION) {
                            Announcements.getInstance().criticalAnnounceToAll(eventName + ": Registration in " + registrationLocationName + "!");
                            Announcements.getInstance().criticalAnnounceToAll(eventName + ": " + (seconds / 60) + " minute(s) till registration close!");
                        } else if (eventState == START) {
                            Announcements.getInstance().criticalAnnounceToAll(eventName + ": " + (seconds / 60) + " minute(s) till event finish!");
                        }

                        break;
                    }
                    case 30: // 30 seconds left
                    case 15: // 15 seconds left
                    case 10: { // 10 seconds left
                        removeOfflinePlayers();
                        // fallthrou?
                    }
                    case 3: // 3 seconds left
                    case 2: // 2 seconds left
                    case 1: { // 1 seconds left
                        if (eventState == REGISTRATION) {
                            Announcements.getInstance().criticalAnnounceToAll(eventName + ": " + seconds + " second(s) till registration close!");
                        } else if (eventState == TELEPORTATION) {
                            Announcements.getInstance().criticalAnnounceToAll(eventName + ": " + seconds + " seconds(s) till start fight!");
                        } else if (eventState == START) {
                            Announcements.getInstance().criticalAnnounceToAll(eventName + ": " + seconds + " second(s) till event finish!");
                        }
                        break;
                    }
                }
            }

            long startOneSecondWaiterStartTime = Chronos.currentTimeMillis();

            // TODO Какая-то печаль, нужно в будущем разобраться.
            // Only the try catch with Thread.sleep(1000) give bad countdown on high wait times
            while ((startOneSecondWaiterStartTime + 1000) > Chronos.currentTimeMillis()) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    protected int getIntervalBetweenMatches() {
        final long actualTime = Chronos.currentTimeMillis();
        final long totalTime = actualTime + generalSetting.getIntervalBetweenMatches();
        final long interval = totalTime - actualTime;
        final int seconds = (int) (interval / 1000);
        return seconds / 60;
    }

    @Override
    public String getEventIdentifier() {
        return generalSetting.getEventName();
    }

    protected boolean checkMinPlayers() {
        return generalSetting.getMinPlayers() <= players.size();
    }

    public boolean checkMaxPlayers() {
        return generalSetting.getMaxPlayers() > players.size();
    }

    public boolean checkMaxLevel(int maxLevel) {
        return generalSetting.getMinLevel() < maxLevel;
    }

    public boolean checkMinLevel(int minLevel) {
        return generalSetting.getMaxLevel() > minLevel;
    }

    public String getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(String eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public GeneralSetting getGeneralSetting() {
        return generalSetting;
    }

    public State getEventState() {
        return eventState;
    }

    public List<PlayerInstance> getPlayers() {
        return players;
    }

    protected abstract void registerPlayer(PlayerInstance player, String teamName);

    protected abstract boolean customPreLaunchChecks();

    protected abstract boolean preLaunchSinglePlayerEventChecks();

    protected abstract boolean preLaunchTeamPlayEventChecks();

    protected abstract void startEvent();

    protected abstract void removeOfflinePlayers();

    protected abstract boolean teleportPlayer();

    protected abstract void startRegistrationPlayer();

    public abstract void loadData(int eventId);

    protected abstract void restorePlayerData();

    protected abstract void customAbortEvent();

    protected abstract void finishEvent();*/
}
