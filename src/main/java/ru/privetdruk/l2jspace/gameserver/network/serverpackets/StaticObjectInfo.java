package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.instance.StaticObject;

/**
 * format dd
 */
public class StaticObjectInfo extends L2GameServerPacket {
    private final StaticObject _staticObject;

    public StaticObjectInfo(StaticObject staticObject) {
        _staticObject = staticObject;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x99);
        writeD(_staticObject.getStaticObjectId());
        writeD(_staticObject.getObjectId());
    }
}