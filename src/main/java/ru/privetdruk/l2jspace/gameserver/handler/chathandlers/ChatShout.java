package ru.privetdruk.l2jspace.gameserver.handler.chathandlers;

import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.data.xml.MapRegionData;
import ru.privetdruk.l2jspace.gameserver.enums.FloodProtector;
import ru.privetdruk.l2jspace.gameserver.enums.SayType;
import ru.privetdruk.l2jspace.gameserver.handler.IChatHandler;
import ru.privetdruk.l2jspace.gameserver.model.World;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.CreatureSay;

public class ChatShout implements IChatHandler {
    private static final SayType[] COMMAND_IDS =
            {
                    SayType.SHOUT
            };

    @Override
    public void handleChat(SayType type, Player player, String target, String text) {
        if (!player.getClient().performAction(FloodProtector.GLOBAL_CHAT))
            return;

        final CreatureSay cs = new CreatureSay(player, type, text);
        if (Config.GLOBAL_CHAT.equalsIgnoreCase("global") || (Config.GLOBAL_CHAT.equalsIgnoreCase("gm") && player.isGM())) {
            for (Player players : World.getInstance().getPlayers()) {
                if (!players.getBlockList().isBlockingAll())
                    players.sendPacket(cs);
            }

        } else if (Config.GLOBAL_CHAT.equalsIgnoreCase("on")) {
            final int region = MapRegionData.getInstance().getMapRegion(player.getX(), player.getY());
            {
                for (Player worldPlayer : World.getInstance().getPlayers()) {
                    if (region == MapRegionData.getInstance().getMapRegion(worldPlayer.getX(), worldPlayer.getY()))
                        worldPlayer.sendPacket(cs);
                }
            }
        }
    }

    @Override
    public SayType[] getChatTypeList() {
        return COMMAND_IDS;
    }
}