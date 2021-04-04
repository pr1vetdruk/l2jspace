package ru.privetdruk.l2jspace.gameserver.model.itemcontainer;

import ru.privetdruk.l2jspace.gameserver.enums.items.ItemLocation;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

public class PcWarehouse extends ItemContainer {
    private final Player _owner;

    public PcWarehouse(Player owner) {
        _owner = owner;
    }

    @Override
    public String getName() {
        return "Warehouse";
    }

    @Override
    public Player getOwner() {
        return _owner;
    }

    @Override
    public ItemLocation getBaseLocation() {
        return ItemLocation.WAREHOUSE;
    }

    @Override
    public boolean validateCapacity(int slotCount) {
        if (slotCount == 0)
            return true;

        return _items.size() + slotCount <= _owner.getStatus().getWareHouseLimit();
    }
}