package ru.privetdruk.l2jspace.gameserver.handler.usercommandhandlers;

import ru.privetdruk.l2jspace.gameserver.handler.IUserCommandHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

public class Mount implements IUserCommandHandler {
    private static final int[] COMMAND_IDS =
            {
                    61
            };

    @Override
    public void useUserCommand(int id, Player player) {
        player.mountPlayer(player.getSummon());
    }

    @Override
    public int[] getUserCommandList() {
        return COMMAND_IDS;
    }
}