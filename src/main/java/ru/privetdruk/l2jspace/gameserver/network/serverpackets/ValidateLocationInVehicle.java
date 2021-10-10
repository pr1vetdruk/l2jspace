package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.location.SpawnLocation;

public class ValidateLocationInVehicle extends L2GameServerPacket {
    private final int _objectId;
    private final int _boatId;
    private final SpawnLocation _loc;

    public ValidateLocationInVehicle(Player player) {
        _objectId = player.getId();
        _boatId = player.getBoat().getId();
        _loc = player.getBoatPosition();
    }

    @Override
    protected final void writeImpl() {
        writeC(0x73);
        writeD(_objectId);
        writeD(_boatId);
        writeD(_loc.getX());
        writeD(_loc.getY());
        writeD(_loc.getZ());
        writeD(_loc.getHeading());
    }
}