package ru.privetdruk.l2jspace.gameserver.handler.chathandlers;

import ru.privetdruk.l2jspace.gameserver.enums.SayType;
import ru.privetdruk.l2jspace.gameserver.handler.IChatHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.group.Party;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.CreatureSay;

public class ChatParty implements IChatHandler {
    private static final SayType[] COMMAND_IDS =
            {
                    SayType.PARTY
            };

    @Override
    public void handleChat(SayType type, Player player, String target, String text) {
        final Party party = player.getParty();
        if (party == null)
            return;

        party.broadcastPacket(new CreatureSay(player, type, text));
    }

    @Override
    public SayType[] getChatTypeList() {
        return COMMAND_IDS;
    }
}