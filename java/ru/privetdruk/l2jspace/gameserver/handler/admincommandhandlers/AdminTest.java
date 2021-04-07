package ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;

import ru.privetdruk.l2jspace.gameserver.handler.IAdminCommandHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

public class AdminTest implements IAdminCommandHandler {
    private static final String[] ADMIN_COMMANDS =
            {
                    "admin_test",
            };

    @Override
    public void useAdminCommand(String command, Player player) {
        final StringTokenizer st = new StringTokenizer(command);
        st.nextToken();

        if (!st.hasMoreTokens()) {
            player.sendMessage("Usage : //test ...");
            return;
        }

        switch (st.nextToken()) {
            // Add your own cases.

            default:
                player.sendMessage("Usage : //test ...");
                break;
        }
    }

    @Override
    public String[] getAdminCommandList() {
        return ADMIN_COMMANDS;
    }
}