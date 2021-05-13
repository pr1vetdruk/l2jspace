package ru.privetdruk.l2jspace.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import ru.privetdruk.l2jspace.gameserver.data.manager.CastleManager;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ActionFailed;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ItemList;

/**
 * An instance type extending {@link Folk}, used by Broadcasting Towers.<br>
 * <br>
 * Those NPCs allow {@link Player}s to spectate areas (sieges, olympiads).
 */
public final class BroadcastingTower extends Folk {
    public BroadcastingTower(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (player.isEventPlayer()) {
            player.sendMessage("Вы участвуете в ивенте. Запрещено.");
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (command.startsWith("observe")) {
            StringTokenizer st = new StringTokenizer(command);
            st.nextToken();

            int cost = Integer.parseInt(st.nextToken());
            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());
            int z = Integer.parseInt(st.nextToken());

            if (command.startsWith("observeSiege") && CastleManager.getInstance().getActiveSiege(x, y, z) == null) {
                player.sendPacket(SystemMessageId.ONLY_VIEW_SIEGE);
                return;
            }

            if (player.reduceAdena("Broadcast", cost, this, true)) {
                player.enterObserverMode(x, y, z);
                player.sendPacket(new ItemList(player, false));
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    @Override
    public String getHtmlPath(int npcId, int val) {
        String filename = "";
        if (val == 0)
            filename = "" + npcId;
        else
            filename = npcId + "-" + val;

        return "data/html/observation/" + filename + ".htm";
    }
}