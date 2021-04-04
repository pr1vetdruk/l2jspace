package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.HennaUnequipList;

public final class RequestHennaUnequipList extends L2GameClientPacket {
    @SuppressWarnings("unused")
    private int _unknown;

    @Override
    protected void readImpl() {
        _unknown = readD(); // ??
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        player.sendPacket(new HennaUnequipList(player));
    }
}