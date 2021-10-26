package ru.privetdruk.l2jspace.gameserver.model.item.kind;

import ru.privetdruk.l2jspace.common.data.StatSet;
import ru.privetdruk.l2jspace.gameserver.enums.items.ArmorType;

/**
 * This class is dedicated to the management of armors.
 */
public final class Armor extends Item {
    private ArmorType _type;

    public Armor(StatSet set) {
        super(set);

        _type = set.getEnum("armor_type", ArmorType.class, ArmorType.NONE);

        Slot slot = getSlot();
        if (slot == Item.Slot.NECK
                || slot == Item.Slot.FACE
                || slot == Item.Slot.HAIR
                || slot == Item.Slot.HAIR_ALL
                || (slot.getId() & Item.Slot.LEFT_EAR.getId()) != 0
                || (slot.getId() & Item.Slot.LEFT_FINGER.getId()) != 0
                || (slot.getId() & Item.Slot.BACK.getId()) != 0) {
            _type1 = Item.TYPE1_WEAPON_RING_EARRING_NECKLACE;
            _type2 = Item.TYPE2_ACCESSORY;
        } else {
            if (_type == ArmorType.NONE && slot == Item.Slot.LEFT_HAND) // retail define shield as NONE
                _type = ArmorType.SHIELD;

            _type1 = Item.TYPE1_SHIELD_ARMOR;
            _type2 = Item.TYPE2_SHIELD_ARMOR;
        }
    }

    @Override
    public ArmorType getItemType() {
        return _type;
    }

    @Override
    public int getItemMask() {
        return getItemType().mask();
    }
}