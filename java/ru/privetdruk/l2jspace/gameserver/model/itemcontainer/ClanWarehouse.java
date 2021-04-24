package ru.privetdruk.l2jspace.gameserver.model.itemcontainer;

import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.enums.items.ItemLocation;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;

public final class ClanWarehouse extends ItemContainer {
    private final Clan _clan;

    public ClanWarehouse(Clan clan) {
        _clan = clan;
    }

    @Override
    public String getName() {
        return "ClanWarehouse";
    }

    @Override
    public int getOwnerId() {
        return _clan.getClanId();
    }

    @Override
    public Player getOwner() {
        return _clan.getLeader().getPlayerInstance();
    }

    @Override
    public ItemLocation getBaseLocation() {
        return ItemLocation.CLANWH;
    }

    @Override
    public boolean validateCapacity(int slotCount) {
        if (slotCount == 0)
            return true;

        return _items.size() + slotCount <= Config.WAREHOUSE_SLOTS_CLAN;
    }
}