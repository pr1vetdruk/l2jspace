package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;

import java.util.List;

public class SellList extends L2GameServerPacket {
    private final int _money;
    private final List<ItemInstance> _items;

    public SellList(int adena, List<ItemInstance> items) {
        _money = adena;
        _items = items;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x10);
        writeD(_money);
        writeD(0x00);
        writeH(_items.size());

        for (ItemInstance item : _items) {
            writeH(item.getItem().getType1());
            writeD(item.getId());
            writeD(item.getItemId());
            writeD(item.getCount());
            writeH(item.getItem().getType2());
            writeH(item.getCustomType1());
            writeD(item.getItem().getSlot().getId());
            writeH(item.getEnchantLevel());
            writeH(item.getCustomType2());
            writeH(0x00);
            writeD(item.getItem().getReferencePrice() / 2);
        }
    }
}