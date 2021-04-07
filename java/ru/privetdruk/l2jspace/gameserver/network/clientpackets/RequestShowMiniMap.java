package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ShowMiniMap;

public final class RequestShowMiniMap extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected final void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        player.sendPacket(ShowMiniMap.REGULAR_MAP);
    }
}