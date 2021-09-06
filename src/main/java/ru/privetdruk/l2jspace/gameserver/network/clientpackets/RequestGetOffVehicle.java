package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.common.math.MathUtil;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.location.Location;
import ru.privetdruk.l2jspace.gameserver.model.location.Point2D;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ActionFailed;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.GetOffVehicle;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.MoveToLocation;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.StopMoveInVehicle;

public final class RequestGetOffVehicle extends L2GameClientPacket {
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

        if (!player.isInBoat() || player.getBoat().getObjectId() != _boatId) {
            sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        player.broadcastPacket(new StopMoveInVehicle(player, _boatId));
        player.setBoat(null);
        player.broadcastPacket(new GetOffVehicle(player.getObjectId(), _boatId, _x, _y, _z));

        // Proper heading has been set when we clicked outside of the ship, just move player forward.
        final Point2D outsidePoint = MathUtil.getNewLocationByDistanceAndHeading(_x, _y, player.getPosition().getHeading(), 60);

        player.setXYZ(outsidePoint.getX(), outsidePoint.getY(), _z);
        player.revalidateZone(true);

        player.broadcastPacket(new MoveToLocation(player, new Location(outsidePoint.getX(), outsidePoint.getY(), _z)));
    }
}