package ru.privetdruk.l2jspace.gameserver.custom.model.event;

import ru.privetdruk.l2jspace.gameserver.model.location.SpawnLocation;

public class TeamSetting {
    protected String name;
    protected int players;
    protected int points;
    protected int color;
    protected int offset;
    protected SpawnLocation spawnLocation;

    public TeamSetting(String name, Integer color, int offset, SpawnLocation spawnLocation) {
        this.name = name;
        this.color = color;
        this.offset = offset;
        this.spawnLocation = spawnLocation;
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

    public Integer getColor() {
        return color;
    }

    public int getOffset() {
        return offset;
    }

    public SpawnLocation getSpawnLocation() {
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

