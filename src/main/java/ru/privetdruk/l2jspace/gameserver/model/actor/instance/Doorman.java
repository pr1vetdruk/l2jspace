package ru.privetdruk.l2jspace.gameserver.model.actor.instance;

import ru.privetdruk.l2jspace.gameserver.data.cache.HtmCache;
import ru.privetdruk.l2jspace.gameserver.data.xml.DoorData;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ActionFailed;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.NpcHtmlMessage;

import java.util.StringTokenizer;

/**
 * An instance type extending {@link Folk}, used to open doors and teleport into specific locations. Used notably by Border Frontier captains, and Doorman (clan halls and castles).<br>
 * <br>
 * It has an active siege (false by default) and ownership (true by default) checks, which are overidden on children classes.<br>
 * <br>
 * It is the mother class of {@link ClanHallDoorman} and {@link CastleDoorman}.
 */
public class Doorman extends Folk {
    public Doorman(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (command.startsWith("open_doors")) {
            if (isOwnerClan(player)) {
                if (isUnderSiege()) {
                    cannotManageDoors(player);
                    player.sendPacket(SystemMessageId.GATES_NOT_OPENED_CLOSED_DURING_SIEGE);
                } else
                    openDoors(player, command);
            }
        } else if (command.startsWith("close_doors")) {
            if (isOwnerClan(player)) {
                if (isUnderSiege()) {
                    cannotManageDoors(player);
                    player.sendPacket(SystemMessageId.GATES_NOT_OPENED_CLOSED_DURING_SIEGE);
                } else
                    closeDoors(player, command);
            }
        } else
            super.onBypassFeedback(player, command);
    }

    @Override
    public void showChatWindow(Player player) {
        final NpcHtmlMessage html = new NpcHtmlMessage(getId());
        html.setFile("data/html/doormen/" + getTemplate().getNpcId() + ((!isOwnerClan(player)) ? "-no.htm" : ".htm"));
        html.replace("%objectId%", getId());
        player.sendPacket(html);

        player.sendPacket(ActionFailed.STATIC_PACKET);
    }

    @Override
    protected boolean isTeleportAllowed(Player player) {
        return isOwnerClan(player);
    }

    protected void openDoors(Player player, String command) {
        StringTokenizer st = new StringTokenizer(command.substring(10), ", ");
        st.nextToken();

        while (st.hasMoreTokens())
            DoorData.getInstance().getDoor(Integer.parseInt(st.nextToken())).openMe();
    }

    protected void closeDoors(Player player, String command) {
        StringTokenizer st = new StringTokenizer(command.substring(11), ", ");
        st.nextToken();

        while (st.hasMoreTokens())
            DoorData.getInstance().getDoor(Integer.parseInt(st.nextToken())).closeMe();
    }

    protected void cannotManageDoors(Player player) {
        String path = "data/html/doormen/" + getNpcId() + "-busy.htm";
        if (!HtmCache.getInstance().isLoadable(path))
            path = "data/html/doormen/busy.htm";

        final NpcHtmlMessage html = new NpcHtmlMessage(getId());
        html.setFile(path);
        player.sendPacket(html);

        player.sendPacket(ActionFailed.STATIC_PACKET);
    }

    protected boolean isOwnerClan(Player player) {
        return true;
    }

    protected boolean isUnderSiege() {
        return false;
    }
}