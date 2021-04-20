package ru.privetdruk.l2jspace.gameserver.custom.model;

import ru.privetdruk.l2jspace.gameserver.model.location.SpawnLocation;

public class NpcInfoShort {
    private int id;
    private SpawnLocation spawnLocation;

    public NpcInfoShort(int id, SpawnLocation spawnLocation) {
        this.id = id;
        this.spawnLocation = spawnLocation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SpawnLocation getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(SpawnLocation spawnLocation) {
        this.spawnLocation = spawnLocation;
    }
}
