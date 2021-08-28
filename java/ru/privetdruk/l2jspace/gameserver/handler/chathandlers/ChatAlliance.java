package ru.privetdruk.l2jspace.gameserver.handler.chathandlers;

import ru.privetdruk.l2jspace.gameserver.enums.SayType;
import ru.privetdruk.l2jspace.gameserver.handler.IChatHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.CreatureSay;

public class ChatAlliance implements IChatHandler {
    private static final SayType[] COMMAND_IDS =
            {
                    SayType.ALLIANCE
            };

    @Override
    public void handleChat(SayType type, Player player, String target, String text) {
        final Clan clan = player.getClan();
        if (clan == null || clan.getAllyId() == 0)
            return;

        clan.broadcastToAllyMembers(new CreatureSay(player, type, text));
    }

    @Override
    public SayType[] getChatTypeList() {
        return COMMAND_IDS;
    }
}