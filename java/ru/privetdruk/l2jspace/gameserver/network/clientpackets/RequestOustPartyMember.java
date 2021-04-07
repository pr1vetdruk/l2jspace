package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.enums.MessageType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.group.Party;

public final class RequestOustPartyMember extends L2GameClientPacket {
    private String _targetName;

    @Override
    protected void readImpl() {
        _targetName = readS();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        final Party party = player.getParty();
        if (party == null || !party.isLeader(player))
            return;

        party.removePartyMember(_targetName, MessageType.EXPELLED);
    }
}