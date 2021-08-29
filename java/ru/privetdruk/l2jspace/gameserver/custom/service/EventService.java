package ru.privetdruk.l2jspace.gameserver.custom.service;

import ru.privetdruk.l2jspace.common.logging.CLogger;
import ru.privetdruk.l2jspace.common.pool.ConnectionPool;
import ru.privetdruk.l2jspace.config.custom.EventConfig;
import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventLoadingMode;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventType;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventService {
    private static final CLogger LOGGER = new CLogger(EventService.class.getName());

    public static EventService getInstance() {
        return EventService.SingletonHolder.INSTANCE;
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
                LOGGER.error("An error occurred while reading event data!");
                return;
            } finally {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (SQLException e) {
                        LOGGER.warn(e.getMessage());
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
            LOGGER.error("Failed to load " + eventType);
        }
    }

    private EventService() {
        load();
    }

    private static class SingletonHolder {
        protected static final EventService INSTANCE = new EventService();
    }
}
