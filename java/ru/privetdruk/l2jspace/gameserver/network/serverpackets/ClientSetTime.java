package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.taskmanager.GameTimeTaskManager;

public class ClientSetTime extends L2GameServerPacket {
    @Override
    protected final void writeImpl() {
        writeC(0xEC);
        writeD(GameTimeTaskManager.getInstance().getGameTime());
        writeD(6);
    }
}