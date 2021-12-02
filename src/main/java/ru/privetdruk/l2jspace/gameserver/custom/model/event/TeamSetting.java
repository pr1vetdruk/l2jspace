package ru.privetdruk.l2jspace.gameserver.custom.model.event;

import ru.privetdruk.l2jspace.gameserver.model.location.Location;

public class TeamSetting {
    protected int id;
    protected String name;
    protected int players;
    protected int points;
    protected String color;
    protected int offset;
    protected Location spawnLocation;

    public TeamSetting(int id, String name, String color, int offset, Location spawnLocation) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.offset = offset;
        this.spawnLocation = spawnLocation;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getPlayers() {
        return players;
    }

    public Integer getPoints() {
        return points;
    }

    public String getColor() {
        return color;
    }

    public int getOffset() {
        return offset;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void addPlayer() {
        players++;
    }

    public void removePlayer() {
        players--;
    }

    public void setPlayers(int players) {
        this.players = players;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void addPoint() {
        points++;
    }

    public void removePoint() {
        points--;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}

