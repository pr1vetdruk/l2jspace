package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import java.util.HashMap;
import java.util.Map;

import ru.privetdruk.l2jspace.common.pool.ThreadPool;

import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.data.manager.BuyListManager;
import ru.privetdruk.l2jspace.gameserver.enums.Paperdoll;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Merchant;
import ru.privetdruk.l2jspace.gameserver.model.buylist.NpcBuyList;
import ru.privetdruk.l2jspace.gameserver.model.buylist.Product;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Item;
import ru.privetdruk.l2jspace.gameserver.model.itemcontainer.Inventory;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ActionFailed;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ShopPreviewInfo;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.UserInfo;

public final class RequestPreviewItem extends L2GameClientPacket {
    @SuppressWarnings("unused")
    private int _unk;
    private int _listId;
    private int _count;
    private int[] _items;

    @Override
    protected void readImpl() {
        _unk = readD();
        _listId = readD();
        _count = readD();

        if (_count < 0)
            _count = 0;
        else if (_count > 100)
            return; // prevent too long lists

        // Create _items table that will contain all ItemID to Wear
        _items = new int[_count];

        // Fill _items table with all ItemID to Wear
        for (int i = 0; i < _count; i++)
            _items[i] = readD();
    }

    @Override
    protected void runImpl() {
        if (_items == null)
            return;

        if (_count < 1 || _listId >= 4000000) {
            sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Get the current player and return if null
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        // Check current target of the player and the INTERACTION_DISTANCE
        final WorldObject target = player.getTarget();
        if (!player.isGM() && (target == null || !(target instanceof Merchant) || !player.isIn3DRadius(target, Npc.INTERACTION_DISTANCE)))
            return;

        // Get the current merchant targeted by the player
        final Merchant merchant = (target instanceof Merchant) ? (Merchant) target : null;
        if (merchant == null)
            return;

        final NpcBuyList buyList = BuyListManager.getInstance().getBuyList(_listId);
        if (buyList == null)
            return;

        long totalPrice = 0;

        _listId = buyList.getListId();

        final Map<Paperdoll, Integer> items = new HashMap<>();
        for (int i = 0; i < _count; i++) {
            int itemId = _items[i];

            final Product product = buyList.get(itemId);
            if (product == null)
                return;

            final Item template = product.getItem();
            if (template == null)
                continue;

            final Paperdoll slot = Inventory.getPaperdollIndex(template.getBodyPart());
            if (slot == Paperdoll.NULL)
                continue;

            if (items.containsKey(slot)) {
                player.sendPacket(SystemMessageId.YOU_CAN_NOT_TRY_THOSE_ITEMS_ON_AT_THE_SAME_TIME);
                return;
            }
            items.put(slot, itemId);

            totalPrice += Config.WEAR_PRICE;
            if (totalPrice > Integer.MAX_VALUE)
                return;
        }

        // Charge buyer and add tax to castle treasury if not owned by npc clan because a Try On is not Free
        if (totalPrice < 0 || !player.reduceAdena("Wear", (int) totalPrice, player.getCurrentFolk(), true)) {
            player.sendPacket(SystemMessageId.YOU_NOT_ENOUGH_ADENA);
            return;
        }

        if (!items.isEmpty()) {
            player.sendPacket(new ShopPreviewInfo(items));

            // Schedule task
            ThreadPool.schedule(() ->
            {
                player.sendPacket(SystemMessageId.NO_LONGER_TRYING_ON);
                player.sendPacket(new UserInfo(player));
            }, Config.WEAR_DELAY * 1000);
        }
    }
}