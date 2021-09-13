package ru.privetdruk.l2jspace.gameserver.custom.model;

public class Reward {
    private int id;
    private int amount;

    public Reward() {
    }

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

    public void setId(int id) {
        this.id = id;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
