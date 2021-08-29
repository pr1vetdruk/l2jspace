package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.enums.SiegeSide;
import ru.privetdruk.l2jspace.gameserver.model.entity.Castle;
import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;

import java.util.List;

public class SiegeDefenderList extends L2GameServerPacket {
    private final Castle _castle;

    public SiegeDefenderList(Castle castle) {
        _castle = castle;
    }

    @Override
    protected final void writeImpl() {
        writeC(0xcb);
        writeD(_castle.getCastleId());
        writeD(0x00);
        writeD(0x01);
        writeD(0x00);

        final List<Clan> defenders = _castle.getSiege().getDefenderClans();
        final List<Clan> pendingDefenders = _castle.getSiege().getPendingClans();
        final int size = defenders.size() + pendingDefenders.size();

        if (size > 0) {
            writeD(size);
            writeD(size);

            for (Clan clan : defenders) {
                writeD(clan.getClanId());
                writeS(clan.getName());
                writeS(clan.getLeaderName());
                writeD(clan.getCrestId());
                writeD(0x00);

                final SiegeSide side = _castle.getSiege().getSide(clan);
                if (side == SiegeSide.OWNER)
                    writeD(0x01);
                else if (side == SiegeSide.PENDING)
                    writeD(0x02);
                else if (side == SiegeSide.DEFENDER)
                    writeD(0x03);
                else
                    writeD(0x00);

                writeD(clan.getAllyId());
                writeS(clan.getAllyName());
                writeS("");
                writeD(clan.getAllyCrestId());
            }

            for (Clan clan : pendingDefenders) {
                writeD(clan.getClanId());
                writeS(clan.getName());
                writeS(clan.getLeaderName());
                writeD(clan.getCrestId());
                writeD(0x00);
                writeD(0x02);
                writeD(clan.getAllyId());
                writeS(clan.getAllyName());
                writeS("");
                writeD(clan.getAllyCrestId());
            }
        } else {
            writeD(0x00);
            writeD(0x00);
        }
    }
}