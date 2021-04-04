package ru.privetdruk.l2jspace.gameserver.handler.chathandlers;

import ru.privetdruk.l2jspace.gameserver.enums.SayType;
import ru.privetdruk.l2jspace.gameserver.handler.IChatHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.CreatureSay;

public class ChatClan implements IChatHandler {
    private static final SayType[] COMMAND_IDS =
            {
                    SayType.CLAN
            };

    @Override
    public void handleChat(SayType type, Player player, String target, String text) {
        final Clan clan = player.getClan();
        if (clan == null)
            return;

        clan.broadcastToOnlineMembers(new CreatureSay(player, type, text));
    }

    @Override
    public SayType[] getChatTypeList() {
        return COMMAND_IDS;
    }
}