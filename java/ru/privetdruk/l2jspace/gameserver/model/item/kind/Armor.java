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

        if (getBodyPart() == Item.SLOT_NECK || getBodyPart() == Item.SLOT_FACE || getBodyPart() == Item.SLOT_HAIR || getBodyPart() == Item.SLOT_HAIRALL || (getBodyPart() & Item.SLOT_L_EAR) != 0 || (getBodyPart() & Item.SLOT_L_FINGER) != 0 || (getBodyPart() & Item.SLOT_BACK) != 0) {
            _type1 = Item.TYPE1_WEAPON_RING_EARRING_NECKLACE;
            _type2 = Item.TYPE2_ACCESSORY;
        } else {
            if (_type == ArmorType.NONE && getBodyPart() == Item.SLOT_L_HAND) // retail define shield as NONE
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
    public final int getItemMask() {
        return getItemType().mask();
    }
}