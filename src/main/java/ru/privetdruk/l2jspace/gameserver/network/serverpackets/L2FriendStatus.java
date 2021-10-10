package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

public class L2FriendStatus extends L2GameServerPacket {
    private final int _isOnline;
    private final String _name;
    private final int _objectId;

    public L2FriendStatus(Player player, boolean isOnline) {
        _isOnline = isOnline ? 1 : 0;
        _name = player.getName();
        _objectId = player.getId();
    }

    @Override
    protected final void writeImpl() {
        writeC(0xfc);
        writeD(_isOnline);
        writeS(_name);
        writeD(_objectId);
    }
}