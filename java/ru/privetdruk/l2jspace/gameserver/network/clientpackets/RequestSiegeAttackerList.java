package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.data.manager.CastleManager;
import ru.privetdruk.l2jspace.gameserver.data.manager.ClanHallManager;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.clanhall.SiegableHall;
import ru.privetdruk.l2jspace.gameserver.model.entity.Castle;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SiegeAttackerList;

public final class RequestSiegeAttackerList extends L2GameClientPacket {
    private int _id;

    @Override
    protected void readImpl() {
        _id = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        // Check Castle entity associated to the id.
        final Castle castle = CastleManager.getInstance().getCastleById(_id);
        if (castle != null) {
            sendPacket(new SiegeAttackerList(castle));
            return;
        }

        // Check SiegableHall entity associated to the id.
        final SiegableHall sh = ClanHallManager.getInstance().getSiegableHall(_id);
        if (sh != null)
            sendPacket(new SiegeAttackerList(sh));
    }
}