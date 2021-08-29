package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.data.manager.PartyMatchRoomManager;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

import java.util.List;

public class ExListPartyMatchingWaitingRoom extends L2GameServerPacket {
    private final int _mode;
    private final List<Player> _members;

    public ExListPartyMatchingWaitingRoom(Player player, int page, int minLvl, int maxLvl, int mode) {
        _mode = mode;
        _members = PartyMatchRoomManager.getInstance().getAvailableWaitingMembers(player, minLvl, maxLvl);
    }

    @Override
    protected void writeImpl() {
        writeC(0xfe);
        writeH(0x35);

        writeD(_mode);
        writeD(_members.size());

        for (Player member : _members) {
            writeS(member.getName());
            writeD(member.getActiveClass());
            writeD(member.getStatus().getLevel());
        }
    }
}