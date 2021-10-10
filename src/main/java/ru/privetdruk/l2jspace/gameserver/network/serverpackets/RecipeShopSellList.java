package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.craft.ManufactureItem;

import java.util.ArrayList;
import java.util.List;

public class RecipeShopSellList extends L2GameServerPacket {
    private final int _adena;

    private final int _objectId;
    private final int _mp;
    private final int _maxMp;
    private final List<ManufactureItem> _manufactureList;

    public RecipeShopSellList(Player buyer, Player manufacturer) {
        _adena = buyer.getAdena();

        _objectId = manufacturer.getId();
        _mp = (int) manufacturer.getStatus().getMp();
        _maxMp = manufacturer.getStatus().getMaxMp();
        _manufactureList = new ArrayList<>(manufacturer.getManufactureList());
    }

    @Override
    protected final void writeImpl() {
        writeC(0xd9);
        writeD(_objectId);
        writeD(_mp);
        writeD(_maxMp);
        writeD(_adena);

        writeD(_manufactureList.size());

        for (ManufactureItem item : _manufactureList) {
            writeD(item.getId());
            writeD(0x00); // unknown
            writeD(item.getValue());
        }
    }
}