package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.common.util.ArraysUtil;
import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.enums.actors.OperateType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.craft.ManufactureItem;
import ru.privetdruk.l2jspace.gameserver.model.craft.ManufactureList;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.RecipeShopMsg;

public final class RequestRecipeShopListSet extends L2GameClientPacket {
    private static final int BATCH_LENGTH = 8;

    private ManufactureItem[] _items;

    @Override
    protected void readImpl() {
        int count = readD();
        if (count < 0 || count > Config.MAX_ITEM_IN_PACKET || count * BATCH_LENGTH != _buf.remaining())
            count = 0;

        _items = new ManufactureItem[count];

        for (int i = 0; i < count; i++) {
            final int recipeId = readD();
            final int cost = readD();

            _items[i] = new ManufactureItem(recipeId, cost);
        }
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        // Retrieve and clear the manufacture list.
        final ManufactureList manufactureList = player.getManufactureList();
        manufactureList.clear();

        // Integrity check.
        if (ArraysUtil.isEmpty(_items)) {
            player.setOperateType(OperateType.NONE);
            player.sendPacket(SystemMessageId.NO_RECIPES_REGISTERED);
            return;
        }

        // Integrity check.
        if (!player.getRecipeBook().canPassManufactureProcess(_items)) {
            player.setOperateType(OperateType.NONE);
            return;
        }

        // Check multiple conditions. Message and OperateType reset are sent directly from the method.
        if (!player.canOpenPrivateStore(false))
            return;

        // Feed it with packet informations.
        manufactureList.set(_items);

        player.getMove().stop();
        player.sitDown();
        player.setOperateType(OperateType.MANUFACTURE);
        player.broadcastUserInfo();
        player.broadcastPacket(new RecipeShopMsg(player));
    }
}