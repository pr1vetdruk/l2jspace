package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.data.manager.BoatManager;
import ru.privetdruk.l2jspace.gameserver.model.actor.Boat;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.location.BoatEntrance;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ActionFailed;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.MoveToLocationInVehicle;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.StopMoveInVehicle;

public final class RequestMoveToLocationInVehicle extends L2GameClientPacket {
    private int _boatId;
    private int _tX;
    private int _tY;
    private int _tZ;
    private int _oX;
    private int _oY;
    private int _oZ;

    @Override
    protected void readImpl() {
        _boatId = readD();
        _tX = readD();
        _tY = readD();
        _tZ = readD();
        _oX = readD();
        _oY = readD();
        _oZ = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        if (_tX == _oX && _tY == _oY && _tZ == _oZ) {
            player.sendPacket(new StopMoveInVehicle(player, _boatId));
            return;
        }

        final Boat boat;
        if (player.isInBoat()) {
            boat = player.getBoat();
            if (boat.getObjectId() != _boatId) {
                player.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }

            // In case the player is already onboard, we don't want to do any server side movement, except updating position inside the boat.
            player.getBoatPosition().set(_tX, _tY, _tZ);

            player.broadcastPacket(new MoveToLocationInVehicle(player, boat, _tX, _tY, _tZ, _oX, _oY, _oZ));
        } else {
            boat = BoatManager.getInstance().getBoat(_boatId);
            if (boat == null) {
                player.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }

            final BoatEntrance closestEntrance = boat.getClosestEntrance(player.getPosition());
            if (closestEntrance == null) {
                player.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }

            player.getAI().tryToMoveTo(closestEntrance.getOuterLocation(), boat);
        }
    }
}