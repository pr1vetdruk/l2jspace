package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.data.manager.PartyMatchRoomManager;
import ru.privetdruk.l2jspace.gameserver.enums.MessageType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.group.Party;
import ru.privetdruk.l2jspace.gameserver.model.group.PartyMatchRoom;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ExPartyRoomMember;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.PartyMatchDetail;

public final class RequestWithdrawParty extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        final Party party = player.getParty();
        if (party == null)
            return;

        party.removePartyMember(player, MessageType.LEFT);

        if (player.isInPartyMatchRoom()) {
            final PartyMatchRoom room = PartyMatchRoomManager.getInstance().getRoom(player.getPartyRoom());
            if (room != null) {
                player.sendPacket(new PartyMatchDetail(room));
                player.sendPacket(new ExPartyRoomMember(room, 0));

                // Remove PartyMatchRoom member.
                room.removeMember(player);
            }
        }
    }
}