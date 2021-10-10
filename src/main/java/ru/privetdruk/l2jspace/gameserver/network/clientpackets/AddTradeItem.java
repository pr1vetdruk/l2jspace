package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.model.World;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.trade.TradeItem;
import ru.privetdruk.l2jspace.gameserver.model.trade.TradeList;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.TradeItemUpdate;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.TradeOtherAdd;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.TradeOwnAdd;

public final class AddTradeItem extends L2GameClientPacket {
    @SuppressWarnings("unused")
    private int _tradeId;
    private int _objectId;
    private int _count;

    public AddTradeItem() {
    }

    @Override
    protected void readImpl() {
        _tradeId = readD();
        _objectId = readD();
        _count = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        final TradeList tradeList = player.getActiveTradeList();
        if (tradeList == null)
            return;

        final Player partner = tradeList.getPartner();
        if (partner == null || World.getInstance().getPlayer(partner.getId()) == null || partner.getActiveTradeList() == null) {
            player.sendPacket(SystemMessageId.TARGET_IS_NOT_FOUND_IN_THE_GAME);
            player.cancelActiveTrade();
            return;
        }

        if (!player.getAccessLevel().allowTransaction()) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            player.cancelActiveTrade();
            return;
        }

        if (tradeList.isConfirmed()) {
            player.sendPacket(SystemMessageId.ONCE_THE_TRADE_IS_CONFIRMED_THE_ITEM_CANNOT_BE_MOVED_AGAIN);
            return;
        }

        if (partner.getActiveTradeList().isConfirmed()) {
            player.sendPacket(SystemMessageId.CANNOT_ADJUST_ITEMS_AFTER_TRADE_CONFIRMED);
            return;
        }

        if (player.validateItemManipulation(_objectId) == null) {
            player.sendPacket(SystemMessageId.NOTHING_HAPPENED);
            return;
        }

        final TradeItem tradeItem = tradeList.addItem(_objectId, _count, 0);
        if (tradeItem == null)
            return;

        player.sendPacket(new TradeOwnAdd(tradeItem));
        player.sendPacket(new TradeItemUpdate(tradeList, player));

        tradeList.getPartner().sendPacket(new TradeOtherAdd(tradeItem));
    }
}