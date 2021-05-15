package ru.privetdruk.l2jspace.gameserver.custom.engine;

import ru.privetdruk.l2jspace.common.util.StringUtil;
import ru.privetdruk.l2jspace.config.custom.EventConfig;
import ru.privetdruk.l2jspace.gameserver.custom.model.NpcInfoShort;
import ru.privetdruk.l2jspace.gameserver.custom.model.SkillEnum;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventPlayer;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventSetting;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventTeamType;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventType;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.TeamSetting;
import ru.privetdruk.l2jspace.gameserver.custom.service.AnnouncementService;
import ru.privetdruk.l2jspace.gameserver.custom.task.EventTask;
import ru.privetdruk.l2jspace.gameserver.custom.util.Chronos;
import ru.privetdruk.l2jspace.gameserver.data.manager.CastleManager;
import ru.privetdruk.l2jspace.gameserver.data.sql.SpawnTable;
import ru.privetdruk.l2jspace.gameserver.data.xml.ItemData;
import ru.privetdruk.l2jspace.gameserver.data.xml.NpcData;
import ru.privetdruk.l2jspace.gameserver.enums.MessageType;
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
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static ru.privetdruk.l2jspace.common.util.StringUtil.declensionWords;
import static ru.privetdruk.l2jspace.common.util.StringUtil.secondWords;
import static ru.privetdruk.l2jspace.config.custom.EventConfig.Engine.DELAY_BEFORE_TELEPORT;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState.*;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventTeamType.SHUFFLE;

public abstract class EventEngine implements EventTask {
    protected static final Logger LOGGER = Logger.getLogger(EventEngine.class.getName());

    protected static final List<EventEngine> eventTaskList = new ArrayList<>();
    protected static final AnnouncementService announcementService = AnnouncementService.getInstance();

    protected EventSetting settings = null;
    protected List<TeamSetting> teamSettings = new ArrayList<>();
    protected Map<Integer, EventPlayer> players = new ConcurrentHashMap<>();

    protected final EventType eventType;
    protected final EventTeamType teamMode;
    protected final boolean ON_START_UNSUMMON_PET;
    protected final boolean ON_START_REMOVE_ALL_EFFECTS;
    protected final boolean JOIN_CURSED_WEAPON;
    protected final boolean REMOVE_BUFFS_ON_DIE;
    protected EventState eventState;
    protected String eventStartTime;

    public EventEngine(EventType eventType,
                       EventTeamType teamMode,
                       boolean onStartUnsummonPet,
                       boolean onStartRemoveAllEffects,
                       boolean joinCursedWeapon,
                       boolean removeBuffsOnDie) {
        this.eventType = eventType;
        this.teamMode = teamMode;
        ON_START_UNSUMMON_PET = onStartUnsummonPet;
        ON_START_REMOVE_ALL_EFFECTS = onStartRemoveAllEffects;
        JOIN_CURSED_WEAPON = joinCursedWeapon;
        REMOVE_BUFFS_ON_DIE = removeBuffsOnDie;

        eventState = INACTIVE;
    }

    @Override
    public void run() {
        EventEngine active = findActive();

        if (active != null) {
            logError("You cannot run several events at the same time.");
            return;
        }

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
                        startEvent();
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
        restorePlayerData();
        returnPlayers();
    }

    protected void startEvent() {
        waiter(EventConfig.Engine.DELAY_BEFORE_START + 1);

        announceCritical("Вперед!");

        eventState = IN_PROGRESS;

        sitPlayers();

        waiter(MINUTES.toSeconds(settings.getDurationEvent()));
    }

    protected void abortEvent() {
        unspawnEventNpc();
        restorePlayerData();

        if (eventState != REGISTRATION) {
            abortCustom();
            returnPlayers();
        }

        eventState = ABORT;

        announceCritical("Ивент прерван!");
    }

    protected void returnPlayers() {
        announceCritical(format(
                "Все участники ивента будут возвращены обратно через %d %s.",
                EventConfig.Engine.DELAY_BEFORE_TELEPORT_RETURN,
                declensionWords(EventConfig.Engine.DELAY_BEFORE_TELEPORT_RETURN, secondWords)
        ));

        sitPlayers();

        waiter(EventConfig.Engine.DELAY_BEFORE_TELEPORT_RETURN);

        // TODO Реализовать возврат на исходную позицию перед ивентом.
        Location spawnLocation = settings.getMainNpc().getSpawnLocation();

        players.values().forEach(eventPlayer -> {
            Player player = eventPlayer.getPlayer();

            if (player.isOnline()) {
                player.teleToLocation(spawnLocation);
            }

            players.remove(player.getObjectId());

            sitPlayer(player);
        });
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
        players.values().forEach(eventPlayer -> {
            eventPlayer.getPlayer().setEventPlayer(null);
            restorePlayerDataCustom(eventPlayer);
        });
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

        announceCritical(format(
                "Все зарегистрированные игроки будут перемещены на ивент через %d %s.",
                DELAY_BEFORE_TELEPORT,
                declensionWords(DELAY_BEFORE_TELEPORT, StringUtil.secondWords)
        ));

        waiter(DELAY_BEFORE_TELEPORT);

        spawnOtherNpc();
        updatePlayerEventData();
        sitPlayers();

        for (EventPlayer eventPlayer : players.values()) {
            preTeleportPlayerChecks(eventPlayer.getPlayer());

            eventPlayer.getPlayer().teleportTo(
                    eventPlayer.getTeamSettings().getSpawnLocation(),
                    eventPlayer.getTeamSettings().getOffset()
            );
        }
    }

    private void updatePlayerEventData() {
        players.values().forEach(eventPlayer -> {
            eventPlayer.getPlayer().setEventPlayer(eventPlayer);
            updatePlayerEventDataCustom(eventPlayer);
        });
    }

    private void preTeleportPlayerChecks(Player player) {
        if (ON_START_UNSUMMON_PET && player.hasPet()) {
            Summon summon = player.getSummon();
            summon.stopAllEffects();
            summon.unSummon(player);
        }

        if (ON_START_REMOVE_ALL_EFFECTS) {
            player.stopAllEffects();
        }

        if (player.getParty() != null) {
            player.getParty().removePartyMember(player, MessageType.LEFT);
        }
    }

    private void sitPlayers() {
        players.values().stream()
                .map(EventPlayer::getPlayer)
                .forEach(this::sitPlayer);
    }

    private void sitPlayer(Player player) {
        if (player.isSitting()) {
            player.standUp();
        } else {
            player.abortAll(true);
            player.sitDown();
        }
    }

    private void preLaunchChecks() {
        if (eventState != INACTIVE
                || settings.getTimeRegistration() <= 0
                || !preLaunchChecksCustom()
                || isSiegesLaunched()
                || (eventType.isTeam() && teamSettings.isEmpty())) {
            eventState = ABORT;
        } else {
            eventState = READY_TO_START;
        }
    }

    private boolean checkBeforeTeleport() {
        int minPlayers = settings.getMinPlayers();

        if (players.size() < minPlayers) {
            String text = format("Увы! Не удалось собрать достаточное кол-во игроков для запуска ивента. Минимум: %d, Зарегистрировалось: %d.", minPlayers, players.size());
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

        announceCritical("Открыта регистрация на ивент!");

        Item rewardTemplate = ItemData.getInstance().getTemplate(settings.getReward().getId());

        if (EventConfig.Engine.ANNOUNCE_REWARD && rewardTemplate != null) {
            announceCritical(format("Награда за победу: %d %s", settings.getReward().getAmount(), rewardTemplate.getName()));
        }

        announceCritical(format("Уровни: %d - %d", settings.getMinLevel(), settings.getMaxLevel()));
        announceCritical("Зарегистрироваться можно в " + settings.getRegistrationLocationName());

        if (EventConfig.Engine.REGISTRATION_BY_COMMANDS) {
            announceCritical("Быстрые команды: .join .leave .info");
        }

        waiter(MINUTES.toSeconds(settings.getTimeRegistration()));
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

    protected void logError(String message) {
        LOGGER.severe(settings.getEventName() + ": " + message);
    }

    protected void waiter(long intervalSeconds) {
        long interval = SECONDS.toMillis(intervalSeconds);
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

                        long minutes = SECONDS.toMinutes(seconds);
                        String minutesWord = declensionWords(minutes, StringUtil.minuteWords);

                        if (eventState == REGISTRATION) {
                            announceCritical("Зарегистрироваться можно в " + settings.getRegistrationLocationName());
                            announceCritical(format("До закрытия регистрации осталось %d %s.", minutes, minutesWord));
                        } else if (eventState == IN_PROGRESS) {
                            announceCritical(format("До завершения ивента осталось %d %s.", minutes, minutesWord));
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
                        String secondsWord = declensionWords(seconds, StringUtil.secondWords);

                        if (eventState == REGISTRATION) {
                            announceCritical(format("До закрытия регистрации осталось %d %s.", seconds, secondsWord));
                        } else if (eventState == TELEPORTATION) {
                            announceCritical(format("Приготовьтесь! До начала ивента осталось %d %s.", seconds, secondsWord));
                        } else if (eventState == IN_PROGRESS) {
                            announceCritical(format("До завершения ивента осталось %d %s.", seconds, secondsWord));
                        }

                        break;
                    }
                }
            }

            long startOneSecondWaiterStartTime = Chronos.currentTimeMillis();

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
        if (player.isCursedWeaponEquipped() && !JOIN_CURSED_WEAPON) {
            player.sendMessage("С Cursed Weapon запрещено принимать участие в ивенте.");
            return false;
        }

        if (player.getStatus().getLevel() < settings.getMinLevel()) {
            player.sendMessage("У вас недостаточный уровень для участия в ивенте");
            return false;
        }

        if (player.getStatus().getLevel() > settings.getMaxLevel()) {
            player.sendMessage("Ваш уровень превышает допустимый для участия в ивенте.");
            return false;
        }

        if (Olympiad.getInstance().isRegistered(player) || player.isInOlympiadMode()) {
            player.sendMessage("Вы участвуете в олимпиаде. Регистрация запрещена.");
            return false;
        }

        if (players.containsKey(player.getObjectId())) {
            player.sendMessage("Вы уже участвуете в ивенте.");
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

        player.sendMessage("Слишком много игроков в команде \"" + teamName + "\".");

        return false;
    }

    public void leave(Player player) {
        players.remove(player.getObjectId());
    }

    public void exclude(Player player) {
        restorePlayerDataCustom(player.getEventPlayer());
        leave(player);
        player.setEventPlayer(null);
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

    public boolean isJoinCursedWeapon() {
        return JOIN_CURSED_WEAPON;
    }

    public boolean isRemoveBuffsOnDie() {
        return REMOVE_BUFFS_ON_DIE;
    }

    // TODO
    public abstract void loadData(int eventId);

    public abstract boolean isSitForced();

    protected abstract boolean preLaunchChecksCustom();

    protected abstract void restorePlayerDataCustom(EventPlayer eventPlayer);

    protected abstract void updatePlayerEventDataCustom(EventPlayer eventPlayer);

    protected abstract void spawnOtherNpc();

    protected abstract void unspawnNpcCustom();

    protected abstract void abortCustom();

    protected abstract void determineWinner();

    public abstract String configureMainPageContent(Player player);

    public abstract void register(Player player, String teamName);

    public abstract void revive(Player player, Player playerKiller);

    public void onDisconnect(Player player) {
        exclude(player);
        player.teleToLocation(settings.getMainNpc().getSpawnLocation());
    }
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
