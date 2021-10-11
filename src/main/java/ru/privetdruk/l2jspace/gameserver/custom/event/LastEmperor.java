package ru.privetdruk.l2jspace.gameserver.custom.event;

import ru.privetdruk.l2jspace.common.pool.ConnectionPool;
import ru.privetdruk.l2jspace.common.random.Rnd;
import ru.privetdruk.l2jspace.common.util.StringUtil;
import ru.privetdruk.l2jspace.config.custom.EventConfig;
import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;
import ru.privetdruk.l2jspace.gameserver.custom.model.Reward;
import ru.privetdruk.l2jspace.gameserver.custom.model.entity.EventWinnerEntity;
import ru.privetdruk.l2jspace.gameserver.custom.model.enums.SocialActionEnum;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.*;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.lastemperor.LastEmperorPlayer;
import ru.privetdruk.l2jspace.gameserver.custom.service.DoorService;
import ru.privetdruk.l2jspace.gameserver.custom.service.EventService;
import ru.privetdruk.l2jspace.gameserver.custom.util.Chronos;
import ru.privetdruk.l2jspace.gameserver.data.xml.ItemData;
import ru.privetdruk.l2jspace.gameserver.enums.TeamAura;
import ru.privetdruk.l2jspace.gameserver.model.World;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Item;
import ru.privetdruk.l2jspace.gameserver.model.location.Location;
import ru.privetdruk.l2jspace.gameserver.model.location.SpawnLocation;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ActionFailed;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static ru.privetdruk.l2jspace.common.util.StringUtil.declensionWords;
import static ru.privetdruk.l2jspace.config.custom.EventConfig.LastEmperor.PREPARE_FOR_BATTLE;
import static ru.privetdruk.l2jspace.config.custom.EventConfig.LastEmperor.TIME_TO_FIGHT;
import static ru.privetdruk.l2jspace.gameserver.custom.model.enums.SocialActionEnum.Npc.GLADNESS;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventBypass.JOIN;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventBypass.LEAVE;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState.*;
import static ru.privetdruk.l2jspace.gameserver.data.SkillTable.FrequentSkill.LARGE_FIREWORK;

public class LastEmperor extends EventEngine {
    private EventBorder eventBorder;
    private final int HEADING = 16000;
    private final int ARENA_CENTER_X = 149422;

    private final List<EventNpc> STATIC_NPCS = List.of(
            new EventNpc(0, 0, 0, eventType, 30872, null, "Guard", new SpawnLocation(149572, 45677, -3408, HEADING)),
            new EventNpc(0, 0, GLADNESS.getId(), eventType, 30868, "Sly Eye", "Tournament Manager", new SpawnLocation(ARENA_CENTER_X, 45703, -3408, HEADING)),
            new EventNpc(0, 0, 0, eventType, 30873, null, "Guard", new SpawnLocation(149272, 45677, -3408, HEADING)),
            new EventNpc(0, 0, 0, eventType, 35422, null, null, new SpawnLocation(ARENA_CENTER_X, 45600, -3408, HEADING)),
            new EventNpc(0, 0, 0, eventType, 35426, null, null, new SpawnLocation(149272, 45600, -3408, HEADING)),
            new EventNpc(0, 0, 0, eventType, 35424, null, null, new SpawnLocation(149572, 45600, -3408, HEADING)),
            new EventNpc(0, 0, 0, eventType, 35423, null, null, new SpawnLocation(148573, 46728, -3408, 0)), // base player 2
            new EventNpc(0, 0, 0, eventType, 35424, null, null, new SpawnLocation(150371, 46728, -3408, 32000)), // base player 1
            new EventNpc(LARGE_FIREWORK.getId(), 0, 0, eventType, 35062, "Flag", null, new SpawnLocation(148473, 47063, -3408, 48000)), // flag
            new EventNpc(LARGE_FIREWORK.getId(), 0, 0, eventType, 35062, "Flag", null, new SpawnLocation(148710, 47063, -3408, 48000)), // flag
            new EventNpc(LARGE_FIREWORK.getId(), 0, 0, eventType, 35062, "Flag", null, new SpawnLocation(148947, 47063, -3408, 48000)), // flag
            new EventNpc(LARGE_FIREWORK.getId(), 0, 0, eventType, 35062, "Flag", null, new SpawnLocation(149184, 47063, -3408, 48000)), // flag
            new EventNpc(LARGE_FIREWORK.getId(), 0, 0, eventType, 35062, "Flag", null, new SpawnLocation(ARENA_CENTER_X, 47063, -3408, 48000)), // flag
            new EventNpc(LARGE_FIREWORK.getId(), 0, 0, eventType, 35062, "Flag", null, new SpawnLocation(149659, 47063, -3408, 48000)), // flag
            new EventNpc(LARGE_FIREWORK.getId(), 0, 0, eventType, 35062, "Flag", null, new SpawnLocation(149896, 47063, -3408, 48000)), // flag
            new EventNpc(LARGE_FIREWORK.getId(), 0, 0, eventType, 35062, "Flag", null, new SpawnLocation(150133, 47063, -3408, 48000)), // flag
            new EventNpc(LARGE_FIREWORK.getId(), 0, 0, eventType, 35062, "Flag", null, new SpawnLocation(150371, 47063, -3408, 48000)), // flag
            new EventNpc(LARGE_FIREWORK.getId(), LARGE_FIREWORK.getId(), 0, eventType, 35469, "Trojan Horse", "Event Manager", new SpawnLocation(ARENA_CENTER_X, 46936, -3408, HEADING))
    );


    public LastEmperor() {
        super(
                EventType.LAST_EMPEROR,
                EventConfig.LastEmperor.TEAM_MODE,
                EventConfig.LastEmperor.UNSUMMON_PET,
                EventConfig.LastEmperor.REMOVE_ALL_EFFECTS,
                EventConfig.LastEmperor.JOIN_CURSED_WEAPON,
                EventConfig.LastEmperor.REMOVE_BUFFS_ON_DIE
        );

        eventTaskList.add(this);
    }

    @Override
    protected void startEventCustom() {
        while (players.size() > 1) {
            Queue<PairOfRivals> pairOfRivals = identifyPairOfRivals();

            while (pairOfRivals.peek() != null) {
                announceCritical("Осталось участников: " + players.size());
                PairOfRivals pair = pairOfRivals.poll();

                if (!pair.presentBothPlayers()) {
                    EventPlayer winner = pair.determineWinnerWithoutFight();

                    if (winner != null) {
                        announceCritical("Игроку <" + winner.getPlayer().getName() + "> не нашлось соперника.");
                        continue;
                    }
                }

                announceCritical(String.format(
                        "Игроку %s и %s приготовиться к бою!",
                        pair.getPlayer1().getPlayer().getName(),
                        pair.getPlayer2().getPlayer().getName()
                ));

                eventState = PREPARE_FOR_START;
                pair.preparePlayersBeforeStart();
                waiter(PREPARE_FOR_BATTLE, "До начала боя осталось %d %s.");

                eventState = FIGHT;
                announceCritical("Бой!");
                pair.allowAttack();

                waiter(TIME_TO_FIGHT, "До конца боя осталось  %d %s.");

                completeRound(pair);
            }
        }
    }

    private void completeRound(PairOfRivals pair) {
        eventState = PREPARE_FOR_NEXT_ROUND;

        EventPlayer winner = pair.determineWinner();
        EventPlayer loser = pair.determineLoser();

        Player winnerPlayer = winner.getPlayer();

        winnerPlayer.performSocialAction(SocialActionEnum.AMAZING_GLOW);
        winnerPlayer.performSocialAction(SocialActionEnum.VICTORY);

        announceCritical("Статистика боя:");
        announceDamage(pair.getPlayer1());
        announceDamage(pair.getPlayer2());

        pair.preparePlayersBeforeNextRound();

        if (players.size() > 2) {
            announceCritical("В следующий раунд попадает игрок <" + winnerPlayer.getName() + ">");

            STATIC_NPCS.forEach(EventNpc::playRoundVictoryAnimation);

            waiter(5);

            winnerPlayer.sitDown();

            waiter(3);

            winnerPlayer.getPosition().setHeading(HEADING);
            teleport(winner);
        }

        exclude(loser.getPlayer());
    }

    private void announceDamage(EventPlayer player) {
        announceCritical("Игрок <" + player.getPlayer().getName() + "> нанес " + player.getDamageDone() + " урона");
    }

    private void waiter(long intervalSeconds, String message) {
        long interval = SECONDS.toMillis(intervalSeconds);
        final long startWaiterTime = Chronos.currentTimeMillis();
        int seconds = (int) intervalSeconds;

        while (((startWaiterTime + interval) > Chronos.currentTimeMillis()) && eventState != WINNER_IS_DETERMINED) {
            if (message != null) {

                switch (seconds) {
                    case 300, 240, 180, 120, 60 -> {
                        long minutes = SECONDS.toMinutes(seconds);
                        String minutesWord = declensionWords(minutes, StringUtil.MINUTE_WORDS);

                        announceCritical(format(message, minutes, minutesWord));
                    }
                    case 30, 15, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 -> {
                        String secondsWord = declensionWords(seconds, StringUtil.SECOND_WORDS);

                        announceCritical(format(message, seconds, secondsWord));
                    }
                }
            }

            seconds--;

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

    private void waiter(int seconds) {
        long interval = SECONDS.toMillis(seconds);
        final long startWaiterTime = Chronos.currentTimeMillis();

        while (((startWaiterTime + interval) > Chronos.currentTimeMillis())) {
            seconds--;
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

    @Override
    protected void announceStart() {
        announceCritical("Да начнётся битва!");
    }

    @Override
    public void loadData(int eventId) {
        try (Connection connection = ConnectionPool.getConnection()) {
            settings = EventService.getInstance().findEventSetting(eventId, eventType);

            if (settings == null) {
                return;
            }

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM event_last_emperor_team_setting WHERE event_id = ? ORDER BY id");
            statement.setInt(1, eventId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                teamSettings.add(
                        new TeamSetting(
                                null,
                                null,
                                0,
                                new Location(
                                        resultSet.getInt("position_x"),
                                        resultSet.getInt("position_y"),
                                        resultSet.getInt("position_z")
                                )
                        )
                );
            }

            resultSet.close();

            if (settings.getMaxPlayers() > teamSettings.size()) {
                throw new Exception("There are not enough settings in the table for the specified event setting value maximum number of players:" +
                        "\nevent.max_playes = " + settings.getMaxPlayers() +
                        "\nevent_last_emperor_team_settings count(*) = " + teamSettings.size());
            }
        } catch (Exception e) {
            eventState = ERROR;
            logError("loadData", e);
        }
    }

    @Override
    public boolean isSitForced() {
        return true;
    }

    @Override
    protected boolean preLaunchChecksCustom() {
        return true;
    }

    @Override
    protected void restorePlayerDataCustom(EventPlayer eventPlayer) {
        eventPlayer.setAllowedToWalk(true);
    }

    @Override
    protected void updatePlayerEventDataCustom(EventPlayer eventPlayer) {

    }

    @Override
    protected void spawnOtherNpc() {
        DoorService.close(24190002); // todo CONST
        DoorService.close(24190003);

        STATIC_NPCS.forEach(EventNpc::spawn);
    }

    @Override
    protected void unspawnNpcCustom() {
        DoorService.open(24190002); // todo CONST
        DoorService.open(24190003);

        STATIC_NPCS.forEach(EventNpc::unspawn);
    }

    @Override
    protected void cancelEventCustom() {

    }

    @Override
    protected void determineWinner() {
        STATIC_NPCS.forEach(EventNpc::playVictoryAnimation);

        players.values().stream()
                .map(EventPlayer::getPlayer)
                .forEach(player -> {
                    announceCritical("Победителем турнира Последний Император становится игрок <" + player.getName() + ">. Поздравляем!");
                    giveReward(player);
                });

        waiter(10);
    }

    private void giveReward(Player player) {
        int randomRewardIndex = Rnd.get(0, settings.getRewards().size() - 1);

        Reward reward = settings.getRewards().get(randomRewardIndex);

        Item rewardTemplate = ItemData.getInstance().getTemplate(reward.getId());

        announceCritical("Награда за победу: " + rewardTemplate.getName() + " " + reward.getAmount() + "шт.");

        player.addItem(
                settings.getEventName(),
                reward.getId(),
                reward.getAmount(),
                player,
                true
        );

        player.setTopRank(true);
        player.setTitle("[TOP RANK 1x1]");
        player.setTitleColor(Integer.decode("0x00D7FF"));
        player.store();

        EventService eventService = EventService.getInstance();

        List<EventWinnerEntity> allWinnersInEvent = eventService.findAllWinnersInEvent(EventType.LAST_EMPEROR, EventWinnerStatus.ACTIVE);
        eventService.resetEventWinners(EventType.LAST_EMPEROR);

        if (allWinnersInEvent != null && allWinnersInEvent.size() > 1) {
            allWinnersInEvent.forEach(event -> {
                Player winnerPlayer = World.getInstance().getPlayer(event.getPlayerId());
                if (winnerPlayer != null && winnerPlayer.isOnline()) {
                    winnerPlayer.setTopRank(false);
                    winnerPlayer.setWonEvents(eventService.findAllWonEvents(winnerPlayer.getId()));
                    winnerPlayer.broadcastUserInfo();
                }
            });
        }

        eventService.createEventWinner(player, EventType.LAST_EMPEROR);

        player.broadcastUserInfo();
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }

    @Override
    public void register(Player player) {
        if (!checkPlayerBeforeRegistration(player)) {
            return;
        }

        players.put(player.getId(), new LastEmperorPlayer(player, null));

        sendPlayerMessage(player, "Вы успешно зарегистрировались на ивент.");
    }

    @Override
    public void register(Player player, String teamName) {
    }

    @Override
    public void addDisconnectedPlayer(Player player) {

    }

    @Override
    public String configureMainPageContent(Player player) {
        StringBuilder content = new StringBuilder();

        int playerLevel = player.getStatus().getLevel();

        if (eventState == REGISTRATION && playerLevel >= settings.getMinLevel() && playerLevel <= settings.getMaxLevel()) {
            EventPlayer eventPlayer = players.get(player.getId());

            if (eventPlayer != null) {
                content.append("<center><font color=\"3366CC\">Вы уже принимаете участие!</font></center><br><br>");


                content.append("<center>Участников: <font color=\"00FF00\">").append(players.size()).append("</font></center><br>");
                content.append("<center><font color=\"3366CC\">Дождитесь начала ивента или откажитесь от участия!</font><center>");
                content.append("<center><button value=\"Покинуть\" action=\"bypass -h npc_%objectId%_")
                        .append(LEAVE.getBypass())
                        .append("\" width=\"100\" height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></center>");
            } else {
                content.append("<center><font color=\"3366CC\">Вы хотите принять участие в ивенте?</font></center><br>");
                content.append("<center><td width=\"200\">Минимальный уровень: <font color=\"00FF00\">").append(settings.getMinLevel()).append("</font></center></td><br>");
                content.append("<center><td width=\"200\">Максимальный уровень: <font color=\"00FF00\">").append(settings.getMaxLevel()).append("</font></center></td><br><br>");
                content.append("<center>Участников: <font color=\"00FF00\">").append(players.size()).append("</font></center><br>");
                content.append("<center><button value=\"Участвовать\" action=\"bypass -h npc_%objectId%_")
                        .append(JOIN.getBypass())
                        .append(" eventShuffle\" width=\"100\" height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></center>");
            }
        } else if (eventState == IN_PROGRESS) {
            content.append("<center>К сожалению ивент ").append(settings.getEventName()).append(" уже начался.</center>");
        } else if (playerLevel < settings.getMinLevel() || playerLevel > settings.getMaxLevel()) {
            content.append("Ваш уровень: <font color=\"00FF00\">").append(playerLevel).append("</font><br>");
            content.append("Минимальный уровень: <font color=\"00FF00\">").append(settings.getMinLevel()).append("</font><br>");
            content.append("Максимальный уровень: <font color=\"00FF00\">").append(settings.getMaxLevel()).append("</font><br><br>");
            content.append("<font color=\"FFFF00\">Вы не можете участвовать в этом ивенте.</font><br>");
        }

        return content.toString();
    }

    @Override
    public void doDie(Player player, Player playerKiller) {
        sendPlayerMessage(player, "Вы проиграли, но не стоит расстраиваться, в следующий раз вы будете сильнее!");

        LastEmperorPlayer eventKiller = (LastEmperorPlayer) playerKiller.getEventPlayer();
        eventKiller.setWinner(true);

        eventState = WINNER_IS_DETERMINED;
    }

    @Override
    public boolean isAllowedTeleportAfterDeath() {
        return true;
    }

    @Override
    protected void announceRewardsAfter() {
        announceCritical("Выдан будет только один предмет случайным образом.");
    }

    private Queue<PairOfRivals> identifyPairOfRivals() {
        Queue<PairOfRivals> pairOfRivalsQueue = new ArrayDeque<>();

        PairOfRivals pairOfRivals = null;

        for (EventPlayer player : players.values()) {
            if (pairOfRivals == null) {
                pairOfRivals = new PairOfRivals();
                pairOfRivals.setPlayer1((LastEmperorPlayer) player);
                pairOfRivalsQueue.add(pairOfRivals);
            } else {
                pairOfRivals.setPlayer2((LastEmperorPlayer) player);
                pairOfRivals = null;
            }
        }

        return pairOfRivalsQueue;
    }

    private static class PairOfRivals {
        private LastEmperorPlayer eventPlayer1;
        private LastEmperorPlayer eventPlayer2;

        public LastEmperorPlayer getPlayer1() {
            return eventPlayer1;
        }

        public void setPlayer1(LastEmperorPlayer player1) {
            this.eventPlayer1 = player1;
        }

        public LastEmperorPlayer getPlayer2() {
            return eventPlayer2;
        }

        public void setPlayer2(LastEmperorPlayer player2) {
            this.eventPlayer2 = player2;
        }

        /**
         * Проверка присутствия игроков
         *
         * @return true если оба игрока присутствуют на ивенте
         */
        public boolean presentBothPlayers() {
            return eventPlayer1 != null
                    && eventPlayer2 != null
                    && eventPlayer1.getPlayer().isOnline()
                    && eventPlayer2.getPlayer().isOnline();
        }

        /**
         * Подготовить игроков перед началом боя
         */
        public void preparePlayersBeforeStart() {
            eventPlayer1.setRival(eventPlayer2);
            eventPlayer2.setRival(eventPlayer1);

            Player player1 = eventPlayer1.getPlayer();
            Player player2 = eventPlayer2.getPlayer();

            setAllowedToWalk(true);

            player1.setTeamAura(TeamAura.BLUE);
            player2.setTeamAura(TeamAura.RED);

            player1.getPosition().setHeading(32000);
            player2.getPosition().setHeading(0);

            player1.teleToLocation(new Location(150171, 46728, -3408));
            player2.teleToLocation(new Location(148773, 46728, -3408));

            player1.broadcastUserInfo();
            player2.broadcastUserInfo();

            player1.standUp();
            player2.standUp();
        }

        private void setAllowedToWalk(boolean allowedToWalk) {
            eventPlayer1.setAllowedToWalk(allowedToWalk);
            eventPlayer2.setAllowedToWalk(allowedToWalk);
        }

        /**
         * Сбросить данные боя перед следующим раундом
         */
        public void preparePlayersBeforeNextRound() {
            setCanAttack(false);
            setAllowedToWalk(false);
            
            eventPlayer1.setRival(null);
            eventPlayer2.setRival(null);

            eventPlayer1.resetDamage();
            eventPlayer2.resetDamage();

            Player player1 = eventPlayer1.getPlayer();
            Player player2 = eventPlayer2.getPlayer();

            player1.setTeamAura(TeamAura.NONE);
            player2.setTeamAura(TeamAura.NONE);

            player1.resetCooldownSkills();
            player2.resetCooldownSkills();

            player1.broadcastCharInfo();
            player2.broadcastCharInfo();
        }

        private void setCanAttack(boolean canAttack) {
            eventPlayer1.setCanAttack(canAttack);
            eventPlayer2.setCanAttack(canAttack);
        }

        /**
         * Определить победившего без боя (когда не удалось подобраться соперника или он вышел, или вышли оба)
         *
         * @return Победителя иначе null
         */
        public EventPlayer determineWinnerWithoutFight() {
            if (eventPlayer1 != null && eventPlayer1.getPlayer().isOnline()) {
                return eventPlayer1;
            } else if (eventPlayer2 != null && eventPlayer2.getPlayer().isOnline()) {
                return eventPlayer2;
            } else {
                return null;
            }
        }

        /**
         * Определить победителя
         *
         * @return Победитель
         */
        public EventPlayer determineWinner() {
            if (eventPlayer1.isWinner()) {
                return eventPlayer1;
            } else if (eventPlayer2.isWinner()) {
                return eventPlayer2;
            } else {
                return eventPlayer1.getDamageDone() >= eventPlayer2.getDamageDone() ? eventPlayer1 : eventPlayer2;
            }
        }

        /**
         * Определить проигравшего
         *
         * @return Проигравший
         */
        public EventPlayer determineLoser() {
            if (eventPlayer1.isWinner()) {
                return eventPlayer2;
            } else if (eventPlayer2.isWinner()) {
                return eventPlayer1;
            } else {
                return eventPlayer1.getDamageDone() >= eventPlayer2.getDamageDone() ? eventPlayer2 : eventPlayer1;
            }
        }

        /**
         * Разрешить атаковать
         */
        public void allowAttack() {
            setCanAttack(true);
        }
    }
}
