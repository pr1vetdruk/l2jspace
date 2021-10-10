package ru.privetdruk.l2jspace.gameserver.model.itemcontainer;

import ru.privetdruk.l2jspace.gameserver.enums.items.EtcItemType;
import ru.privetdruk.l2jspace.gameserver.enums.items.ItemLocation;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Pet;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;

public class PetInventory extends Inventory {
    private final Pet _owner;

    public PetInventory(Pet owner) {
        _owner = owner;
    }

    @Override
    public Pet getOwner() {
        return _owner;
    }

    @Override
    public int getOwnerId() {
        int id;
        try {
            id = _owner.getOwner().getId();
        } catch (NullPointerException e) {
            return 0;
        }
        return id;
    }

    @Override
    protected void refreshWeight() {
        super.refreshWeight();

        getOwner().updateAndBroadcastStatus(1);
        getOwner().sendPetInfosToOwner();
    }

    public boolean validateCapacity(ItemInstance item) {
        int slots = 0;

        if (!(item.isStackable() && getItemByItemId(item.getItemId()) != null) && item.getItemType() != EtcItemType.HERB)
            slots++;

        return validateCapacity(slots);
    }

    @Override
    public boolean validateCapacity(int slotCount) {
        if (slotCount == 0)
            return true;

        return _items.size() + slotCount <= _owner.getInventoryLimit();
    }

    public boolean validateWeight(ItemInstance item, int count) {
        return validateWeight(count * item.getItem().getWeight());
    }

    @Override
    public boolean validateWeight(int weight) {
        return _totalWeight + weight <= _owner.getWeightLimit();
    }

    @Override
    protected ItemLocation getBaseLocation() {
        return ItemLocation.PET;
    }

    @Override
    protected ItemLocation getEquipLocation() {
        return ItemLocation.PET_EQUIP;
    }

    @Override
    public void deleteMe() {
        final Player petOwner = getOwner().getOwner();
        if (petOwner != null) {
            for (ItemInstance item : _items) {
                if (petOwner.getInventory().validateCapacity(1))
                    getOwner().transferItem("return", item.getId(), item.getCount(), petOwner.getInventory(), petOwner, getOwner());
                else {
                    final ItemInstance droppedItem = dropItem("drop", item.getId(), item.getCount(), petOwner, getOwner());
                    droppedItem.dropMe(getOwner(), 70);
                }
            }
        }
        _items.clear();
    }
}