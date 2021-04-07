package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.handler.IUserCommandHandler;
import ru.privetdruk.l2jspace.gameserver.handler.UserCommandHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

public class RequestUserCommand extends L2GameClientPacket {
    private int _commandId;

    @Override
    protected void readImpl() {
        _commandId = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        final IUserCommandHandler handler = UserCommandHandler.getInstance().getHandler(_commandId);
        if (handler != null)
            handler.useUserCommand(_commandId, player);
    }
}