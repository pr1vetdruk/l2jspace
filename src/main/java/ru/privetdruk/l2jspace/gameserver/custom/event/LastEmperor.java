package ru.privetdruk.l2jspace.gameserver.custom.event;

import ru.privetdruk.l2jspace.common.pool.ConnectionPool;
import ru.privetdruk.l2jspace.config.custom.EventConfig;
import ru.privetdruk.l2jspace.gameserver.custom.builder.EventSettingBuilder;
import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;
import ru.privetdruk.l2jspace.gameserver.custom.model.NpcInfoShort;
import ru.privetdruk.l2jspace.gameserver.custom.model.Reward;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventBorder;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventPlayer;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventType;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.lastemperor.LastEmperorPlayer;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.location.SpawnLocation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventBypass.JOIN;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventBypass.LEAVE;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState.ERROR;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState.IN_PROGRESS;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState.REGISTRATION;

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

    }

    @Override
    protected void updatePlayerEventDataCustom(EventPlayer eventPlayer) {

    }

    @Override
    protected void spawnOtherNpc() {

    }

    @Override
    protected void unspawnNpcCustom() {

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
    public void revive(Player player, Player playerKiller) {
        sendPlayerMessage(player, "Вы проиграли, но не стоит расстраиваться, в следующий раз вы будете сильнее!");
    }
}
