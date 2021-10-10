package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

public class RecipeShopMsg extends L2GameServerPacket {
    private final Player _player;

    public RecipeShopMsg(Player player) {
        _player = player;
    }

    @Override
    protected final void writeImpl() {
        writeC(0xdb);

        writeD(_player.getId());
        writeS(_player.getManufactureList().getStoreName());
    }
}