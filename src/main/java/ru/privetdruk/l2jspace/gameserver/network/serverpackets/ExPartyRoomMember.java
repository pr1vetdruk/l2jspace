package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.data.xml.MapRegionData;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.group.PartyMatchRoom;

public class ExPartyRoomMember extends L2GameServerPacket {
    private final PartyMatchRoom _room;
    private final int _mode;

    public ExPartyRoomMember(PartyMatchRoom room, int mode) {
        _room = room;
        _mode = mode;
    }

    @Override
    protected void writeImpl() {
        writeC(0xfe);
        writeH(0x0e);
        writeD(_mode);
        writeD(_room.getMembersCount());

        for (Player member : _room.getMembers()) {
            writeD(member.getId());
            writeS(member.getName());
            writeD(member.getActiveClass());
            writeD(member.getStatus().getLevel());
            writeD(MapRegionData.getInstance().getClosestLocation(member.getX(), member.getY()));

            if (_room.isLeader(member))
                writeD(1);
            else {
                if ((_room.getLeader().isInParty() && member.isInParty()) && (_room.getLeader().getParty().getLeaderObjectId() == member.getParty().getLeaderObjectId()))
                    writeD(2);
                else
                    writeD(0);
            }
        }
    }
}