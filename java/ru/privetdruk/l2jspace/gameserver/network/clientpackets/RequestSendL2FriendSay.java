package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import ru.privetdruk.l2jspace.Config;
import ru.privetdruk.l2jspace.gameserver.model.World;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.L2FriendSay;

public final class RequestSendL2FriendSay extends L2GameClientPacket {
    private static final Logger CHAT_LOG = Logger.getLogger("chat");

    private String _message;
    private String _recipient;

    @Override
    protected void readImpl() {
        _message = readS();
        _recipient = readS();
    }

    @Override
    protected void runImpl() {
        if (_message == null || _message.isEmpty() || _message.length() > 300)
            return;

        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        final Player recipient = World.getInstance().getPlayer(_recipient);
        if (recipient == null || !recipient.getFriendList().contains(player.getObjectId())) {
            player.sendPacket(SystemMessageId.TARGET_IS_NOT_FOUND_IN_THE_GAME);
            return;
        }

        // If sender is in block list of recipient then notify him about this and ignore message.
        if (recipient.getBlockList().isInBlockList(player)) {
            player.sendPacket(new L2FriendSay(_recipient, player.getName(), _message, 620));
            return;
        }

        if (Config.LOG_CHAT) {
            LogRecord record = new LogRecord(Level.INFO, _message);
            record.setLoggerName("chat");
            record.setParameters(new Object[]
                    {
                            "PRIV_MSG",
                            "[" + player.getName() + " to " + _recipient + "]"
                    });

            CHAT_LOG.log(record);
        }

        recipient.sendPacket(new L2FriendSay(player.getName(), _recipient, _message, 0));
    }
}