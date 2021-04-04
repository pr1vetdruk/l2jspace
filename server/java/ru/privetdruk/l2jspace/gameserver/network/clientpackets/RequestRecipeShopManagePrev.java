package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ActionFailed;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.RecipeShopSellList;

public final class RequestRecipeShopManagePrev extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        // Player shouldn't be able to set stores if he/she is alike dead (dead or fake death)
        if (player.isAlikeDead()) {
            sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (!(player.getTarget() instanceof Player))
            return;

        player.sendPacket(new RecipeShopSellList(player, (Player) player.getTarget()));
    }
}