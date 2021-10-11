package ru.privetdruk.l2jspace.gameserver.custom.service;

import ru.privetdruk.l2jspace.common.logging.CLogger;
import ru.privetdruk.l2jspace.common.pool.ConnectionPool;
import ru.privetdruk.l2jspace.config.custom.EventConfig;
import ru.privetdruk.l2jspace.gameserver.custom.builder.EventSettingBuilder;
import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;
import ru.privetdruk.l2jspace.gameserver.custom.model.NpcInfoShort;
import ru.privetdruk.l2jspace.gameserver.custom.model.Reward;
import ru.privetdruk.l2jspace.gameserver.custom.model.entity.EventWinnerEntity;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.*;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.location.SpawnLocation;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventService {
    protected static final CLogger LOGGER = new CLogger(EventService.class.getName());

    private static final String SELECT_ALL_FROM_EVENT_WINNER_BY_PLAYER_ID = "SELECT ew.* FROM event_winner ew WHERE ew.player_id = ?";
    private static final String SELECT_ALL_FROM_EVENT_WINNER_BY_EVENT_TYPE = "SELECT ew.* FROM event_winner ew WHERE ew.event_type = ? AND ew.status = ?";
    private static final String INSERT_INTO_EVENT_WINNER = "INSERT INTO event_winner (player_id, event_type, victory_date, status) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ALL_FROM_EVENT_BY_ID_AND_TYPE = "SELECT e.* FROM event e WHERE e.id = ? AND e.type = ?";
    private static final String RESET_EVENT_WINNER = "UPDATE event_winner SET status = 'NOT_ACTIVE' WHERE event_type = ? AND status = 'ACTIVE'";

    /**
     * Поиск ивентов, в которых победил игрок
     *
     * @param playerId Идентификатор игрока
     * @return Список ивентов
     */
    public List<EventWinnerEntity> findAllWonEvents(int playerId) {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_FROM_EVENT_WINNER_BY_PLAYER_ID)) {
            statement.setInt(1, playerId);

            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return null;
            }

            List<EventWinnerEntity> wonEvents = new ArrayList<>();

            do {
                wonEvents.add(new EventWinnerEntity(
                        resultSet.getLong("id"),
                        resultSet.getInt("player_id"),
                        EventType.valueOf(resultSet.getString("event_type")),
                        resultSet.getDate("victory_date").toLocalDate(),
                        EventWinnerStatus.valueOf(resultSet.getString("status"))
                ));
            } while (resultSet.next());

            resultSet.close();

            return wonEvents;
        } catch (Exception e) {
            LOGGER.error("An error occured while searching for events in which the player won", e);
            return null;
        }
    }

    /**
     * Найти всех победителей в ивенте
     *
     * @param eventType Тип ивента
     * @param status    Статус победы
     * @return Список победителей
     */
    public List<EventWinnerEntity> findAllWinnersInEvent(EventType eventType, EventWinnerStatus status) {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_FROM_EVENT_WINNER_BY_EVENT_TYPE)) {
            statement.setString(1, eventType.name());
            statement.setString(2, status.name());

            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return null;
            }

            List<EventWinnerEntity> wonEvents = new ArrayList<>();

            do {
                wonEvents.add(new EventWinnerEntity(
                        resultSet.getLong("id"),
                        resultSet.getInt("player_id"),
                        EventType.valueOf(resultSet.getString("event_type")),
                        resultSet.getDate("victory_date").toLocalDate(),
                        EventWinnerStatus.valueOf(resultSet.getString("status"))
                ));
            } while (resultSet.next());

            resultSet.close();

            return wonEvents;
        } catch (Exception e) {
            LOGGER.error("An error occurred while searching for winners in the event", e);
            return null;
        }
    }

    /**
     * Создать победителя ивента
     *
     * @param player    Победивший игрок
     * @param eventType Ивент, в котором победил
     */
    public void createEventWinner(Player player, EventType eventType) {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_INTO_EVENT_WINNER, Statement.RETURN_GENERATED_KEYS)) {
            EventWinnerEntity eventWinner = new EventWinnerEntity();
            eventWinner.setEventType(eventType);
            eventWinner.setPlayerId(player.getId());

            statement.setInt(1, eventWinner.getPlayerId());
            statement.setString(2, eventWinner.getEventType().name());
            statement.setDate(3, Date.valueOf(eventWinner.getVictoryDate()));
            statement.setString(4, eventWinner.getStatus().name());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                LOGGER.error("An error occured while saving the event winner: " + eventWinner);
                return;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    eventWinner.setId(generatedKeys.getLong(1));

                    List<EventWinnerEntity> wonEvents = player.getWonEvents();
                    if (wonEvents == null) {
                        wonEvents = new ArrayList<>();
                    }
                    wonEvents.add(eventWinner);

                } else {
                    LOGGER.error("An error occurred while saving the event winner. Could not determine record ID: " + eventWinner);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Could not read event_winner setting when loading player", e);
        }
    }

    /**
     * Сбросить статус победителя в ивенте
     *
     * @param eventType Тип ивента
     */
    public void resetEventWinners(EventType eventType) {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(RESET_EVENT_WINNER)) {
            statement.setString(1, eventType.name());

            statement.execute();
        } catch (Exception e) {
            LOGGER.error("Failed to reset winner status to NOT_ACTIVE", e);
        }
    }

    public EventSetting findEventSetting(int id, EventType type) {
        try (Connection connection = ConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SELECT_ALL_FROM_EVENT_BY_ID_AND_TYPE);
            statement.setInt(1, id);
            statement.setString(2, type.name());

            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                LOGGER.warn("Couldn't find settings for " + type.name());
                return null;
            }

            EventSetting settings = new EventSettingBuilder()
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
                    .setTimeRegistration(resultSet.getInt("time_registration"))
                    .setDurationTime(resultSet.getInt("duration_event"))
                    .setMinPlayers(resultSet.getInt("min_players"))
                    .setMaxPlayers(resultSet.getInt("max_players"))
                    .setIntervalBetweenMatches(resultSet.getLong("delay_next_event"))
                    .build();

            statement.close();
            resultSet.close();

            statement = connection.prepareStatement("SELECT ev.* FROM event_reward ev WHERE ev.event_id = ? ORDER BY ev.id ASC");
            statement.setInt(1, id);

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Reward reward = new Reward();

                reward.setId(resultSet.getInt("reward_id"));
                reward.setAmount(resultSet.getInt("reward_amount"));

                settings.getRewards().add(reward);
            }

            return settings;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void load() {
        if (EventConfig.CTF.ENABLED) {
            load(EventType.CTF, EventConfig.CTF.LAUNCH_TIMES);
        }

        if (EventConfig.LastEmperor.ENABLED) {
            load(EventType.LAST_EMPEROR, EventConfig.LastEmperor.LAUNCH_TIMES);
        }
    }

    private void load(EventType eventType, String[] times) {
        try {
            EventTaskService.getInstance().clearEventTasksByEventName("ALL");

            List<Integer> eventIdList = new ArrayList<>();
            ResultSet resultSet = null;

            try (Connection connection = ConnectionPool.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT id FROM event e WHERE e.type = ? order by e.loading_order")) {
                statement.setString(1, eventType.name());
                resultSet = statement.executeQuery();
                if (!resultSet.next()) {
                    LOGGER.warn(eventType.name() + ": Settings not found!");
                    return;
                }

                do {
                    eventIdList.add(resultSet.getInt("id"));
                } while (resultSet.next());
            } catch (Exception e) {
                LOGGER.error("An error occurred while reading event data!", e);
                return;
            } finally {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (SQLException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }

            if (eventType == EventType.CTF && EventConfig.CTF.LOADING_MODE == EventLoadingMode.RANDOMLY) {
                Collections.shuffle(eventIdList);
            }

            for (int timeIndex = 0, eventIdIndex = 0; timeIndex < times.length; timeIndex++, eventIdIndex++) {
                if (eventIdIndex == eventIdList.size()) {
                    eventIdIndex = 0;
                }

                EventEngine eventTask = eventType.getClazz().getDeclaredConstructor().newInstance();

                eventTask.loadData(eventIdList.get(eventIdIndex));

                if (eventTask.getEventState() != EventState.ERROR) {
                    String time = times[timeIndex];
                    eventTask.setEventStartTime(time);
                    EventTaskService.getInstance().registerNewEventTask(eventTask);
                    LOGGER.info(eventTask.getEventIdentifier() + ": starts at " + time);
                }
            }
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            LOGGER.error("Failed to load " + eventType, e);
        }
    }

    public static EventService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final EventService INSTANCE = new EventService();
    }
}
