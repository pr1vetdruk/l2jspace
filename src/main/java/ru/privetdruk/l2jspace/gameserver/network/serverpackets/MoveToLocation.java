package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.location.Location;

public final class MoveToLocation extends L2GameServerPacket {
    private final int _objectId;
    private final Location _currentPosition;
    private final Location _destination;

    public MoveToLocation(Creature creature) {
        _objectId = creature.getId();
        _currentPosition = creature.getPosition().clone();
        _destination = creature.getMove().getDestination().clone();
    }

    public MoveToLocation(Creature creature, Location destination) {
        _objectId = creature.getId();
        _currentPosition = creature.getPosition().clone();
        _destination = destination;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x01);
        writeD(_objectId);
        writeLoc(_destination);
        writeLoc(_currentPosition);
    }
}