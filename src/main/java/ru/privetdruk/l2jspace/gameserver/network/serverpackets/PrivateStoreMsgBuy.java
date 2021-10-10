package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

public class PrivateStoreMsgBuy extends L2GameServerPacket {
    private final Player _player;
    private String _message;

    public PrivateStoreMsgBuy(Player player) {
        _player = player;

        if (_player.getBuyList() != null)
            _message = _player.getBuyList().getTitle();
    }

    @Override
    protected final void writeImpl() {
        writeC(0xb9);

        writeD(_player.getId());
        writeS(_message);
    }
}