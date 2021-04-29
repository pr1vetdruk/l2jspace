package ru.privetdruk.l2jspace.gameserver.custom.engine;

import ru.privetdruk.l2jspace.common.pool.ThreadPool;
import ru.privetdruk.l2jspace.config.custom.EventConfig;
import ru.privetdruk.l2jspace.gameserver.custom.model.NpcInfoShort;
import ru.privetdruk.l2jspace.gameserver.custom.model.SkillEnum;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventPlayer;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventSetting;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventTeamType;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventType;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.TeamSetting;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.CtfTeamSetting;
import ru.privetdruk.l2jspace.gameserver.custom.service.AnnouncementService;
import ru.privetdruk.l2jspace.gameserver.custom.task.EventTask;
import ru.privetdruk.l2jspace.gameserver.custom.util.Chronos;
import ru.privetdruk.l2jspace.gameserver.data.manager.CastleManager;
import ru.privetdruk.l2jspace.gameserver.data.sql.SpawnTable;
import ru.privetdruk.l2jspace.gameserver.data.xml.ItemData;
import ru.privetdruk.l2jspace.gameserver.data.xml.NpcData;
import ru.privetdruk.l2jspace.gameserver.enums.MessageType;
import ru.privetdruk.l2jspace.gameserver.handler.AdminCommandHandler;
import ru.privetdruk.l2jspace.gameserver.model.World;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.Summon;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.model.entity.Castle;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Item;
import ru.privetdruk.l2jspace.gameserver.model.location.Location;
import ru.privetdruk.l2jspace.gameserver.model.olympiad.Olympiad;
import ru.privetdruk.l2jspace.gameserver.model.spawn.Spawn;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.MagicSkillUse;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static ru.privetdruk.l2jspace.config.custom.EventConfig.Engine.WAIT_TELEPORT_SECONDS;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState.*;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventTeamType.SHUFFLE;

public abstract class EventEngine implements EventTask {
    protected static final Logger LOGGER = Logger.getLogger(EventEngine.class.getName());

    protected static final List<EventEngine> eventTaskList = new ArrayList<>();
    protected static final AnnouncementService announcementService = AnnouncementService.getInstance();

    protected EventSetting settings = null;
    protected List<TeamSetting> teamSettings = null;
    protected final Map<Integer, EventPlayer> players = new ConcurrentHashMap<>();

    protected EventType eventType;
    protected EventTeamType teamMode;
    protected EventState eventState;
    protected String eventStartTime;
    protected boolean onStartUnsummonPet;
    protected boolean onStartRemoveAllEffects;

    public EventEngine(EventType eventType,
                       EventTeamType teamMode,
                       boolean onStartUnsummonPet,
                       boolean onStartRemoveAllEffects) {
        this.eventType = eventType;
        this.teamMode = teamMode;
        this.onStartUnsummonPet = onStartUnsummonPet;
        this.onStartRemoveAllEffects = onStartRemoveAllEffects;

        eventState = INACTIVE;
    }

    @Override
    public void run() {
        try {
            players.clear();

            logInfo("Notification start.");

            preLaunchChecks();

            switch (eventState) {
                case ABORT -> logInfo("Failed to start the event because failed to pass prelaunch checks.");
                case READY_TO_START -> {
                    spawnMainEventNpc();
                    registration();
                    if (checkBeforeTeleport()) {
                        teleport();
                        waiter(1);
                        startEvent();
                        waiter(settings.getDurationEvent());
                        finishEvent();
                    } else {
                        abortEvent();
                    }
                }
                default -> logInfo("Failed to start the event because the state of the event is incorrect.");
            }
        } catch (Exception e) {
            logError("run", e.getMessage());
        } finally {
            eventState = INACTIVE;
        }
    }

    protected void finishEvent() {
        if (eventState != IN_PROGRESS) {
            return;
        }

        eventState = FINISH;

        unspawnEventNpc();
        determineWinner();
        returnPlayer();
    }

    protected void startEvent() {
        announceCritical("Started!");

        eventState = IN_PROGRESS;

        sitPlayer();
    }

    protected void abortEvent() {
        unspawnEventNpc();
        restorePlayerData();

        if (eventState != REGISTRATION) {
            abortCustom();
            returnPlayer();
        }

        eventState = ABORT;

        announceCritical("Match aborted!");
    }

    protected void returnPlayer() {
        announceCritical("Teleport back to participation NPC in 20 seconds!");

        sitPlayer();

        ThreadPool.schedule(() -> {
            // TODO Реализовать возврат на исходную позицию перед эвентом.
            Location spawnLocation = settings.getMainNpc().getSpawnLocation();

            players.values().stream()
                    .map(EventPlayer::getPlayer)
                    .filter(Objects::nonNull)
                    .filter(Player::isOnline)
                    .forEach(player -> player.teleToLocation(spawnLocation));

            restorePlayerData();
            sitPlayer();
        }, TimeUnit.SECONDS.toMillis(20));
    }

    protected void unspawnEventNpc() {
        unspawnNpcCustom();

        Spawn spawnMainNpc = settings.getSpawnMainNpc();

        if (spawnMainNpc == null || spawnMainNpc.getNpc() == null) {
            return;
        }

        spawnMainNpc.getNpc().deleteMe();
        spawnMainNpc.setRespawnState(false);

        SpawnTable.getInstance().deleteSpawn(spawnMainNpc, true);
    }

    protected void restorePlayerData() {
        players.values().stream()
                .filter(eventPlayer -> eventPlayer.getPlayer() != null)
                .forEach(this::restorePlayerDataCustom);
    }

    public void teleport() {
        if (eventState == ABORT) {
            return;
        }

        eventState = TELEPORTATION;

        if (eventType.isTeam()) {
            shuffleTeams();
        }

        removeOfflinePlayers();

        announceCritical(String.format("Teleport to spot in %d seconds!", WAIT_TELEPORT_SECONDS));

        ThreadPool.schedule(() -> {
            updatePlayerEventData();
            sitPlayer();
            spawnOtherNpc();

            for (EventPlayer eventPlayer : players.values()) {
                Player player = eventPlayer.getPlayer();

                if (player != null) {
                    preTeleportPlayerChecks(player);

                    TeamSetting playerTeamSettings = eventPlayer.getTeamSettings();
                    player.teleportTo(playerTeamSettings.getSpawnLocation(), playerTeamSettings.getOffset());
                }
            }
        }, TimeUnit.SECONDS.toMillis(WAIT_TELEPORT_SECONDS));
    }

    private void preTeleportPlayerChecks(Player player) {
        if (onStartUnsummonPet && player.hasPet()) {
            Summon summon = player.getSummon();
            summon.stopAllEffects();
            summon.unSummon(player);
        }

        if (onStartRemoveAllEffects) {
            player.stopAllEffects();
        }

        if (player.getParty() != null) {
            player.getParty().removePartyMember(player, MessageType.LEFT);
        }
    }

    private void sitPlayer() {
        for (EventPlayer eventPlayer : players.values()) {
            Player player = eventPlayer.getPlayer();

            if (player != null) {
                if (player.isSitting()) {
                    player.standUp();
                } else {
                    player.abortAll(true);
                    player.sitDown();
                }
            }
        }
    }

    private void preLaunchChecks() {
        if (eventState != INACTIVE
                || settings.getTimeRegistration() <= 0
                || !preLaunchChecksCustom()
                || isSiegesLaunched()) {
            eventState = ABORT;
        } else {
            eventState = READY_TO_START;
        }
    }

    private boolean checkBeforeTeleport() {
        int minPlayers = settings.getMinPlayers();

        if (players.size() < minPlayers) {
            String text = String.format("Not enough players for event. Min requested: %d, participating: %d.", minPlayers, players.size());
            announceCritical(text);

            if (EventConfig.Engine.LOG_STATISTICS) {
                logInfo(text);
            }

            return false;
        }

        return true;
    }

    private boolean isSiegesLaunched() {
        for (Castle castle : CastleManager.getInstance().getCastles()) {
            if (castle != null && castle.getSiege() != null && castle.getSiege().isInProgress()) {
                return true;
            }
        }

        return false;
    }

    private void registration() {
        eventState = REGISTRATION;

        String name = settings.getEventName();

        announceCritical("Registration for the event is open");

        Item rewardTemplate = ItemData.getInstance().getTemplate(settings.getReward().getId());

        if (EventConfig.Engine.ANNOUNCE_REWARD && rewardTemplate != null) {
            announceCritical(String.format("Reward: %d %s", settings.getReward().getAmount(), rewardTemplate.getName()));
        }

        announceCritical(String.format("Levels: %d - %d", settings.getMinLevel(), settings.getMaxLevel()));
        announceCritical("Registration in " + settings.getRegistrationLocationName());

        if (EventConfig.Engine.REGISTRATION_BY_COMMANDS) {
            announcementService.criticalToAll(name + ": Commands .join .leave .info");
        }

        waiter(settings.getTimeRegistration());
    }

    protected void spawnMainEventNpc() throws ClassNotFoundException, NoSuchMethodException {
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
            logError("spawnMainEventNpc", e.getMessage());
            throw e;
        }
    }

    protected void shuffleTeams() {
        if (teamMode != SHUFFLE) {
            return;
        }

        List<EventPlayer> playersShuffle = new ArrayList<>(players.values());
        Collections.shuffle(playersShuffle);

        int teamIndex = 0;

        for (EventPlayer player : players.values()) {
            player.setTeamSettings(teamSettings.get(teamIndex));

            if (teamIndex == (teamSettings.size() - 1)) {
                teamIndex = 0;
            } else {
                teamIndex++;
            }
        }
    }

    protected void sendPlayerMessage(Player player, String message) {
        player.sendMessage(settings.getEventName() + ": " + message);
    }

    protected void announceCritical(String message) {
        announcementService.criticalToAll(settings.getEventName() + ": " + message);
    }

    protected void logInfo(String message) {
        LOGGER.info(settings.getEventName() + ": " + message);
    }

    protected void logError(String method, String message) {
        LOGGER.severe(settings.getEventName() + "." + method + "(): " + message);
    }

    protected void waiter(int intervalMinutes) {
        long interval = TimeUnit.MINUTES.toMillis(intervalMinutes);
        final long startWaiterTime = Chronos.currentTimeMillis();
        int seconds = (int) (interval / 1000);

        while (((startWaiterTime + interval) > Chronos.currentTimeMillis()) && eventState != ABORT) {
            seconds--; // Here because we don't want to see two time announce at the same time

            if (eventState == REGISTRATION || eventState == IN_PROGRESS || eventState == TELEPORTATION) {
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
                            announceCritical("Registration in " + settings.getRegistrationLocationName());
                            announceCritical((seconds / 60) + " minute(s) till registration close");
                        } else if (eventState == IN_PROGRESS) {
                            announceCritical((seconds / 60) + " minute(s) till event finish!");
                        }

                        break;
                    }
                    case 30: // 30 seconds left
                    case 15: // 15 seconds left
                    case 10: { // 10 seconds left
                        removeOfflinePlayers();
                        // fallthrou?
                    }
                    case 1: { // 1 seconds left
                        if (eventState == REGISTRATION) {
                            announceCritical("Registration close!");
                        } else if (eventState == TELEPORTATION) {
                            announceCritical("Teleported!");
                        } else if (eventState == IN_PROGRESS) {
                            announceCritical("Event finish!");
                        }

                        break;
                    }
                }
            }

            long startOneSecondWaiterStartTime = Chronos.currentTimeMillis();

            // TODO Какая-то дичь, нужно в будущем разобраться.
            // Only the try catch with Thread.sleep(1000) give bad countdown on high wait times
            while ((startOneSecondWaiterStartTime + 1000) > Chronos.currentTimeMillis()) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    protected void removeOfflinePlayers() {
        if (players.isEmpty()) {
            return;
        }

        try {
            players.values().stream()
                    .filter(eventPlayer -> eventPlayer.getPlayer() != null)
                    .filter(eventPlayer -> !eventPlayer.getPlayer().isOnline()
                            || eventPlayer.getPlayer().isInJail()
                            || eventPlayer.getPlayer().getOfflineStartTime() > 0)
                    .forEach(eventPlayer -> {
                        restorePlayerDataCustom(eventPlayer);
                        players.remove(eventPlayer.getPlayer().getObjectId());
                    });
        } catch (Exception e) {
            LOGGER.warning(e.getMessage());
        }
    }

    protected boolean checkPlayerBeforeRegistration(Player player) {
        if (player.isCursedWeaponEquipped()) {
            player.sendMessage("You are not allowed to participate to the event because you are holding a Cursed Weapon.");
            return false;
        }

        if (player.getStatus().getLevel() < settings.getMinLevel()) {
            player.sendMessage("You are not allowed to participate to the event because your level is too low.");
            return false;
        }

        if (player.getStatus().getLevel() > settings.getMaxLevel()) {
            player.sendMessage("You are not allowed to participate to the event because your level is too high.");
            return false;
        }

        if (player.getKarma() > 0) {
            player.sendMessage("You are not allowed to participate to the event because you have Karma.");
            return false;
        }

        if (Olympiad.getInstance().isRegistered(player) || player.isInOlympiadMode()) {
            player.sendMessage("You already participated in Olympiad!");
            return false;
        }


        if (players.get(player.getObjectId()) != null) {
            player.sendMessage("You already participated in the event!");
            return false;
        }

        return true;
    }

    protected boolean checkTeamBeforeRegistration(Player player, String teamName) {
        switch (teamMode) {
            case NO:
            case SHUFFLE:
                return true;
            case BALANCE:
                boolean allTeamsEqual = true;

                int playersCount = teamSettings.get(0).getPlayers();

                for (TeamSetting team : teamSettings) {
                    if (playersCount != team.getPlayers()) {
                        allTeamsEqual = false;
                        break;
                    }

                    playersCount = team.getPlayers();
                }

                if (allTeamsEqual) {
                    return true;
                }

                int minPlayersCount = teamSettings.stream()
                        .map(TeamSetting::getPlayers)
                        .min(Comparator.naturalOrder())
                        .orElse(Integer.MIN_VALUE);

                for (TeamSetting team : teamSettings) {
                    if (team.getPlayers() == minPlayersCount && team.getName().equals(teamName)) {
                        return true;
                    }
                }

                break;
        }

        player.sendMessage("Too many players in team \"" + teamName + "\"");

        return false;
    }

    public void excludePlayer(Player player) {
        players.remove(player.getObjectId());
    }

    public static EventEngine findActive() {
        return eventTaskList.stream()
                .filter(event -> event.eventState != INACTIVE)
                .findFirst()
                .orElse(null);
    }

    public TeamSetting findTeam(String name) {
        return teamSettings.stream()
                .filter(team -> team.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(String eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    @Override
    public String getEventIdentifier() {
        return settings.getEventName();
    }

    public EventState getEventState() {
        return eventState;
    }

    public EventType getEventType() {
        return eventType;
    }

    public EventTeamType getTeamMode() {
        return teamMode;
    }

    public Map<Integer, EventPlayer> getPlayers() {
        return players;
    }

    public EventSetting getSettings() {
        return settings;
    }

    // TODO
    public abstract void loadData(int eventId);

    protected abstract boolean preLaunchChecksCustom();

    protected abstract void restorePlayerDataCustom(EventPlayer player);

    protected abstract void updatePlayerEventData();

    protected abstract void spawnOtherNpc();

    protected abstract void unspawnNpcCustom();

    protected abstract void abortCustom();

    protected abstract void determineWinner();

    public abstract String configurePageContent(Player player);

    public abstract void registerPlayer(Player player, String teamName);
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
