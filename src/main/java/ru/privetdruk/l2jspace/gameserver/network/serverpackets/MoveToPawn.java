package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;

/**
 * This packet is used to move characters to a target.<br>
 * It is aswell used to rotate characters in front of the target.
 */
public class MoveToPawn extends L2GameServerPacket {
    private final int _objectId;
    private final int _targetId;
    private final int _distance;
    private final int _x;
    private final int _y;
    private final int _z;

    public MoveToPawn(Creature creature, WorldObject target, int distance) {
        _objectId = creature.getId();
        _targetId = target.getId();
        _distance = distance;
        _x = creature.getX();
        _y = creature.getY();
        _z = creature.getZ();
    }

    @Override
    protected final void writeImpl() {
        writeC(0x60);
        writeD(_objectId);
        writeD(_targetId);
        writeD(_distance);
        writeD(_x);
        writeD(_y);
        writeD(_z);
    }
}