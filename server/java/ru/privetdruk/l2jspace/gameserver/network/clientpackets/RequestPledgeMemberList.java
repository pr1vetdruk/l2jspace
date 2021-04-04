package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;
import ru.privetdruk.l2jspace.gameserver.model.pledge.SubPledge;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.PledgeShowMemberListAll;

public final class RequestPledgeMemberList extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        final Clan clan = player.getClan();
        if (clan == null)
            return;

        player.sendPacket(new PledgeShowMemberListAll(clan, 0));

        for (SubPledge sp : clan.getAllSubPledges())
            player.sendPacket(new PledgeShowMemberListAll(clan, sp.getId()));
    }
}