package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.enums.actors.HennaType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.container.player.HennaList;
import ru.privetdruk.l2jspace.gameserver.model.item.Henna;

import java.util.List;

public class GMHennaInfo extends L2GameServerPacket {
    private final HennaList _hennaList;

    public GMHennaInfo(Player player) {
        _hennaList = player.getHennaList();
    }

    @Override
    protected void writeImpl() {
        writeC(0xea);

        writeC(_hennaList.getStat(HennaType.INT));
        writeC(_hennaList.getStat(HennaType.STR));
        writeC(_hennaList.getStat(HennaType.CON));
        writeC(_hennaList.getStat(HennaType.MEN));
        writeC(_hennaList.getStat(HennaType.DEX));
        writeC(_hennaList.getStat(HennaType.WIT));

        writeD(_hennaList.getMaxSize());

        final List<Henna> hennas = _hennaList.getHennas();
        writeD(hennas.size());
        for (Henna h : hennas) {
            writeD(h.getSymbolId());
            writeD(_hennaList.canBeUsedBy(h) ? h.getSymbolId() : 0);
        }
    }
}