package ru.privetdruk.l2jspace.gameserver.handler;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

public interface IVoicedCommandHandler {
    public boolean useVoicedCommand(String command, Player player, String params);

    public String[] getVoicedCommandList();
}
