package ru.privetdruk.l2jspace.gameserver.model.item.kind;

public class ArmorSet {
    private final int helmetId;
    private final int glovesId;
    private final int bootsId;
    private final int chestId;
    private final int legsId;

    public ArmorSet(int helmetId, int glovesId, int bootsId, int chestId, int legsId) {
        this.helmetId = helmetId;
        this.glovesId = glovesId;
        this.bootsId = bootsId;
        this.chestId = chestId;
        this.legsId = legsId;
    }

    public ArmorSet(int helmetId, int glovesId, int bootsId, int chestId) {
        this.helmetId = helmetId;
        this.glovesId = glovesId;
        this.bootsId = bootsId;
        this.chestId = chestId;
        this.legsId = 0;
    }

    public int getHelmetId() {
        return helmetId;
    }

    public int getGlovesId() {
        return glovesId;
    }

    public int getBootsId() {
        return bootsId;
    }

    public int getChestId() {
        return chestId;
    }

    public int getLegsId() {
        return legsId;
    }
}
