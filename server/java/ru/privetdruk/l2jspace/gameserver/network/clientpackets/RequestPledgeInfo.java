package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.data.sql.ClanTable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.PledgeInfo;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.PledgeStatusChanged;

public final class RequestPledgeInfo extends L2GameClientPacket {
    private int _clanId;

    @Override
    protected void readImpl() {
        _clanId = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        final Clan clan = ClanTable.getInstance().getClan(_clanId);
        if (clan == null)
            return;

        player.sendPacket(new PledgeInfo(clan));
        player.sendPacket(new PledgeStatusChanged(clan));
    }

    @Override
    protected boolean triggersOnActionRequest() {
        return false;
    }
}