package ru.privetdruk.l2jspace.gameserver.handler.usercommandhandlers;

import ru.privetdruk.l2jspace.gameserver.handler.IUserCommandHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.group.CommandChannel;
import ru.privetdruk.l2jspace.gameserver.model.group.Party;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;

public class ChannelLeave implements IUserCommandHandler {
    private static final int[] COMMAND_IDS =
            {
                    96
            };

    @Override
    public void useUserCommand(int id, Player player) {
        final Party party = player.getParty();
        if (party == null || !party.isLeader(player))
            return;

        final CommandChannel channel = party.getCommandChannel();
        if (channel == null)
            return;

        channel.removeParty(party);

        party.broadcastMessage(SystemMessageId.LEFT_COMMAND_CHANNEL);
        channel.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_PARTY_LEFT_COMMAND_CHANNEL).addCharName(player));
    }

    @Override
    public int[] getUserCommandList() {
        return COMMAND_IDS;
    }
}