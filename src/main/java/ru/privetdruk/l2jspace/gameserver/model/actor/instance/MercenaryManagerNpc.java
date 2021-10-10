package ru.privetdruk.l2jspace.gameserver.model.actor.instance;

import ru.privetdruk.l2jspace.gameserver.data.manager.BuyListManager;
import ru.privetdruk.l2jspace.gameserver.data.manager.SevenSignsManager;
import ru.privetdruk.l2jspace.gameserver.enums.SealType;
import ru.privetdruk.l2jspace.gameserver.enums.actors.NpcTalkCond;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.model.buylist.NpcBuyList;
import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.BuyList;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.NpcHtmlMessage;

import java.util.StringTokenizer;

public final class MercenaryManagerNpc extends Folk {
    public MercenaryManagerNpc(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        final NpcTalkCond condition = getNpcTalkCond(player);
        if (condition != NpcTalkCond.OWNER)
            return;

        if (command.startsWith("back"))
            showChatWindow(player);
        else if (command.startsWith("how_to")) {
            final NpcHtmlMessage html = new NpcHtmlMessage(getId());
            html.setFile("data/html/mercmanager/mseller005.htm");
            html.replace("%objectId%", getId());
            player.sendPacket(html);
        } else if (command.startsWith("hire")) {
            // Can't buy new mercenaries if seal validation period isn't reached.
            if (!SevenSignsManager.getInstance().isSealValidationPeriod()) {
                final NpcHtmlMessage html = new NpcHtmlMessage(getId());
                html.setFile("data/html/mercmanager/msellerdenial.htm");
                html.replace("%objectId%", getId());
                player.sendPacket(html);
                return;
            }

            final StringTokenizer st = new StringTokenizer(command, " ");
            st.nextToken();

            final NpcBuyList buyList = BuyListManager.getInstance().getBuyList(Integer.parseInt(getNpcId() + st.nextToken()));
            if (buyList == null || !buyList.isNpcAllowed(getNpcId()))
                return;

            player.tempInventoryDisable();
            player.sendPacket(new BuyList(buyList, player.getAdena(), 0));

            final NpcHtmlMessage html = new NpcHtmlMessage(getId());
            html.setFile("data/html/mercmanager/mseller004.htm");
            player.sendPacket(html);
        } else if (command.startsWith("merc_limit")) {
            final NpcHtmlMessage html = new NpcHtmlMessage(getId());
            html.setFile("data/html/mercmanager/" + ((getCastle().getCastleId() == 5) ? "aden_msellerLimit.htm" : "msellerLimit.htm"));
            html.replace("%castleName%", getCastle().getName());
            html.replace("%objectId%", getId());
            player.sendPacket(html);
        } else
            super.onBypassFeedback(player, command);
    }

    @Override
    public void showChatWindow(Player player) {
        final NpcHtmlMessage html = new NpcHtmlMessage(getId());

        final NpcTalkCond condition = getNpcTalkCond(player);
        if (condition == NpcTalkCond.NONE)
            html.setFile("data/html/mercmanager/mseller002.htm");
        else if (condition == NpcTalkCond.UNDER_SIEGE)
            html.setFile("data/html/mercmanager/mseller003.htm");
        else {
            // Different output depending about who is currently owning the Seal of Strife.
            switch (SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE)) {
                case DAWN:
                    html.setFile("data/html/mercmanager/mseller001_dawn.htm");
                    break;

                case DUSK:
                    html.setFile("data/html/mercmanager/mseller001_dusk.htm");
                    break;

                default:
                    html.setFile("data/html/mercmanager/mseller001.htm");
                    break;
            }
        }

        html.replace("%objectId%", getId());
        player.sendPacket(html);
    }

    @Override
    protected NpcTalkCond getNpcTalkCond(Player player) {
        if (getCastle() != null && player.getClan() != null) {
            if (getCastle().getSiege().isInProgress())
                return NpcTalkCond.UNDER_SIEGE;

            if (getCastle().getOwnerId() == player.getClanId() && player.hasClanPrivileges(Clan.CP_CS_MERCENARIES))
                return NpcTalkCond.OWNER;
        }
        return NpcTalkCond.NONE;
    }
}