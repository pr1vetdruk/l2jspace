package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.common.util.ArraysUtil;

import ru.privetdruk.l2jspace.Config;
import ru.privetdruk.l2jspace.gameserver.enums.actors.OperateType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.trade.BuyProcessItem;
import ru.privetdruk.l2jspace.gameserver.model.trade.TradeList;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.PrivateStoreManageListBuy;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.PrivateStoreMsgBuy;

public final class SetPrivateStoreListBuy extends L2GameClientPacket {
    private static final int BATCH_LENGTH = 16;

    private BuyProcessItem[] _items = null;

    @Override
    protected void readImpl() {
        final int count = readD();
        if (count < 1 || count > Config.MAX_ITEM_IN_PACKET || count * BATCH_LENGTH != _buf.remaining())
            return;

        _items = new BuyProcessItem[count];

        for (int i = 0; i < count; i++) {
            final int itemId = readD();
            final int enchant = readH();
            readH();
            final int cnt = readD();
            final int price = readD();

            _items[i] = new BuyProcessItem(itemId, cnt, price, enchant);
        }
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        // Retrieve and clear the buylist.
        final TradeList tradeList = player.getBuyList();
        tradeList.clear();

        // Integrity check.
        if (ArraysUtil.isEmpty(_items)) {
            player.setOperateType(OperateType.NONE);
            player.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            return;
        }

        // Integrity check.
        if (!player.getInventory().canPassBuyProcess(_items)) {
            player.setOperateType(OperateType.NONE);
            return;
        }

        if (!player.getAccessLevel().allowTransaction()) {
            player.setOperateType(OperateType.NONE);
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }

        // Check multiple conditions. Message and OperateType reset are sent directly from the method.
        if (!player.canOpenPrivateStore(false))
            return;

        // Check maximum number of allowed slots.
        if (_items.length > player.getStatus().getPrivateBuyStoreLimit()) {
            player.setOperateType(OperateType.NONE);
            player.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            return;
        }

        long totalCost = 0;

        for (BuyProcessItem i : _items) {
            if (!i.addToTradeList(tradeList)) {
                player.sendPacket(SystemMessageId.EXCEEDED_THE_MAXIMUM);
                player.sendPacket(new PrivateStoreManageListBuy(player));
                return;
            }

            totalCost += i.getCost();
            if (totalCost > Integer.MAX_VALUE) {
                player.sendPacket(SystemMessageId.EXCEEDED_THE_MAXIMUM);
                player.sendPacket(new PrivateStoreManageListBuy(player));
                return;
            }
        }

        // Check for available funds
        if (totalCost > player.getAdena()) {
            player.sendPacket(SystemMessageId.THE_PURCHASE_PRICE_IS_HIGHER_THAN_MONEY);
            player.sendPacket(new PrivateStoreManageListBuy(player));
            return;
        }

        player.getMove().stop();
        player.sitDown();
        player.setOperateType(OperateType.BUY);
        player.broadcastUserInfo();
        player.broadcastPacket(new PrivateStoreMsgBuy(player));
    }
}