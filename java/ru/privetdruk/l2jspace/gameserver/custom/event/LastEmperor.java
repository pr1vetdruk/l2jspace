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
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.location.SpawnLocation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState.ERROR;

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
    public void register(Player player, String teamName) {

    }

    @Override
    public void addDisconnectedPlayer(Player player) {

    }

    @Override
    public String configureMainPageContent(Player player) {
        return null;
    }

    @Override
    public void revive(Player player, Player playerKiller) {

    }
}
