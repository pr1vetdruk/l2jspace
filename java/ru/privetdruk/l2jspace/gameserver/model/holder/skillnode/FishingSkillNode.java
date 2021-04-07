package ru.privetdruk.l2jspace.gameserver.model.holder.skillnode;

import ru.privetdruk.l2jspace.common.data.StatSet;

/**
 * A datatype used by fishing (or common) skill types. It extends {@link SkillNode}.
 */
public final class FishingSkillNode extends SkillNode {
    private final int _itemId;
    private final int _itemCount;

    private final boolean _isDwarven;

    public FishingSkillNode(StatSet set) {
        super(set);

        _itemId = set.getInteger("itemId");
        _itemCount = set.getInteger("itemCount");

        _isDwarven = set.getBool("isDwarven", false);
    }

    public int getItemId() {
        return _itemId;
    }

    public int getItemCount() {
        return _itemCount;
    }

    public boolean isDwarven() {
        return _isDwarven;
    }
}