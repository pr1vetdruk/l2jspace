package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

public final class RequestPrivateStoreManageSell extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        player.tryOpenPrivateSellStore(false);
    }
}