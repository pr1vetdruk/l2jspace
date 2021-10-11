package ru.privetdruk.l2jspace.gameserver.handler.chathandlers;

import ru.privetdruk.l2jspace.gameserver.enums.FloodProtector;
import ru.privetdruk.l2jspace.gameserver.enums.SayType;
import ru.privetdruk.l2jspace.gameserver.handler.IChatHandler;
import ru.privetdruk.l2jspace.gameserver.handler.IVoicedCommandHandler;
import ru.privetdruk.l2jspace.gameserver.handler.VoicedCommandHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.CreatureSay;

import java.util.StringTokenizer;

public class ChatAll implements IChatHandler {
    private static final SayType[] COMMAND_IDS =
            {
                    SayType.ALL
            };

    @Override
    public void handleChat(SayType type, Player player, String target, String text) {
        if (!player.getClient().performAction(FloodProtector.GLOBAL_CHAT))
            return;

        boolean useHandler = false;
        if (text.startsWith(".")) {
            final StringTokenizer st = new StringTokenizer(text);
            final IVoicedCommandHandler vch;
            String command = "";

            if (st.countTokens() > 1) {
                command = st.nextToken().substring(1);
                target = text.substring(command.length() + 2);
                vch = VoicedCommandHandler.getInstance().getHandler(command);
            } else {
                command = text.substring(1);
                vch = VoicedCommandHandler.getInstance().getHandler(command);
            }

            if (vch != null) {
                vch.useVoicedCommand(command, player, target);
                useHandler = true;
            }
        }

        if (!useHandler) {
            CreatureSay cs = new CreatureSay(player.getId(), type, player.getChatName(), text);

            for (Player knownPlayer : player.getKnownTypeInRadius(Player.class, 1250)) {
                if (!knownPlayer.getBlockList().isBlockingAll())
                    knownPlayer.sendPacket(cs);
            }

            player.sendPacket(cs);
        }
    }

    @Override
    public SayType[] getChatTypeList() {
        return COMMAND_IDS;
    }
}