package ru.privetdruk.l2jspace.gameserver.handler.chathandlers;

import ru.privetdruk.l2jspace.gameserver.data.manager.PartyMatchRoomManager;
import ru.privetdruk.l2jspace.gameserver.enums.SayType;
import ru.privetdruk.l2jspace.gameserver.handler.IChatHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.group.PartyMatchRoom;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.CreatureSay;

public class ChatPartyMatchRoom implements IChatHandler {
    private static final SayType[] COMMAND_IDS =
            {
                    SayType.PARTYMATCH_ROOM
            };

    @Override
    public void handleChat(SayType type, Player player, String target, String text) {
        if (!player.isInPartyMatchRoom())
            return;

        final PartyMatchRoom room = PartyMatchRoomManager.getInstance().getRoom(player.getPartyRoom());
        if (room == null)
            return;

        room.broadcastPacket(new CreatureSay(player, type, text));
    }

    @Override
    public SayType[] getChatTypeList() {
        return COMMAND_IDS;
    }
}