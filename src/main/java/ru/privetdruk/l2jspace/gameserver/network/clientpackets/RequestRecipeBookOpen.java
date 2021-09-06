package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.RecipeBookItemList;

public final class RequestRecipeBookOpen extends L2GameClientPacket {
    private boolean _isDwarven;

    @Override
    protected void readImpl() {
        _isDwarven = (readD() == 0);
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        if (player.getCast().isCastingNow() || player.isAllSkillsDisabled()) {
            player.sendPacket(SystemMessageId.NO_RECIPE_BOOK_WHILE_CASTING);
            return;
        }

        player.sendPacket(new RecipeBookItemList(player, _isDwarven));
    }
}