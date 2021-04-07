package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.Config;
import ru.privetdruk.l2jspace.gameserver.data.manager.FishingChampionshipManager;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

public final class RequestExFishRanking extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        if (Config.ALLOW_FISH_CHAMPIONSHIP)
            FishingChampionshipManager.getInstance().showMidResult(player);
    }
}