package ru.privetdruk.l2jspace.gameserver.handler.chathandlers;

import ru.privetdruk.l2jspace.gameserver.enums.FloodProtector;
import ru.privetdruk.l2jspace.gameserver.enums.SayType;
import ru.privetdruk.l2jspace.gameserver.handler.IChatHandler;
import ru.privetdruk.l2jspace.gameserver.model.World;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.CreatureSay;

public class ChatHeroVoice implements IChatHandler {
    private static final SayType[] COMMAND_IDS =
            {
                    SayType.HERO_VOICE
            };

    @Override
    public void handleChat(SayType type, Player player, String target, String text) {
        if (!player.isHero())
            return;

        if (!player.getClient().performAction(FloodProtector.HERO_VOICE))
            return;

        World.toAllOnlinePlayers(new CreatureSay(player, type, text));
    }

    @Override
    public SayType[] getChatTypeList() {
        return COMMAND_IDS;
    }
}