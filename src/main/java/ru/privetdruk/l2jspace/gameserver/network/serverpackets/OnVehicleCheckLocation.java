package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;

public class OnVehicleCheckLocation extends L2GameServerPacket {
    private final Creature _boat;

    public OnVehicleCheckLocation(Creature boat) {
        _boat = boat;
    }

    @Override
    protected void writeImpl() {
        writeC(0x5b);
        writeD(_boat.getId());
        writeD(_boat.getX());
        writeD(_boat.getY());
        writeD(_boat.getZ());
        writeD(_boat.getHeading());
    }
}