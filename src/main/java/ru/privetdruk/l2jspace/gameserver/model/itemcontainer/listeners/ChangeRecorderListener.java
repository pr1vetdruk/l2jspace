package ru.privetdruk.l2jspace.gameserver.model.itemcontainer.listeners;

import ru.privetdruk.l2jspace.gameserver.enums.Paperdoll;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.model.itemcontainer.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Recorder of alterations in a given {@link Inventory}.
 */
public class ChangeRecorderListener implements OnEquipListener {
    private final List<ItemInstance> _changed = new ArrayList<>();

    public ChangeRecorderListener(Inventory inventory) {
        inventory.addPaperdollListener(this);
    }

    @Override
    public void onEquip(Paperdoll slot, ItemInstance item, Playable actor) {
        if (!_changed.contains(item))
            _changed.add(item);
    }

    @Override
    public void onUnequip(Paperdoll slot, ItemInstance item, Playable actor) {
        if (!_changed.contains(item))
            _changed.add(item);
    }

    /**
     * @return The array of alterated {@link ItemInstance}.
     */
    public ItemInstance[] getChangedItems() {
        return _changed.toArray(new ItemInstance[_changed.size()]);
    }
}