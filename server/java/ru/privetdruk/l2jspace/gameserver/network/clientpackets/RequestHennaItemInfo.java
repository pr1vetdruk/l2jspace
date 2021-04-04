package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.data.xml.HennaData;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.item.Henna;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.HennaItemInfo;

public final class RequestHennaItemInfo extends L2GameClientPacket {
    private int _symbolId;

    @Override
    protected void readImpl() {
        _symbolId = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        final Henna henna = HennaData.getInstance().getHenna(_symbolId);
        if (henna == null)
            return;

        player.sendPacket(new HennaItemInfo(henna, player));
    }
}