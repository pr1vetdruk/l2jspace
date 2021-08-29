package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.craft.ManufactureItem;
import ru.privetdruk.l2jspace.gameserver.model.craft.ManufactureList;
import ru.privetdruk.l2jspace.gameserver.model.item.Recipe;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class RecipeShopManageList extends L2GameServerPacket {
    private final Player _player;
    private final Collection<Recipe> _recipes;

    public RecipeShopManageList(Player player, boolean isDwarven) {
        _player = player;
        _recipes = player.getRecipeBook().get(isDwarven && player.hasDwarvenCraft());

        final ManufactureList manufactureList = player.getManufactureList();
        manufactureList.setState(isDwarven);

        // Integrity check.
        final Iterator<ManufactureItem> it = manufactureList.iterator();
        while (it.hasNext()) {
            ManufactureItem item = it.next();
            if (item.isDwarven() != isDwarven || !player.getRecipeBook().hasRecipe(item.getId()))
                it.remove();
        }
    }

    @Override
    protected final void writeImpl() {
        writeC(0xd8);
        writeD(_player.getObjectId());
        writeD(_player.getAdena());
        writeD(_player.getManufactureList().isDwarven() ? 0x00 : 0x01);

        if (_recipes == null)
            writeD(0);
        else {
            writeD(_recipes.size());

            int i = 0;
            for (Recipe recipe : _recipes) {
                writeD(recipe.getId());
                writeD(++i);
            }
        }

        final List<ManufactureItem> list = _player.getManufactureList();
        writeD(list.size());

        for (ManufactureItem item : list) {
            writeD(item.getId());
            writeD(0x00);
            writeD(item.getValue());
        }
    }
}