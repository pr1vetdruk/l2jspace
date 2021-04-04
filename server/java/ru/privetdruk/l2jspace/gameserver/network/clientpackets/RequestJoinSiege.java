package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.data.manager.CastleManager;
import ru.privetdruk.l2jspace.gameserver.data.manager.ClanHallManager;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.clanhall.SiegableHall;
import ru.privetdruk.l2jspace.gameserver.model.entity.Castle;
import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SiegeInfo;

public final class RequestJoinSiege extends L2GameClientPacket {
    private int _id;
    private int _isAttacker;
    private int _isJoining;

    @Override
    protected void readImpl() {
        _id = readD();
        _isAttacker = readD();
        _isJoining = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        if (!player.hasClanPrivileges(Clan.CP_CS_MANAGE_SIEGE)) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }

        final Clan clan = player.getClan();
        if (clan == null)
            return;

        // Check Castle entity associated to the id.
        final Castle castle = CastleManager.getInstance().getCastleById(_id);
        if (castle != null) {
            if (_isJoining == 1) {
                if (System.currentTimeMillis() < clan.getDissolvingExpiryTime()) {
                    player.sendPacket(SystemMessageId.CANT_PARTICIPATE_IN_SIEGE_WHILE_DISSOLUTION_IN_PROGRESS);
                    return;
                }

                if (_isAttacker == 1)
                    castle.getSiege().registerAttacker(player);
                else
                    castle.getSiege().registerDefender(player);
            } else
                castle.getSiege().unregisterClan(clan);

            player.sendPacket(new SiegeInfo(castle));
            return;
        }

        // Check SiegableHall entity associated to the id.
        final SiegableHall sh = ClanHallManager.getInstance().getSiegableHall(_id);
        if (sh != null) {
            if (_isJoining == 1) {
                if (System.currentTimeMillis() < clan.getDissolvingExpiryTime()) {
                    player.sendPacket(SystemMessageId.CANT_PARTICIPATE_IN_SIEGE_WHILE_DISSOLUTION_IN_PROGRESS);
                    return;
                }

                sh.registerClan(clan, player);
            } else
                sh.unregisterClan(clan);

            player.sendPacket(new SiegeInfo(sh));
        }
    }
}