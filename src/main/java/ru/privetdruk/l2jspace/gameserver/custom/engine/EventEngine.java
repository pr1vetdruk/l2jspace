package ru.privetdruk.l2jspace.gameserver.custom.engine;

import ru.privetdruk.l2jspace.common.util.StringUtil;
import ru.privetdruk.l2jspace.config.custom.EventConfig;
import ru.privetdruk.l2jspace.gameserver.custom.model.NpcInfoShort;
import ru.privetdruk.l2jspace.gameserver.custom.model.SkillEnum;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.*;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.CtfTeamSetting;
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
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SocialAction;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static ru.privetdruk.l2jspace.common.util.StringUtil.declensionWords;
import static ru.privetdruk.l2jspace.common.util.StringUtil.SECOND_WORDS;
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
    protected Map<Integer, EventPlayer> allPlayers;

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
            clearPlayers();

            logInfo("Notification start.");

            preLaunchChecks();

            switch (eventState) {
                case ABORT -> logInfo("Failed to start the event because failed to pass prelaunch checks.");
                case READY_TO_START -> {
                    spawnNpcForRegistration();
                    registration();

                    if (checkBeforeTeleport()) {
                        teleport();
                        startEvent();
                        finishEvent();
                    } else {
                        cancelEvent();
                    }
                }
                default -> logInfo("Failed to start the event because the state of the event is incorrect.");
            }
        } catch (Exception e) {
            logError("run", e);
        } finally {
            eventState = INACTIVE;
            clearPlayers();
        }
    }

    private void clearPlayers() {
        players.clear();

        if (allPlayers != null) {
            allPlayers.clear();
        }
    }

    public static boolean isCanAttack(Player player, Player targetPlayer) {
        if (player.isEventPlayer() && targetPlayer.isEventPlayer()) {
            EventPlayer eventPlayer = player.getEventPlayer();
            EventPlayer targetEventPlayer = targetPlayer.getEventPlayer();

            EventPlayer playerRival = eventPlayer.getRival();
            EventPlayer targetPlayerRival = targetEventPlayer.getRival();

            return ((playerRival == targetEventPlayer && targetPlayerRival == eventPlayer) || (playerRival == null && targetPlayerRival == null))
                    && eventPlayer.getTeamSettings() != targetEventPlayer.getTeamSettings()
                    && eventPlayer.isCanAttack();
        }

        return false;
    }

    protected void finishEvent() {
        eventState = FINISH;

        determineWinner();
        unspawnNpcCustom();
        unspawnNpcForRegistration();
        restorePlayerData();
        returnPlayers();
    }

    protected void startEvent() {
        eventState = PREPARE_FOR_START;

        allPlayers = new HashMap<>(players);

        waiter(EventConfig.Engine.DELAY_BEFORE_START + 1);

        announceStart();

        eventState = IN_PROGRESS;

        startEventCustom();

        waiter(MINUTES.toSeconds(settings.getDurationEvent()));
    }

    protected abstract void startEventCustom();

    protected void cancelEvent() {
        eventState = ABORT;

        unspawnNpcForRegistration();
        cancelEventCustom();

        announceCritical("Ивент прерван!");
    }

    protected void returnPlayers() {
        announceCritical(format(
                "Все участники ивента будут возвращены обратно через %d %s.",
                EventConfig.Engine.DELAY_BEFORE_TELEPORT_RETURN,
                declensionWords(EventConfig.Engine.DELAY_BEFORE_TELEPORT_RETURN, SECOND_WORDS)
        ));

        sitPlayers();

        waiter(EventConfig.Engine.DELAY_BEFORE_TELEPORT_RETURN);

        // TODO Реализовать возврат на исходную позицию перед ивентом.
        Location spawnLocation = settings.getMainNpc().getSpawnLocation();

        players.values().forEach(eventPlayer -> {
            Player player = eventPlayer.getPlayer();

            if (player.isOnline()) {
                player.teleportTo(spawnLocation, eventPlayer.getTeamSettings().getOffset());
            }

            players.remove(player.getId());

            sitPlayer(player);
        });
    }

    protected void unspawnNpcForRegistration() {
        Spawn registerNpc = settings.getRegisterNpc();

        if (registerNpc == null || registerNpc.getNpc() == null) {
            return;
        }

        registerNpc.getNpc().deleteMe();
        registerNpc.setRespawnState(false);

        SpawnTable.getInstance().deleteSpawn(registerNpc, true);
    }

    protected void restorePlayerData() {
        players.values().forEach(eventPlayer -> {
            eventPlayer.getPlayer().setEventPlayer(null);
            restorePlayerDataCustom(eventPlayer);
        });
    }

    protected void playAnimation(Player player, boolean isWinner) {
        if (player != null) {
            player.broadcastPacket(new SocialAction(player, isWinner ? 3 : 7));
        }
    }

    public void teleport() {
        if (eventState == ABORT) {
            return;
        }

        eventState = TELEPORTATION;

        prohibitPlayersToMove();

        shufflePlayers();

        removeOfflinePlayers();

        announceCritical(format(
                "Все участники ивента будут телепортированы на ивент через %d %s.",
                DELAY_BEFORE_TELEPORT,
                declensionWords(DELAY_BEFORE_TELEPORT, StringUtil.SECOND_WORDS)
        ));

        spawnOtherNpc();
        updatePlayerEventData();
        sitPlayers();

        waiter(DELAY_BEFORE_TELEPORT);

        players.values().forEach(this::teleport);
    }

    /**
     * Запретить игрокам передвижение
     */
    protected void prohibitPlayersToMove() {
        players.values().forEach(eventPlayer -> eventPlayer.setAllowedToWalk(false));
    }

    /**
     * Разрешить игрокам передвижение
     */
    protected void allowPlayersToMove() {
        players.values().forEach(eventPlayer -> eventPlayer.setAllowedToWalk(true));
    }


    protected void teleport(EventPlayer eventPlayer) {
        Player player = eventPlayer.getPlayer();

        preTeleportPlayerChecks(player);

        player.teleportTo(
                eventPlayer.getTeamSettings().getSpawnLocation(),
                eventPlayer.getTeamSettings().getOffset()
        );
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

    protected void sitPlayers() {
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

        announceRewards();

        announceCritical(format("Уровни: %d - %d", settings.getMinLevel(), settings.getMaxLevel()));
        announceCritical("Зарегистрироваться можно в " + settings.getRegistrationLocationName());

        if (EventConfig.Engine.REGISTRATION_BY_COMMANDS) {
            announceCritical("Быстрые команды: .join .leave .info");
        }

        waiter(MINUTES.toSeconds(settings.getTimeRegistration()));
    }

    private void announceRewards() {
        if (EventConfig.Engine.ANNOUNCE_REWARD && !settings.getRewards().isEmpty()) {
            announceCritical("Награда за победу:");

            settings.getRewards().forEach(reward -> {
                Item rewardTemplate = ItemData.getInstance().getTemplate(reward.getId());
                announceCritical(reward.getAmount() + "шт. " + rewardTemplate.getName());
            });

            announceRewardsAfter();
        }
    }

    protected void spawnNpcForRegistration() throws ClassNotFoundException, NoSuchMethodException {
        NpcInfoShort npcInfo = settings.getMainNpc();
        NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(npcInfo.getId());

        try {
            Spawn spawn = new Spawn(npcTemplate);
            spawn.setLoc(npcInfo.getSpawnLocation());
            spawn.setRespawnDelay(1);
            SpawnTable.getInstance().addSpawn(spawn, false);
            spawn.doSpawn(true);

            Npc npc = spawn.getNpc();
            npc.setMortal(false);
            npc.setTitle(settings.getEventName());
            npc.isAggressive();
            npc.decayMe();
            npc.spawnMe(npc.getPosition());

            npc.broadcastPacket(
                    new MagicSkillUse(npc, npc, SkillEnum.Bishop.REPOSE.getId(), 1, 1, 1)
            );

            settings.setSpawnMainNpc(spawn);
        } catch (Exception e) {
            logError("spawnMainNpc", e);
            throw e;
        }
    }

    protected void shufflePlayers() {
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
        announcementService.criticalToAll(message);
    }

    protected void logInfo(String message) {
        LOGGER.info(settings.getEventName() + ": " + message);
    }

    protected void logError(String method, Exception e) {
        LOGGER.severe(settings.getEventName() + "." + method + "(): " + e.getMessage());
        e.printStackTrace();
    }

    protected void logError(String message) {
        LOGGER.severe(settings.getEventName() + ": " + message);
    }

    protected void waiter(long intervalSeconds) {
        long interval = SECONDS.toMillis(intervalSeconds);
        final long startWaiterTime = Chronos.currentTimeMillis();
        int seconds = (int) intervalSeconds;

        while (((startWaiterTime + interval) > Chronos.currentTimeMillis()) && eventState != ABORT) {
            seconds--; // Here because we don't want to see two time announce at the same time

            switch (eventState) {
                case REGISTRATION, IN_PROGRESS, TELEPORTATION, PREPARE_FOR_START -> {
                    switch (seconds) {
                        case 3600, 1800, 900, 600, 300, 240, 180, 120, 60 -> {
                            if (seconds == 3600) {
                                removeOfflinePlayers();
                            }

                            long minutes = SECONDS.toMinutes(seconds);
                            String minutesWord = declensionWords(minutes, StringUtil.MINUTE_WORDS);

                            if (eventState == REGISTRATION) {
                                announceCritical("Зарегистрироваться можно в " + settings.getRegistrationLocationName());
                                announceCritical(format("До закрытия регистрации осталось %d %s.", minutes, minutesWord));
                            } else if (eventState == IN_PROGRESS) {
                                announceCritical(format("До завершения ивента осталось %d %s.", minutes, minutesWord));
                            }
                        }
                        case 30, 15, 10, 5, 4, 3, 2, 1 -> {
                            if (seconds < 2 || seconds > 5) {
                                removeOfflinePlayers();
                            }

                            String secondsWord = declensionWords(seconds, StringUtil.SECOND_WORDS);
                            String message = null;

                            switch (eventState) {
                                case PREPARE_FOR_START -> message = "До начала ивента осталось %d %s.";
                                case REGISTRATION -> message = "До закрытия регистрации осталось %d %s.";
                                case TELEPORTATION -> {
                                    if (seconds == 1) {
                                        secondsWord = "секунду";
                                    }
                                    message = "Все участники будут телепортированы через %d %s.";
                                }
                                case IN_PROGRESS -> message = "До завершения ивента осталось %d %s.";
                            }

                            if (message != null) {
                                announceCritical(format(message, seconds, secondsWord));
                            }
                        }
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
                        players.remove(eventPlayer.getPlayer().getId());
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

        if (players.containsKey(player.getId())) {
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
        players.remove(player.getId());
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

    protected abstract void announceStart();

    public abstract void loadData(int eventId);

    public abstract boolean isSitForced();

    protected abstract boolean preLaunchChecksCustom();

    protected abstract void restorePlayerDataCustom(EventPlayer eventPlayer);

    protected abstract void updatePlayerEventDataCustom(EventPlayer eventPlayer);

    protected abstract void spawnOtherNpc();

    protected abstract void unspawnNpcCustom();

    protected abstract void cancelEventCustom();

    protected abstract void determineWinner();

    public abstract void register(Player player);

    public abstract void register(Player player, String teamName);

    public abstract void addDisconnectedPlayer(Player player);

    public abstract String configureMainPageContent(Player player);

    public abstract void doDie(Player player, Player playerKiller);

    public abstract boolean isAllowedTeleportAfterDeath();

    protected abstract void announceRewardsAfter();

    public void onDisconnect(Player player) {
        exclude(player);
        player.teleToLocation(settings.getMainNpc().getSpawnLocation());
    }
}
