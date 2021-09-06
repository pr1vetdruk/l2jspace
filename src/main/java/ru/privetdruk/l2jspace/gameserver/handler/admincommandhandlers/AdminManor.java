package ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers;

import ru.privetdruk.l2jspace.common.lang.StringUtil;
import ru.privetdruk.l2jspace.gameserver.data.manager.CastleManager;
import ru.privetdruk.l2jspace.gameserver.data.manager.CastleManorManager;
import ru.privetdruk.l2jspace.gameserver.handler.IAdminCommandHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.entity.Castle;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.NpcHtmlMessage;

public class AdminManor implements IAdminCommandHandler {
    private static final String[] ADMIN_COMMANDS =
            {
                    "admin_manor"
            };

    @Override
    public void useAdminCommand(String command, Player player) {
        if (command.startsWith("admin_manor")) {
            final NpcHtmlMessage html = new NpcHtmlMessage(0);
            html.setFile("data/html/admin/manor.htm");
            html.replace("%status%", CastleManorManager.getInstance().getCurrentModeName());
            html.replace("%change%", CastleManorManager.getInstance().getNextModeChange());

            final StringBuilder sb = new StringBuilder(3400);
            for (Castle castle : CastleManager.getInstance().getCastles()) {
                StringUtil.append(sb, "<tr><td width=110>Name:</td><td width=160><font color=008000>" + castle.getName() + "</font></td></tr>");
                StringUtil.append(sb, "<tr><td>Current period cost:</td><td><font color=FF9900>", StringUtil.formatNumber(CastleManorManager.getInstance().getManorCost(castle.getCastleId(), false)), " Adena</font></td></tr>");
                StringUtil.append(sb, "<tr><td>Next period cost:</td><td><font color=FF9900>", StringUtil.formatNumber(CastleManorManager.getInstance().getManorCost(castle.getCastleId(), true)), " Adena</font></td></tr>");
                StringUtil.append(sb, "<tr><td>&nbsp;</td></tr>");
            }
            html.replace("%castleInfo%", sb.toString());

            player.sendPacket(html);
        }
    }

    @Override
    public String[] getAdminCommandList() {
        return ADMIN_COMMANDS;
    }
}