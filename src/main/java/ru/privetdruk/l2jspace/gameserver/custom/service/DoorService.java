package ru.privetdruk.l2jspace.gameserver.custom.service;

import ru.privetdruk.l2jspace.gameserver.data.xml.DoorData;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Door;

public class DoorService {
    public static void open(int doorId) {
        Door door = DoorData.getInstance().getDoor(doorId);
        door.changeState(true, false);
    }

    public static void close(int doorId) {
        Door door = DoorData.getInstance().getDoor(doorId);
        door.changeState(false, false);
    }
}
