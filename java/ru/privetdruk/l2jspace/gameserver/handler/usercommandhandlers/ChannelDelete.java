package ru.privetdruk.l2jspace.gameserver.handler.usercommandhandlers;

import ru.privetdruk.l2jspace.gameserver.handler.IUserCommandHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.group.CommandChannel;
import ru.privetdruk.l2jspace.gameserver.model.group.Party;

public class ChannelDelete implements IUserCommandHandler {
    private static final int[] COMMAND_IDS =
            {
                    93
            };

    @Override
    public void useUserCommand(int id, Player player) {
        final Party party = player.getParty();
        if (party == null || !party.isLeader(player))
            return;

        final CommandChannel channel = party.getCommandChannel();
        if (channel == null || !channel.isLeader(player))
            return;

        channel.disband();
    }

    @Override
    public int[] getUserCommandList() {
        return COMMAND_IDS;
    }
}