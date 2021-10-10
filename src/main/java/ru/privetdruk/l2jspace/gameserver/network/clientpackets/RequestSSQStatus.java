package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.data.manager.SevenSignsManager;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SSQStatus;

public final class RequestSSQStatus extends L2GameClientPacket {
    private int _page;

    @Override
    protected void readImpl() {
        _page = readC();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        if ((SevenSignsManager.getInstance().isSealValidationPeriod() || SevenSignsManager.getInstance().isCompResultsPeriod()) && _page == 4)
            return;

        player.sendPacket(new SSQStatus(player.getId(), _page));
    }
}