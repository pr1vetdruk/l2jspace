package ru.privetdruk.l2jspace.gameserver.model.actor.ai.type;

import ru.privetdruk.l2jspace.gameserver.model.actor.Boat;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.VehicleDeparture;

public class BoatAI extends CreatureAI {
    public BoatAI(Boat boat) {
        super(boat);
    }

    @Override
    public void describeStateToPlayer(Player player) {
        if (getActor().isMoving())
            player.sendPacket(new VehicleDeparture(getActor()));
    }

    @Override
    public Boat getActor() {
        return (Boat) _actor;
    }

    @Override
    public void onEvtAttacked(Creature attacker) {
    }

    @Override
    protected void onEvtArrived() {
        getActor().getMove().onArrival();
    }

    @Override
    protected void onEvtDead() {
    }

    @Override
    protected void onEvtFinishedCasting() {
    }
}