package ru.privetdruk.l2jspace.gameserver.skill.extractable;

import ru.privetdruk.l2jspace.gameserver.model.holder.IntIntHolder;

import java.util.List;

public class ExtractableProductItem {
    private final List<IntIntHolder> _items;
    private final double _chance;

    public ExtractableProductItem(List<IntIntHolder> items, double chance) {
        _items = items;
        _chance = chance;
    }

    public List<IntIntHolder> getItems() {
        return _items;
    }

    public double getChance() {
        return _chance;
    }
}