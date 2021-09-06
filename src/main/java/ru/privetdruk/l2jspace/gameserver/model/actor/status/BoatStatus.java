package ru.privetdruk.l2jspace.gameserver.model.actor.status;

import ru.privetdruk.l2jspace.gameserver.model.actor.Boat;

public class BoatStatus extends CreatureStatus<Boat> {
    private int _moveSpeed;
    private int _rotationSpeed;

    public BoatStatus(Boat actor) {
        super(actor);
    }

    @Override
    public float getMoveSpeed() {
        return _moveSpeed;
    }

    public final void setMoveSpeed(int speed) {
        _moveSpeed = speed;
    }

    public final int getRotationSpeed() {
        return _rotationSpeed;
    }

    public final void setRotationSpeed(int speed) {
        _rotationSpeed = speed;
    }
}