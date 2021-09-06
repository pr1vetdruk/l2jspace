package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

public final class RequestChangeWaitType extends L2GameClientPacket {
    private boolean _typeStand;

    @Override
    protected void readImpl() {
        _typeStand = (readD() == 1);
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        final WorldObject target = player.getTarget();

        if (_typeStand)
            player.getAI().tryToStand();
        else
            player.getAI().tryToSit(target);
    }
}