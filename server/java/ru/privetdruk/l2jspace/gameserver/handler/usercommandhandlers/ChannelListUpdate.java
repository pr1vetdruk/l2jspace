package ru.privetdruk.l2jspace.gameserver.handler.usercommandhandlers;

import ru.privetdruk.l2jspace.gameserver.handler.IUserCommandHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.group.CommandChannel;
import ru.privetdruk.l2jspace.gameserver.model.group.Party;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ExMultiPartyCommandChannelInfo;

public class ChannelListUpdate implements IUserCommandHandler {
    private static final int[] COMMAND_IDS =
            {
                    97
            };

    @Override
    public void useUserCommand(int id, Player player) {
        final Party party = player.getParty();
        if (party == null)
            return;

        final CommandChannel channel = party.getCommandChannel();
        if (channel == null)
            return;

        player.sendPacket(new ExMultiPartyCommandChannelInfo(channel));
    }

    @Override
    public int[] getUserCommandList() {
        return COMMAND_IDS;
    }
}