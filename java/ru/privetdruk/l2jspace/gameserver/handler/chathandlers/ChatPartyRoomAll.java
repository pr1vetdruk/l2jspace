package ru.privetdruk.l2jspace.gameserver.handler.chathandlers;

import ru.privetdruk.l2jspace.gameserver.enums.SayType;
import ru.privetdruk.l2jspace.gameserver.handler.IChatHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.group.CommandChannel;
import ru.privetdruk.l2jspace.gameserver.model.group.Party;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.CreatureSay;

public class ChatPartyRoomAll implements IChatHandler {
    private static final SayType[] COMMAND_IDS =
            {
                    SayType.PARTYROOM_ALL
            };

    @Override
    public void handleChat(SayType type, Player player, String target, String text) {
        final Party party = player.getParty();
        if (party == null || !party.isLeader(player))
            return;

        final CommandChannel channel = party.getCommandChannel();
        if (channel == null)
            return;

        channel.broadcastCreatureSay(new CreatureSay(player, type, text), player);
    }

    @Override
    public SayType[] getChatTypeList() {
        return COMMAND_IDS;
    }
}