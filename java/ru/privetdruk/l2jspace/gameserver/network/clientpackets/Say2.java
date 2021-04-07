package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import ru.privetdruk.l2jspace.Config;
import ru.privetdruk.l2jspace.gameserver.enums.SayType;
import ru.privetdruk.l2jspace.gameserver.handler.ChatHandler;
import ru.privetdruk.l2jspace.gameserver.handler.IChatHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ActionFailed;

public final class Say2 extends L2GameClientPacket {
    private static final Logger CHAT_LOG = Logger.getLogger("chat");

    private static final String[] WALKER_COMMAND_LIST =
            {
                    "USESKILL",
                    "USEITEM",
                    "BUYITEM",
                    "SELLITEM",
                    "SAVEITEM",
                    "LOADITEM",
                    "MSG",
                    "DELAY",
                    "LABEL",
                    "JMP",
                    "CALL",
                    "RETURN",
                    "MOVETO",
                    "NPCSEL",
                    "NPCDLG",
                    "DLGSEL",
                    "CHARSTATUS",
                    "POSOUTRANGE",
                    "POSINRANGE",
                    "GOHOME",
                    "SAY",
                    "EXIT",
                    "PAUSE",
                    "STRINDLG",
                    "STRNOTINDLG",
                    "CHANGEWAITTYPE",
                    "FORCEATTACK",
                    "ISMEMBER",
                    "REQUESTJOINPARTY",
                    "REQUESTOUTPARTY",
                    "QUITPARTY",
                    "MEMBERSTATUS",
                    "CHARBUFFS",
                    "ITEMCOUNT",
                    "FOLLOWTELEPORT"
            };

    private String _text;
    private int _id;
    private String _target;

    @Override
    protected void readImpl() {
        _text = readS();
        _id = readD();
        _target = (_id == SayType.TELL.ordinal()) ? readS() : null;
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        if (_id < 0 || _id >= SayType.VALUES.length)
            return;

        if (_text.isEmpty() || _text.length() > 100)
            return;

        SayType type = SayType.VALUES[_id];
        if (Config.L2WALKER_PROTECTION && type == SayType.TELL && checkBot(_text))
            return;

        if (!player.isGM() && (type == SayType.ANNOUNCEMENT || type == SayType.CRITICAL_ANNOUNCE))
            return;

        if (player.isChatBanned() || (player.isInJail() && !player.isGM())) {
            player.sendPacket(SystemMessageId.CHATTING_PROHIBITED);
            return;
        }

        if (player.getStatus().getLevel() < Config.CHAT_ALL_LEVEL && (type == SayType.ALL)) {
            player.sendMessage("Общий чат доступен с " + Config.CHAT_ALL_LEVEL + " уровня.");
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (player.getStatus().getLevel() < Config.CHAT_TELL_LEVEL && (type == SayType.TELL)) {
            player.sendMessage("Приватный чат доступен с " + Config.CHAT_TELL_LEVEL + " уровня.");
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (player.getStatus().getLevel() < Config.CHAT_SHOUT_LEVEL && (type == SayType.SHOUT)) {
            player.sendMessage("Шаут чат доступен с " + Config.CHAT_SHOUT_LEVEL + " уровня.");
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (player.getStatus().getLevel() < Config.CHAT_TRADE_LEVEL && (type == SayType.TRADE)) {
            player.sendMessage("Торговый чат доступен с " + Config.CHAT_TRADE_LEVEL + " уровня.");
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (type == SayType.PETITION_PLAYER && player.isGM())
            type = SayType.PETITION_GM;

        if (Config.LOG_CHAT) {
            LogRecord record = new LogRecord(Level.INFO, _text);
            record.setLoggerName("chat");

            if (type == SayType.TELL)
                record.setParameters(new Object[]
                        {
                                type,
                                "[" + player.getName() + " to " + _target + "]"
                        });
            else
                record.setParameters(new Object[]
                        {
                                type,
                                "[" + player.getName() + "]"
                        });

            CHAT_LOG.log(record);
        }

        _text = _text.replaceAll("\\\\n", "");

        final IChatHandler handler = ChatHandler.getInstance().getHandler(type);
        if (handler == null) {
            LOGGER.warn("{} tried to use unregistred chathandler type: {}.", player.getName(), type);
            return;
        }

        handler.handleChat(type, player, _target, _text);
    }

    private static boolean checkBot(String text) {
        for (String botCommand : WALKER_COMMAND_LIST) {
            if (text.startsWith(botCommand))
                return true;
        }
        return false;
    }

    @Override
    protected boolean triggersOnActionRequest() {
        return false;
    }
}