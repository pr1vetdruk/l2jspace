package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.data.sql.ClanTable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.clanhall.SiegableHall;
import ru.privetdruk.l2jspace.gameserver.model.entity.Castle;
import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;

import java.util.Calendar;

public class SiegeInfo extends L2GameServerPacket {
    private Castle _castle;
    private SiegableHall _hall;

    public SiegeInfo(Castle castle) {
        _castle = castle;
    }

    public SiegeInfo(SiegableHall hall) {
        _hall = hall;
    }

    @Override
    protected final void writeImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        writeC(0xc9);

        if (_castle != null) {
            final int ownerId = _castle.getOwnerId();

            writeD(_castle.getCastleId());
            writeD((ownerId == player.getClanId() && player.isClanLeader()) ? 0x01 : 0x00);
            writeD(ownerId);

            Clan clan = null;
            if (ownerId > 0)
                clan = ClanTable.getInstance().getClan(ownerId);

            if (clan != null) {
                writeS(clan.getName());
                writeS(clan.getLeaderName());
                writeD(clan.getAllyId());
                writeS(clan.getAllyName());
            } else {
                writeS("NPC");
                writeS("");
                writeD(0);
                writeS("");
            }

            writeD((int) (Calendar.getInstance().getTimeInMillis() / 1000));
            writeD((int) (_castle.getSiege().getSiegeDate().getTimeInMillis() / 1000));
        } else {
            final int ownerId = _hall.getOwnerId();

            writeD(_hall.getId());
            writeD(((ownerId == player.getClanId()) && player.isClanLeader()) ? 0x01 : 0x00);
            writeD(ownerId);

            Clan clan = null;
            if (ownerId > 0)
                clan = ClanTable.getInstance().getClan(ownerId);

            if (clan != null) {
                writeS(clan.getName());
                writeS(clan.getLeaderName());
                writeD(clan.getAllyId());
                writeS(clan.getAllyName());
            } else {
                writeS("NPC");
                writeS("");
                writeD(0);
                writeS("");
            }

            writeD((int) (Calendar.getInstance().getTimeInMillis() / 1000));
            writeD((int) ((_hall.getNextSiegeTime()) / 1000));
        }
        writeD(0x00);
    }
}