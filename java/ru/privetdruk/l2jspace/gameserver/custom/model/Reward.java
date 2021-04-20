package ru.privetdruk.l2jspace.gameserver.custom.model;

public class Reward {
    private final int id;
    private final int amount;

    public Reward(int id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }
}
