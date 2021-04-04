package ru.privetdruk.l2jspace.gameserver.model.itemcontainer.listeners;

import ru.privetdruk.l2jspace.gameserver.enums.Paperdoll;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;

public interface OnEquipListener {
    public void onEquip(Paperdoll slot, ItemInstance item, Playable actor);

    public void onUnequip(Paperdoll slot, ItemInstance item, Playable actor);
}