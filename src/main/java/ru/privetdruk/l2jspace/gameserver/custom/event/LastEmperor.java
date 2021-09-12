package ru.privetdruk.l2jspace.gameserver.custom.event;

import ru.privetdruk.l2jspace.common.pool.ConnectionPool;
import ru.privetdruk.l2jspace.common.util.StringUtil;
import ru.privetdruk.l2jspace.config.custom.EventConfig;
import ru.privetdruk.l2jspace.gameserver.custom.builder.EventSettingBuilder;
import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;
import ru.privetdruk.l2jspace.gameserver.custom.model.NpcInfoShort;
import ru.privetdruk.l2jspace.gameserver.custom.model.Reward;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventBorder;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventPlayer;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventType;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.TeamSetting;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.lastemperor.LastEmperorPlayer;
import ru.privetdruk.l2jspace.gameserver.custom.service.DoorService;
import ru.privetdruk.l2jspace.gameserver.custom.util.Chronos;
import ru.privetdruk.l2jspace.gameserver.enums.AuraTeamType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.location.Location;
import ru.privetdruk.l2jspace.gameserver.model.location.SpawnLocation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayDeque;
import java.util.Queue;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static ru.privetdruk.l2jspace.common.util.StringUtil.declensionWords;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventBypass.JOIN;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventBypass.LEAVE;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState.*;

public class LastEmperor extends EventEngine {
    private EventBorder eventBorder;

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
                pair.configurePlayersBeforeStart();
                waiter(15, "До начала боя осталось %d %s."); // todo конфиг

                pair.setRivals();
                eventState = FIGHT;
                announceCritical("Бой!");

                waiter(120, "До конца боя осталось  %d %s."); // todo конфиг

                completeRound(pair);
            }
        }

        players.values().forEach(eventPlayer ->
                announceCritical("Победителем турнира Последний Император становится игрок <" + eventPlayer.getPlayer().getName() + ">. Поздравляем!")
        );
    }

    private void completeRound(PairOfRivals pair) {
        EventPlayer winner = pair.determineWinner();
        EventPlayer loser = pair.determineLoser();

        announceCritical("Статистика боя:");
        announceDamage(pair.getPlayer1());
        announceDamage(pair.getPlayer2());

        playAnimation(winner.getPlayer(), true);

        pair.clearBeforeNextRound();

        if (players.size() > 2) {
            announceCritical("В следующий раунд попадает игрок <" + winner.getPlayer().getName() + ">");

            waiter(5, null);

            winner.getPlayer().sitDown();
            winner.setAllowedToWalk(false);
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
        int seconds = (int) (interval / 1000);

        while (((startWaiterTime + interval) > Chronos.currentTimeMillis()) && eventState != WINNER_IS_DETERMINED) {
            if (message != null) {
                String secondsWord = declensionWords(seconds, StringUtil.secondWords);
                switch (seconds) {
                    case 120, 30, 15, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 -> announceCritical(format(message, seconds, secondsWord));
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

    @Override
    protected void announceStart() {
        announceCritical("Да начнётся битва!");
    }

    @Override
    public void loadData(int eventId) {
        try (Connection connection = ConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM event WHERE id = ? AND type = ?");
            statement.setInt(1, eventId);
            statement.setString(2, eventType.name());

            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                LOGGER.warning("Не удалось найти настройки для CTF.");
                return;
            }

            settings = new EventSettingBuilder()
                    .setName(resultSet.getString("name"))
                    .setDescription(resultSet.getString("description"))
                    .setRegistrationLocationName(resultSet.getString("registration_location"))
                    .setMinLevel(resultSet.getInt("min_level"))
                    .setMaxLevel(resultSet.getInt("max_level"))
                    .setNpc(new NpcInfoShort(
                            resultSet.getInt("npc_id"),
                            new SpawnLocation(
                                    resultSet.getInt("npc_x"),
                                    resultSet.getInt("npc_y"),
                                    resultSet.getInt("npc_z"),
                                    resultSet.getInt("npc_heading")
                            )
                    ))
                    .setReward(new Reward(
                            resultSet.getInt("reward_id"),
                            resultSet.getInt("reward_amount")
                    ))
                    .setTimeRegistration(resultSet.getInt("time_registration"))
                    .setDurationTime(resultSet.getInt("duration_event"))
                    .setMinPlayers(resultSet.getInt("min_players"))
                    .setMaxPlayers(resultSet.getInt("max_players"))
                    .setIntervalBetweenMatches(resultSet.getLong("delay_next_event"))
                    .build();

            statement.close();
            resultSet.close();

            statement = connection.prepareStatement("SELECT * FROM event_last_emperor_team_setting WHERE event_id = ? ORDER BY id");
            statement.setInt(1, eventId);
            resultSet = statement.executeQuery();

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
    }

    @Override
    protected void unspawnNpcCustom() {
        DoorService.open(24190002); // todo CONST
        DoorService.open(24190003);
    }

    @Override
    protected void abortCustom() {

    }

    @Override
    protected void determineWinner() {

    }

    @Override
    public void register(Player player) {
        if (!checkPlayerBeforeRegistration(player)) {
            return;
        }

        players.put(player.getObjectId(), new LastEmperorPlayer(player, null));

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
            EventPlayer eventPlayer = players.get(player.getObjectId());

            if (eventPlayer != null) {
                content.append("<center><font color=\"3366CC\">Вы уже принимаете участие!</font></center><br><br>");


                content.append("<center>Участников: <font color=\"00FF00\">").append(players.size()).append("</font></center><br>");
                content.append("<center><font color=\"3366CC\">Дождитесь начала ивента или откажитесь от участия!</font><center>");
                content.append("<center><button value=\"Покинуть\" action=\"bypass -h npc_%objectId%_")
                        .append(LEAVE.getBypass())
                        .append("\" width=\"90\" height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></center>");
            } else {
                content.append("<center><font color=\"3366CC\">Вы хотите принять участие в ивенте?</font></center><br>");
                content.append("<center><td width=\"200\">Минимальный уровень: <font color=\"00FF00\">").append(settings.getMinLevel()).append("</font></center></td><br>");
                content.append("<center><td width=\"200\">Максимальный уровень: <font color=\"00FF00\">").append(settings.getMaxLevel()).append("</font></center></td><br><br>");
                content.append("<center>Участников: <font color=\"00FF00\">").append(players.size()).append("</font></center><br>");
                content.append("<center><button value=\"Участвовать\" action=\"bypass -h npc_%objectId%_")
                        .append(JOIN.getBypass())
                        .append(" eventShuffle\" \"90\" height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></center>");
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

        public boolean presentBothPlayers() {
            return eventPlayer1 != null
                    && eventPlayer2 != null
                    && eventPlayer1.getPlayer().isOnline()
                    && eventPlayer2.getPlayer().isOnline();
        }

        public void configurePlayersBeforeStart() {
            Player player1 = eventPlayer1.getPlayer();
            Player player2 = eventPlayer2.getPlayer();

            eventPlayer1.setAllowedToWalk(true);
            eventPlayer2.setAllowedToWalk(true);

            player1.setAura(AuraTeamType.BLUE);
            player2.setAura(AuraTeamType.RED);

            player1.teleToLocation(new Location(150171, 46728, -3408));
            player2.teleToLocation(new Location(148765, 46728, -3408));

            player1.standUp();
            player2.standUp();
        }

        public void setRivals() {
            eventPlayer1.setRival(eventPlayer2);
            eventPlayer2.setRival(eventPlayer1);
        }

        public void clearBeforeNextRound() {
            eventPlayer1.setRival(null);
            eventPlayer2.setRival(null);

            eventPlayer1.getPlayer().setAura(AuraTeamType.NONE);
            eventPlayer2.getPlayer().setAura(AuraTeamType.NONE);

            eventPlayer1.resetDamage();
            eventPlayer2.resetDamage();
        }

        public EventPlayer determineWinnerWithoutFight() {
            if (eventPlayer1 != null && eventPlayer1.getPlayer().isOnline()) {
                return eventPlayer1;
            } else if (eventPlayer2 != null && eventPlayer2.getPlayer().isOnline()) {
                return eventPlayer2;
            } else {
                return null;
            } 
        }

        public EventPlayer determineWinner() {
            if (eventPlayer1.isWinner()) {
                return eventPlayer1;
            } else if (eventPlayer2.isWinner()) {
                return eventPlayer2;
            } else {
                return eventPlayer1.getDamageDone() >= eventPlayer2.getDamageDone() ? eventPlayer1 : eventPlayer2;
            }
        }

        public EventPlayer determineLoser() {
            if (eventPlayer1.isWinner()) {
                return eventPlayer2;
            } else if (eventPlayer2.isWinner()) {
                return eventPlayer1;
            } else {
                return eventPlayer1.getDamageDone() >= eventPlayer2.getDamageDone() ? eventPlayer2 : eventPlayer1;
            }
        }
    }
}
