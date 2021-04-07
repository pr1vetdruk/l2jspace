package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.data.manager.BoatManager;
import ru.privetdruk.l2jspace.gameserver.model.actor.Boat;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ActionFailed;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.GetOnVehicle;

public final class RequestGetOnVehicle extends L2GameClientPacket {
    private int _boatId;
    private int _x;
    private int _y;
    private int _z;

    @Override
    protected void readImpl() {
        _boatId = readD();
        _x = readD();
        _y = readD();
        _z = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        Boat boat;
        if (player.isInBoat()) {
            boat = player.getBoat();
            if (boat.getObjectId() != _boatId) {
                sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
        } else {
            boat = BoatManager.getInstance().getBoat(_boatId);
            if (boat == null) {
                sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }

            // It is only here as first shot warning. Player can actually onboard freely.
            if (player.getSummon() != null)
                player.sendPacket(SystemMessageId.RELEASE_PET_ON_BOAT);

            // Assigning boat and its coordinates to the player.
            player.setBoat(boat);
            player.setXYZ(boat.getX(), boat.getY(), boat.getZ());
            player.revalidateZone(true);

            // In case player jumped into departing boat.
            if (boat.isMoving())
                boat.addPassenger(player);
        }

        player.broadcastPacket(new GetOnVehicle(player.getObjectId(), boat.getObjectId(), _x, _y, _z));
    }
}