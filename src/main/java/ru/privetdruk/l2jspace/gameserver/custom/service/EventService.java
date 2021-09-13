package ru.privetdruk.l2jspace.gameserver.custom.service;

import ru.privetdruk.l2jspace.common.pool.ConnectionPool;
import ru.privetdruk.l2jspace.config.custom.EventConfig;
import ru.privetdruk.l2jspace.gameserver.custom.builder.EventSettingBuilder;
import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;
import ru.privetdruk.l2jspace.gameserver.custom.model.NpcInfoShort;
import ru.privetdruk.l2jspace.gameserver.custom.model.Reward;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventLoadingMode;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventSetting;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventType;
import ru.privetdruk.l2jspace.gameserver.model.location.SpawnLocation;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class EventService {
    private static final Logger LOGGER = Logger.getLogger(EventService.class.getName());

    public EventSetting findEventSetting(int id, EventType type) {
        try (Connection connection = ConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT e.* FROM event e WHERE e.id = ? AND e.type = ?");
            statement.setInt(1, id);
            statement.setString(2, type.name());

            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                LOGGER.warning("Не удалось найти настройки для " + type.name());
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
                    LOGGER.warning(eventType.name() + ": Settings not found!");
                    return;
                }

                do {
                    eventIdList.add(resultSet.getInt("id"));
                } while (resultSet.next());
            } catch (Exception e) {
                LOGGER.severe("An error occurred while reading event data!");
                return;
            } finally {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (SQLException e) {
                        LOGGER.warning(e.getMessage());
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
            LOGGER.severe("Failed to load " + eventType);
        }
    }

    public static EventService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final EventService INSTANCE = new EventService();
    }
}
