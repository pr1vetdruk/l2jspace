package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.PledgeShowMemberListDelete;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;

public final class RequestWithdrawPledge extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        final Clan clan = player.getClan();
        if (clan == null) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER);
            return;
        }

        if (player.isClanLeader()) {
            player.sendPacket(SystemMessageId.CLAN_LEADER_CANNOT_WITHDRAW);
            return;
        }

        if (player.isInCombat()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_LEAVE_DURING_COMBAT);
            return;
        }

        clan.removeClanMember(player.getId(), System.currentTimeMillis() + Config.CLAN_JOIN_DAYS * 86400000L);
        clan.broadcastToMembers(SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_WITHDRAWN_FROM_THE_CLAN).addCharName(player));

        // Remove the player from the members list.
        if (clan.isSubPledgeLeader(player.getId()))
            clan.broadcastClanStatus(); // refresh list
        else
            clan.broadcastToMembers(new PledgeShowMemberListDelete(player.getName()));

        player.sendPacket(SystemMessageId.YOU_HAVE_WITHDRAWN_FROM_CLAN);
        player.sendPacket(SystemMessageId.YOU_MUST_WAIT_BEFORE_JOINING_ANOTHER_CLAN);
    }
}