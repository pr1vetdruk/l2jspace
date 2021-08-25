package ru.privetdruk.l2jspace.gameserver.model.actor.move;

import ru.privetdruk.l2jspace.gameserver.enums.actors.MoveType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Boat;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.location.BoatLocation;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.VehicleDeparture;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.VehicleInfo;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.VehicleStarted;

public class BoatMove extends CreatureMove<Boat> {
    private BoatLocation[] _currentPath;
    private int _pathIndex;

    public BoatMove(Boat actor) {
        super(actor);

        // Boats simply don't bother about other movements.
        addMoveType(MoveType.FLY);
    }

    @Override
    public void stop() {
        if (_task == null)
            return;

        if (_task != null) {
            _task.cancel(false);
            _task = null;
        }

        _actor.broadcastPacket(new VehicleStarted(_actor, 0));
        _actor.broadcastPacket(new VehicleInfo(_actor));
    }

    @Override
    public boolean moveToNextRoutePoint() {
        return false;
    }

    @Override
    public boolean updatePosition(boolean firstRun) {
        final boolean result = super.updatePosition(firstRun);

        // Refresh all Players passengers positions.
        for (Player player : _actor.getPassengers()) {
            if (player.getBoat() == _actor) {
                player.setXYZ(_actor);
                player.revalidateZone(false);
            }
        }
        return result;
    }

    public void onArrival() {
        // Increment the path index.
        _pathIndex++;

        // We are still on path, move to the next BoatLocation.
        if (_pathIndex < _currentPath.length) {
            moveBoatTo(_currentPath[_pathIndex]);
            return;
        }

        // Stop the Boat.
        stop();

        // Renew Boat entrances when definitively stopped.
        _actor.renewBoatEntrances();

        // We are out of path, continue to process the engine.properties.
        _actor.runEngine(10);
    }

    /**
     * Move the {@link Boat} related to this {@link BoatMove} using a given {@link BoatLocation}.
     *
     * @param loc : The BoatLocation we send the Boat to.
     */
    private void moveBoatTo(BoatLocation loc) {
        // Feed Boat move speed and rotation based on BoatLocation parameter.
        if (loc.getMoveSpeed() > 0)
            _actor.getStatus().setMoveSpeed(loc.getMoveSpeed());

        if (loc.getRotationSpeed() > 0)
            _actor.getStatus().setRotationSpeed(loc.getRotationSpeed());

        // Set the destination.
        _destination.set(loc);

        // Set the heading.
        _actor.getPosition().setHeadingTo(loc);

        registerMoveTask();

        // Broadcast the movement (angle change, speed change, destination).
        _actor.broadcastPacket(new VehicleDeparture(_actor));
    }

    /**
     * Feed this {@link BoatMove} with a {@link BoatLocation} array, then trigger the {@link Boat} movement using the first BoatLocation of the array.
     *
     * @param path : The BoatLocation array used as path.
     */
    public void executePath(BoatLocation[] path) {
        // Current coordinates is zero on first call. Lets initialize it.
        _xAccurate = _actor.getX();
        _yAccurate = _actor.getY();

        // Initialize values.
        _pathIndex = 0;
        _currentPath = path;

        // Move the Boat to first encountered BoatLocation.
        moveBoatTo(_currentPath[0]);

        // Broadcast the starting movement.
        _actor.broadcastPacket(new VehicleStarted(_actor, 1));
    }
}