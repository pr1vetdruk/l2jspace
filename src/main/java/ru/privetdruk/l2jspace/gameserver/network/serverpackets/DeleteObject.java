package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.model.WorldObject;

public class DeleteObject extends L2GameServerPacket {
    private final int _objectId;
    private final boolean _isSeated;

    public DeleteObject(WorldObject obj) {
        _objectId = obj.getId();
        _isSeated = false;
    }

    public DeleteObject(WorldObject obj, boolean sit) {
        _objectId = obj.getId();
        _isSeated = sit;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x12);
        writeD(_objectId);
        writeD(_isSeated ? 0x00 : 0x01); // 0 - stand up and delete, 1 - delete
    }
}