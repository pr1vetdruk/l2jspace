package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.data.xml.MapRegionData;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.group.PartyMatchRoom;

public class ExManagePartyRoomMember extends L2GameServerPacket {
    private final Player _player;
    private final PartyMatchRoom _room;
    private final int _mode;

    public ExManagePartyRoomMember(Player player, PartyMatchRoom room, int mode) {
        _player = player;
        _room = room;
        _mode = mode;
    }

    @Override
    protected void writeImpl() {
        writeC(0xfe);
        writeH(0x10);

        writeD(_mode);
        writeD(_player.getId());
        writeS(_player.getName());
        writeD(_player.getActiveClass());
        writeD(_player.getStatus().getLevel());
        writeD(MapRegionData.getInstance().getClosestLocation(_player.getX(), _player.getY()));

        if (_room.isLeader(_player))
            writeD(1);
        else {
            if ((_room.getLeader().isInParty() && _player.isInParty()) && (_room.getLeader().getParty().getLeaderObjectId() == _player.getParty().getLeaderObjectId()))
                writeD(2);
            else
                writeD(0);
        }
    }
}