package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.enums.skills.FlyType;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;

public final class FlyToLocation extends L2GameServerPacket {
    private final int _chaObjId;
    private final int _chaX;
    private final int _chaY;
    private final int _chaZ;
    private final int _destX;
    private final int _destY;
    private final int _destZ;
    private final FlyType _type;

    public FlyToLocation(Creature cha, int destX, int destY, int destZ, FlyType type) {
        _chaObjId = cha.getObjectId();
        _chaX = cha.getX();
        _chaY = cha.getY();
        _chaZ = cha.getZ();
        _destX = destX;
        _destY = destY;
        _destZ = destZ;
        _type = type;
    }

    public FlyToLocation(Creature cha, WorldObject dest, FlyType type) {
        this(cha, dest.getX(), dest.getY(), dest.getZ(), type);
    }

    @Override
    protected void writeImpl() {
        writeC(0xC5);
        writeD(_chaObjId);
        writeD(_destX);
        writeD(_destY);
        writeD(_destZ);
        writeD(_chaX);
        writeD(_chaY);
        writeD(_chaZ);
        writeD(_type.ordinal());
    }
}