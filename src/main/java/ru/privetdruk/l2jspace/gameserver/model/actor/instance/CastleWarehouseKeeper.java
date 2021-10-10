package ru.privetdruk.l2jspace.gameserver.model.actor.instance;

import ru.privetdruk.l2jspace.gameserver.enums.actors.NpcTalkCond;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ActionFailed;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.NpcHtmlMessage;

public class CastleWarehouseKeeper extends WarehouseKeeper {
    public CastleWarehouseKeeper(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public boolean isWarehouse() {
        return true;
    }

    @Override
    public void showChatWindow(Player player, int val) {
        player.sendPacket(ActionFailed.STATIC_PACKET);

        final NpcHtmlMessage html = new NpcHtmlMessage(getId());

        final NpcTalkCond condition = getNpcTalkCond(player);
        if (condition == NpcTalkCond.NONE)
            html.setFile("data/html/castlewarehouse/castlewarehouse-no.htm");
        else if (condition == NpcTalkCond.UNDER_SIEGE)
            html.setFile("data/html/castlewarehouse/castlewarehouse-busy.htm");
        else {
            if (val == 0)
                html.setFile("data/html/castlewarehouse/castlewarehouse.htm");
            else
                html.setFile("data/html/castlewarehouse/castlewarehouse-" + val + ".htm");
        }
        html.replace("%objectId%", getId());
        html.replace("%npcname%", getName());
        player.sendPacket(html);
    }

    @Override
    protected NpcTalkCond getNpcTalkCond(Player player) {
        if (getCastle() != null && player.getClan() != null) {
            if (getCastle().getSiege().isInProgress())
                return NpcTalkCond.UNDER_SIEGE;

            if (getCastle().getOwnerId() == player.getClanId())
                return NpcTalkCond.OWNER;
        }
        return NpcTalkCond.NONE;
    }
}