package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.data.manager.CastleManager;
import ru.privetdruk.l2jspace.gameserver.data.sql.ClanTable;
import ru.privetdruk.l2jspace.gameserver.enums.SiegeSide;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.entity.Castle;
import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SiegeDefenderList;

public final class RequestConfirmSiegeWaitingList extends L2GameClientPacket {
    private int _approved;
    private int _castleId;
    private int _clanId;

    @Override
    protected void readImpl() {
        _castleId = readD();
        _clanId = readD();
        _approved = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        // Check if the player has a clan
        if (player.getClan() == null)
            return;

        final Castle castle = CastleManager.getInstance().getCastleById(_castleId);
        if (castle == null)
            return;

        // Check if leader of the clan who owns the castle?
        if (castle.getOwnerId() != player.getClanId() || !player.isClanLeader())
            return;

        final Clan clan = ClanTable.getInstance().getClan(_clanId);
        if (clan == null)
            return;

        if (!castle.getSiege().isRegistrationOver()) {
            if (_approved == 1) {
                if (castle.getSiege().checkSide(clan, SiegeSide.PENDING))
                    castle.getSiege().registerClan(clan, SiegeSide.DEFENDER);
            } else {
                if (castle.getSiege().checkSides(clan, SiegeSide.PENDING, SiegeSide.DEFENDER))
                    castle.getSiege().unregisterClan(clan);
            }
        }

        // Update the defender list
        player.sendPacket(new SiegeDefenderList(castle));
    }
}