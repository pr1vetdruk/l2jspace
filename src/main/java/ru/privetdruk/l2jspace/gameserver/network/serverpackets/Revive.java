package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.model.WorldObject;

/**
 * format d
 */
public class Revive extends L2GameServerPacket {
    private final int _objectId;

    public Revive(WorldObject obj) {
        _objectId = obj.getId();
    }

    @Override
    protected final void writeImpl() {
        writeC(0x07);
        writeD(_objectId);
    }
}