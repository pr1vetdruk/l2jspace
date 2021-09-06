package ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers;

import ru.privetdruk.l2jspace.common.lang.StringUtil;
import ru.privetdruk.l2jspace.gameserver.data.manager.ZoneManager;
import ru.privetdruk.l2jspace.gameserver.data.xml.MapRegionData;
import ru.privetdruk.l2jspace.gameserver.enums.ZoneId;
import ru.privetdruk.l2jspace.gameserver.handler.IAdminCommandHandler;
import ru.privetdruk.l2jspace.gameserver.model.World;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.zone.type.subtype.ZoneType;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ExServerPrimitive;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.NpcHtmlMessage;

import java.util.StringTokenizer;

public class AdminZone implements IAdminCommandHandler {
    private static final String[] ADMIN_COMMANDS =
            {
                    "admin_zone"
            };

    @Override
    public void useAdminCommand(String command, Player player) {
        final StringTokenizer st = new StringTokenizer(command, " ");
        st.nextToken();

        if (!st.hasMoreTokens()) {
            showHtml(player);
            return;
        }

        switch (st.nextToken().toLowerCase()) {
            case "show":
                try {
                    final ExServerPrimitive debug = player.getDebugPacket("ZONE");
                    debug.reset();

                    final String param = st.nextToken().toLowerCase();
                    switch (param) {
                        case "all":
                            for (ZoneType zone : player.getZones(false))
                                zone.visualizeZone(debug, player.getZ());

                            debug.sendTo(player);

                            showHtml(player);
                            break;

                        case "clear":
                            debug.sendTo(player);

                            showHtml(player);
                            break;

                        default:
                            ZoneManager.getInstance().getZoneById(Integer.parseInt(param)).visualizeZone(debug, player.getZ());

                            debug.sendTo(player);
                            break;
                    }
                } catch (Exception e) {
                    player.sendMessage("Invalid parameter for //zone show.");
                }
                break;

            default:
                showHtml(player);
                break;
        }
    }

    private static void showHtml(Player player) {
        int x = player.getX();
        int y = player.getY();
        int rx = (x - World.WORLD_X_MIN) / World.TILE_SIZE + World.TILE_X_MIN;
        int ry = (y - World.WORLD_Y_MIN) / World.TILE_SIZE + World.TILE_Y_MIN;

        final NpcHtmlMessage html = new NpcHtmlMessage(0);
        html.setFile("data/html/admin/zone.htm");

        html.replace("%MAPREGION%", "[x:" + MapRegionData.getMapRegionX(x) + " y:" + MapRegionData.getMapRegionY(y) + "]");
        html.replace("%GEOREGION%", rx + "_" + ry);
        html.replace("%CLOSESTTOWN%", MapRegionData.getInstance().getClosestTownName(x, y));
        html.replace("%CURRENTLOC%", x + ", " + y + ", " + player.getZ());

        final StringBuilder sb = new StringBuilder(100);

        for (ZoneId zoneId : ZoneId.VALUES) {
            if (player.isInsideZone(zoneId))
                StringUtil.append(sb, zoneId, "<br1>");
        }
        html.replace("%ZONES%", sb.toString());

        // Reset the StringBuilder for another use.
        sb.setLength(0);

        for (ZoneType zoneType : World.getInstance().getRegion(x, y).getZones()) {
            if (zoneType.isCharacterInZone(player))
                StringUtil.append(sb, zoneType.getId(), " ");
        }
        html.replace("%ZLIST%", sb.toString());
        player.sendPacket(html);
    }

    @Override
    public String[] getAdminCommandList() {
        return ADMIN_COMMANDS;
    }
}