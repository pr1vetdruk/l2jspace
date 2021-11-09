package ru.privetdruk.l2jspace.gameserver.model.item.kind;

public enum Costume {
    NONE(null),
    BLUE_WOLF_ROBE(new ArmorSet(2416, 5720, 5736, 2398));

    private final ArmorSet set;

    Costume(ArmorSet set) {
        this.set = set;
    }

    public ArmorSet getSet() {
        return set;
    }
}
