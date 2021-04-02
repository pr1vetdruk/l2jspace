package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class Menu implements IVoicedCommandHandler {
    private static final String ACTIVED = "<font color=00FF00>ON</font>";
    private static final String DESAСTIVED = "<font color=FF0000>OFF</font>";

    private static final String[] VOICED_COMMANDS =
            {
                    "cfg",
                    "menu",
                    "mod_menu_"
            };

    @Override
    public boolean useVoicedCommand(String command, Player player, String target) {
        if (Config.ENABLE_MENU) {
            if (command.equalsIgnoreCase("menu") || command.equalsIgnoreCase("cfg"))
                showHtm(player);
            else if (command.startsWith("mod_menu_")) {
                String addcmd = command.substring(9).trim();
                if (addcmd.startsWith("exp")) {
                    int flag = Integer.parseInt(addcmd.substring(3).trim());
                    if (flag == 0) {
                        player.setStopExp(true);
                        player.sendMessage("Вы не можете получить опыт, убивая мобов.");
                    } else {
                        player.setStopExp(false);
                        player.sendMessage("Вы можете получить опыт, убивая мобов.");
                    }

                    showHtm(player);
                    return true;
                } else if (addcmd.startsWith("trade")) {
                    int flag = Integer.parseInt(addcmd.substring(5).trim());
                    if (flag == 0) {
                        player.setTradeRefusal(true);
                        player.sendMessage("Возможность использовать трейд включена");
                    } else {
                        player.setTradeRefusal(false);
                        player.sendMessage("Возможность использовать трейд отключена");
                    }

                    showHtm(player);
                    return true;
                }
            }
        } else
            player.sendMessage("Сервис отключен");

        return true;
    }

    private static void showHtm(Player player) {
        NpcHtmlMessage htm = new NpcHtmlMessage(0);
        htm.setFile("./data/html/mods/menu/menu.htm");

        htm.replace("%gainexp%", player.isStopExp() ? ACTIVED : DESAСTIVED);
        htm.replace("%trade%", player.isTradeRefusal() ? ACTIVED : DESAСTIVED);

        player.sendPacket(htm);
    }

    @Override
    public String[] getVoicedCommandList() {
        return VOICED_COMMANDS;
    }
}