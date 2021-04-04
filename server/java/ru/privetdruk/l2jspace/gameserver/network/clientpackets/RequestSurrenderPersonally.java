package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.data.sql.ClanTable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;

public final class RequestSurrenderPersonally extends L2GameClientPacket {
    private String _pledgeName;

    @Override
    protected void readImpl() {
        _pledgeName = readS();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        final Clan playerClan = player.getClan();
        if (playerClan == null)
            return;

        final Clan clan = ClanTable.getInstance().getClanByName(_pledgeName);
        if (clan == null)
            return;

        if (!playerClan.isAtWarWith(clan.getClanId()) || player.wantsPeace()) {
            player.sendPacket(SystemMessageId.FAILED_TO_PERSONALLY_SURRENDER);
            return;
        }

        player.setWantsPeace(true);
        player.applyDeathPenalty(false, false);
        player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_PERSONALLY_SURRENDERED_TO_THE_S1_CLAN).addString(_pledgeName));
        ClanTable.getInstance().checkSurrender(playerClan, clan);
    }
}