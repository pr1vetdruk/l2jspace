package ru.privetdruk.l2jspace.gameserver.model.itemcontainer.listeners;

import ru.privetdruk.l2jspace.gameserver.enums.Paperdoll;
import ru.privetdruk.l2jspace.gameserver.enums.items.WeaponType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;

public class BowRodListener implements OnEquipListener {
    private static BowRodListener instance = new BowRodListener();

    public static BowRodListener getInstance() {
        return instance;
    }

    @Override
    public void onEquip(Paperdoll slot, ItemInstance item, Playable actor) {
        if (slot != Paperdoll.RHAND)
            return;

        if (item.getItemType() == WeaponType.BOW) {
            final ItemInstance arrow = actor.getInventory().findArrowForBow(item.getItem());
            if (arrow != null)
                actor.getInventory().setPaperdollItem(Paperdoll.LHAND, arrow);
        }
    }

    @Override
    public void onUnequip(Paperdoll slot, ItemInstance item, Playable actor) {
        if (slot != Paperdoll.RHAND)
            return;

        if (item.getItemType() == WeaponType.BOW || item.getItemType() == WeaponType.FISHINGROD) {
            final ItemInstance lHandItem = actor.getSecondaryWeaponInstance();
            if (lHandItem != null)
                actor.getInventory().setPaperdollItem(Paperdoll.LHAND, null);
        }
    }
}