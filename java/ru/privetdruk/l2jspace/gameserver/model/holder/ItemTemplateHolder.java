package ru.privetdruk.l2jspace.gameserver.model.holder;

import ru.privetdruk.l2jspace.common.data.StatSet;

/**
 * An holder used for item stored on PlayerTemplate.
 */
public class ItemTemplateHolder extends IntIntHolder {
    private final boolean _isEquipped;

    public ItemTemplateHolder(StatSet set) {
        super(set.getInteger("id"), set.getInteger("count"));

        _isEquipped = set.getBool("isEquipped", true);
    }

    public final boolean isEquipped() {
        return _isEquipped;
    }
}