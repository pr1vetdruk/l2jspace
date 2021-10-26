package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.trade.TradeItem;

import java.util.List;

public class PrivateStoreListBuy extends L2GameServerPacket {
    private final Player _storePlayer;
    private final int _playerAdena;
    private final List<TradeItem> _items;

    public PrivateStoreListBuy(Player player, Player storePlayer) {
        _storePlayer = storePlayer;
        _storePlayer.getSellList().updateItems();

        _playerAdena = player.getAdena();
        _items = _storePlayer.getBuyList().getAvailableItems(player.getInventory());
    }

    @Override
    protected final void writeImpl() {
        writeC(0xb8);
        writeD(_storePlayer.getId());
        writeD(_playerAdena);
        writeD(_items.size());

        for (TradeItem item : _items) {
            writeD(item.getObjectId());
            writeD(item.getItem().getItemId());
            writeH(item.getEnchant());
            writeD(item.getCount());
            writeD(item.getItem().getReferencePrice());
            writeH(0);
            writeD(item.getItem().getSlot().getId());
            writeH(item.getItem().getType2());
            writeD(item.getPrice());
            writeD(item.getQuantity());
        }
    }
}