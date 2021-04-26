package ru.privetdruk.l2jspace.gameserver.custom.service;

import ru.privetdruk.l2jspace.common.logging.CLogger;
import ru.privetdruk.l2jspace.common.pool.ConnectionPool;
import ru.privetdruk.l2jspace.config.custom.event.EventConfig;
import ru.privetdruk.l2jspace.gameserver.GameServer;
import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;
import ru.privetdruk.l2jspace.gameserver.custom.event.CTF;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventLoadingMode;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
            load(CTF.class, EventConfig.CTF.LAUNCH_TIMES);
        }
    }

    private void load(Class<? extends EventEngine> eventClass, String[] times) {
        try {


            EventTaskService.getInstance().clearEventTasksByEventName("ALL");

            List<Integer> eventIdList = new ArrayList<>();

            try (Connection connection = ConnectionPool.getConnection()) {
                PreparedStatement statement;
                ResultSet resultSet;

                statement = connection.prepareStatement("SELECT id FROM event e WHERE e.type = 'CTF' order by e.loading_order");
                resultSet = statement.executeQuery();

                if (!resultSet.next()) {
                    LOGGER.warn("Settings not found!");
                    return;
                }

                do {
                    eventIdList.add(resultSet.getInt("id"));
                } while (resultSet.next());
            } catch (Exception e) {
                LOGGER.error("An error occurred while reading event data!");
                return;
            }

            if (eventClass == CTF.class && EventConfig.CTF.LOADING_MODE == EventLoadingMode.RANDOMLY) {
                Collections.shuffle(eventIdList);
            }

            for (int timeIndex = 0, eventIdIndex = 0; timeIndex < times.length; timeIndex++, eventIdIndex++) {
                if (eventIdIndex == eventIdList.size()) {
                    eventIdIndex = 0;
                }

                EventEngine eventTask = eventClass.getDeclaredConstructor().newInstance();

                eventTask.loadData(eventIdList.get(eventIdIndex));

                if (eventTask.getEventState() != EventState.ERROR) {
                    String time = times[timeIndex];
                    eventTask.setEventStartTime(time);
                    EventTaskService.getInstance().registerNewEventTask(eventTask);
                    LOGGER.info(eventTask.getEventIdentifier() + ": starts at " + time);
                }
            }
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            LOGGER.error("Failed to load " + eventClass.getName());
        }
    }

    private EventService() {
        load();
    }

    private static class SingletonHolder {
        protected static final EventService INSTANCE = new EventService();
    }
}
