package ru.privetdruk.l2jspace.gameserver.handler.voicedcommandhandlers;

import java.text.SimpleDateFormat;

import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.handler.IVoicedCommandHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.NpcHtmlMessage;

public class PremiumStatus implements IVoicedCommandHandler {
    private static final String[] VOICED_COMMANDS =
            {
                    "premium"
            };

    @Override
    public boolean useVoicedCommand(String command, Player player, String target) {
        if (command.startsWith(VOICED_COMMANDS[0])) {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            if (player.getPremiumService() == 0) {
                NpcHtmlMessage preReply = new NpcHtmlMessage(5);
                StringBuilder html = new StringBuilder("<html><body><title>Normal Account</title><center>");
                html.append("<table>");
                html.append("<tr><td><center>Your account :<font color=\"LEVEL\">Normal<br></font></td></tr>");
                html.append("<tr><td><center>Details<br1></td></tr>");
                html.append("<tr><td>Rate EXP: <font color=\"LEVEL\">" + Config.RATE_XP + "<br1></font></td></tr>");
                html.append("<tr><td>Rate SP: <font color=\"LEVEL\">" + Config.RATE_SP + "<br1></font></td></tr>");
                html.append("<tr><td>Rate Spoil: <font color=\"LEVEL\">" + Config.RATE_DROP_SPOIL + "<br1></font></td></tr><br>");
                html.append("<tr><td>Expires : <font color=\"00A5FF\"> Never (Normal Account)<br1></font></td></tr>");
                html.append("<tr><td>Current Date : <font color=\"70FFCA\"> :" + String.valueOf(format.format(System.currentTimeMillis())) + " <br><br></font></td></tr><br><br1><br1>");
                html.append("<tr><td><font color=\"LEVEL\"><center>Premium Info & Rules<br1></font></td></tr>");
                html.append("<tr><td>Upgrade to Premium Account :<br1></td></tr>");
                html.append("<tr><td>Premium Account : <font color=\"70FFCA\"> Benefits<br1></font></td></tr>");
                html.append("<tr><td>Rate EXP: <font color=\"LEVEL\"> " + Config.PREMIUM_RATE_XP + " (Account Premium )<br1></font></td></tr>");
                html.append("<tr><td>Rate SP: <font color=\"LEVEL\"> " + Config.PREMIUM_RATE_SP + " (Account Premium )<br1></font></td></tr>");
                html.append("<tr><td>Drop Spoil Rate: <font color=\"LEVEL\"> " + Config.PREMIUM_RATE_DROP_SPOIL + " (Account Premium )<br1></font></td></tr>");
                html.append("<tr><td> <font color=\"70FFCA\">1.Premium  benefits CAN NOT BE TRANSFERED.<br1></font></td></tr><br>");
                html.append("<tr><td> <font color=\"70FFCA\">2.Premium benefits effect ALL characters in same account.<br1></font></td></tr><br>");
                html.append("<tr><td> <font color=\"70FFCA\">3.Does not effect Party members.</font></td></tr>");
                html.append("</table>");
                html.append("</center></body></html>");

                preReply.setHtml(html.toString());
                player.sendPacket(preReply);
            } else {
                long endPremDate = 0L;
                endPremDate = player.getPremServiceData();
                NpcHtmlMessage preReply = new NpcHtmlMessage(5);

                StringBuilder html = new StringBuilder("<html><body><title>Premium Account Details</title><center>");
                html.append("<table>");
                html.append("<tr><td><center>Thank you for supporting server.<br></td></tr>");
                html.append("<tr><td><center>Your account : <font color=\"LEVEL\">Premium<br></font></td></tr>");
                html.append("<tr><td><center>Details<br1></center></td></tr>");
                html.append("<tr><td>Rate EXP: <font color=\"LEVEL\"> x" + Config.PREMIUM_RATE_XP + " <br1></font></td></tr>");
                html.append("<tr><td>Rate SP: <font color=\"LEVEL\"> x" + Config.PREMIUM_RATE_SP + "  <br1></font></td></tr>");
                html.append("<tr><td>Rate Spoil: <font color=\"LEVEL\"> x" + Config.PREMIUM_RATE_DROP_SPOIL + " <br1></font></td></tr>");
                html.append("<tr><td>Expires : <font color=\"00A5FF\"> " + format.format(endPremDate) + " (Premium added)</font></td></tr>");
                html.append("<tr><td>Current Date : <font color=\"70FFCA\"> :" + format.format(System.currentTimeMillis()) + " <br><br></font></td></tr>");
                html.append("<tr><td><font color=\"LEVEL\"><center>Premium Info & Rules<br1></font></center></td></tr>");
                html.append("<tr><td><font color=\"70FFCA\">1.Premium Account CAN NOT BE TRANSFERED.<br1></font></td></tr>");
                html.append("<tr><td><font color=\"70FFCA\">2.Premium Account effects ALL characters in same account.<br1></font></td></tr>");
                html.append("<tr><td><font color=\"70FFCA\">3.Does not effect Party members.<br><br></font></td></tr>");
                html.append("</table>");
                html.append("</center></body></html>");

                preReply.setHtml(html.toString());
                player.sendPacket(preReply);
            }
        }
        return true;
    }

    @Override
    public String[] getVoicedCommandList() {
        return VOICED_COMMANDS;
    }
}