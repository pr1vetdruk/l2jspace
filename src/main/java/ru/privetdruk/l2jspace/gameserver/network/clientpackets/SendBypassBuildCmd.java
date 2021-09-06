package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.data.xml.AdminData;
import ru.privetdruk.l2jspace.gameserver.handler.AdminCommandHandler;
import ru.privetdruk.l2jspace.gameserver.handler.IAdminCommandHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

import java.util.logging.Logger;

public final class SendBypassBuildCmd extends L2GameClientPacket {
    private static final Logger GMAUDIT_LOG = Logger.getLogger("gmaudit");

    private String _command;

    @Override
    protected void readImpl() {
        _command = readS();
        if (_command != null)
            _command = _command.trim();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        String command = "admin_" + _command.split(" ")[0];

        final IAdminCommandHandler ach = AdminCommandHandler.getInstance().getHandler(command);
        if (ach == null) {
            if (player.isGM())
                player.sendMessage("The command " + command.substring(6) + " doesn't exist.");

            LOGGER.warn("No handler registered for admin command '{}'.", command);
            return;
        }

        if (!AdminData.getInstance().hasAccess(command, player.getAccessLevel())) {
            player.sendMessage("You don't have the access right to use this command.");
            LOGGER.warn("{} tried to use admin command '{}', but has no access to use it.", player.getName(), command);
            return;
        }

        if (Config.GM_AUDIT)
            GMAUDIT_LOG.info(player.getName() + " [" + player.getObjectId() + "] used '" + _command + "' command on: " + ((player.getTarget() != null) ? player.getTarget().getName() : "none"));

        ach.useAdminCommand("admin_" + _command, player);
    }
}