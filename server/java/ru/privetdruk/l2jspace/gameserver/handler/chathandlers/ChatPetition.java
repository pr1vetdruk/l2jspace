package ru.privetdruk.l2jspace.gameserver.handler.chathandlers;

import ru.privetdruk.l2jspace.gameserver.data.manager.PetitionManager;
import ru.privetdruk.l2jspace.gameserver.enums.SayType;
import ru.privetdruk.l2jspace.gameserver.handler.IChatHandler;
import ru.privetdruk.l2jspace.gameserver.model.Petition;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;

public class ChatPetition implements IChatHandler {
    private static final SayType[] COMMAND_IDS =
            {
                    SayType.PETITION_PLAYER,
                    SayType.PETITION_GM
            };

    @Override
    public void handleChat(SayType type, Player player, String target, String text) {
        final Petition petition = PetitionManager.getInstance().getPetitionInProcess(player);
        if (petition == null) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_IN_PETITION_CHAT);
            return;
        }

        petition.sendMessage(player, text);
    }

    @Override
    public SayType[] getChatTypeList() {
        return COMMAND_IDS;
    }
}