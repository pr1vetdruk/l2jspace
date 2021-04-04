package ru.privetdruk.l2jspace.gameserver.handler.voicedcommandhandlers;

import ru.privetdruk.l2jspace.Config;
import ru.privetdruk.l2jspace.gameserver.handler.IVoicedCommandHandler;
import ru.privetdruk.l2jspace.gameserver.model.World;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

public class Online implements IVoicedCommandHandler {
    private static final String[] _voicedCommands =
            {
                    "online"
            };

    @Override
    public boolean useVoicedCommand(String command, Player player, String target) {
        if (command.equals("online") && Config.ENABLE_ONLINE_COMMAND)
            player.sendMessage("Сейчас в онлайне: " + World.getInstance().getPlayers().size() + " игроков.");

        return true;
    }

    @Override
    public String[] getVoicedCommandList() {
        return _voicedCommands;
    }
}