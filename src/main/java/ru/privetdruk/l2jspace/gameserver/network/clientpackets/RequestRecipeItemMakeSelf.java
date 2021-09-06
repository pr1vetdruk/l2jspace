package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.data.xml.RecipeData;
import ru.privetdruk.l2jspace.gameserver.enums.FloodProtector;
import ru.privetdruk.l2jspace.gameserver.enums.actors.OperateType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.craft.RecipeItemMaker;
import ru.privetdruk.l2jspace.gameserver.model.item.Recipe;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;

public final class RequestRecipeItemMakeSelf extends L2GameClientPacket {
    private int _recipeId;

    @Override
    protected void readImpl() {
        _recipeId = readD();
    }

    @Override
    protected void runImpl() {
        if (!getClient().performAction(FloodProtector.MANUFACTURE))
            return;

        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        if (player.getOperateType() == OperateType.MANUFACTURE || player.isCrafting())
            return;

        if (player.isInDuel() || player.isInCombat()) {
            player.sendPacket(SystemMessageId.CANT_OPERATE_PRIVATE_STORE_DURING_COMBAT);
            return;
        }

        final Recipe recipe = RecipeData.getInstance().getRecipeList(_recipeId);
        if (recipe == null)
            return;

        if (!player.getRecipeBook().hasRecipeOnSpecificBook(_recipeId, recipe.isDwarven()))
            return;

        final RecipeItemMaker maker = new RecipeItemMaker(player, recipe, player);
        if (maker._isValid)
            maker.run();
    }
}