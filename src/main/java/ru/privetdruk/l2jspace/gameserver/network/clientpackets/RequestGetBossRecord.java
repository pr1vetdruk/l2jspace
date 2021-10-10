package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.data.manager.RaidPointManager;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ExGetBossRecord;

import java.util.Map;

public class RequestGetBossRecord extends L2GameClientPacket {
    @SuppressWarnings("unused")
    private int _bossId;

    @Override
    protected void readImpl() {
        _bossId = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        final int points = RaidPointManager.getInstance().getPointsByOwnerId(player.getId());
        final int ranking = RaidPointManager.getInstance().calculateRanking(player.getId());
        final Map<Integer, Integer> list = RaidPointManager.getInstance().getList(player);

        player.sendPacket(new ExGetBossRecord(ranking, points, list));
    }
}