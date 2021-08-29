package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.data.cache.HtmCache;
import ru.privetdruk.l2jspace.gameserver.enums.StatusType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Fisherman;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.MercenaryManagerNpc;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Merchant;
import ru.privetdruk.l2jspace.gameserver.model.holder.IntIntHolder;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ItemList;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.NpcHtmlMessage;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.StatusUpdate;

public final class RequestSellItem extends L2GameClientPacket {
    private static final int BATCH_LENGTH = 12; // length of the one item

    private int _listId;
    private IntIntHolder[] _items = null;

    @Override
    protected void readImpl() {
        _listId = readD();
        int count = readD();
        if (count <= 0 || count > Config.MAX_ITEM_IN_PACKET || count * BATCH_LENGTH != _buf.remaining())
            return;

        _items = new IntIntHolder[count];
        for (int i = 0; i < count; i++) {
            int objectId = readD();
            int itemId = readD();
            int cnt = readD();

            if (objectId < 1 || itemId < 1 || cnt < 1) {
                _items = null;
                return;
            }

            _items[i] = new IntIntHolder(objectId, cnt);
        }
    }

    @Override
    protected void runImpl() {
        if (_items == null)
            return;

        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        final Npc merchant = (player.getTarget() instanceof Merchant || player.getTarget() instanceof MercenaryManagerNpc) ? (Npc) player.getTarget() : null;
        if (merchant == null || !player.getAI().canDoInteract(merchant))
            return;

        if (_listId > 1000000 && merchant.getTemplate().getNpcId() != _listId - 1000000) {
            return;
        }

        long totalPrice = 0;

        // Proceed the sell
        for (IntIntHolder i : _items) {
            ItemInstance item = player.checkItemManipulation(i.getId(), i.getValue());
            if (item == null || (!item.isSellable()))
                continue;

            int price = item.getReferencePrice() / 2;
            totalPrice += (long) price * i.getValue();
            if ((Integer.MAX_VALUE / i.getValue()) < price || totalPrice > Integer.MAX_VALUE)
                return;

            player.getInventory().destroyItem("Sell", i.getId(), i.getValue(), player, merchant);
        }

        player.addAdena("Sell", (int) totalPrice, merchant, false);

        // Send the htm, if existing.
        String htmlFolder = "";
        if (merchant instanceof Fisherman)
            htmlFolder = "fisherman";
        else if (merchant instanceof Merchant)
            htmlFolder = "merchant";

        if (!htmlFolder.isEmpty()) {
            final String content = HtmCache.getInstance().getHtm("data/html/" + htmlFolder + "/" + merchant.getNpcId() + "-sold.htm");
            if (content != null) {
                final NpcHtmlMessage html = new NpcHtmlMessage(merchant.getObjectId());
                html.setHtml(content);
                html.replace("%objectId%", merchant.getObjectId());
                player.sendPacket(html);
            }
        }

        // Update current load as well
        StatusUpdate su = new StatusUpdate(player);
        su.addAttribute(StatusType.CUR_LOAD, player.getCurrentWeight());
        player.sendPacket(su);
        player.sendPacket(new ItemList(player, true));
    }
}