package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;

/**
 * format ddddd
 */
public class GetItem extends L2GameServerPacket {
    private final ItemInstance _item;
    private final int _playerId;

    public GetItem(ItemInstance item, int playerId) {
        _item = item;
        _playerId = playerId;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x0d);
        writeD(_playerId);
        writeD(_item.getId());

        writeD(_item.getX());
        writeD(_item.getY());
        writeD(_item.getZ());
    }
}