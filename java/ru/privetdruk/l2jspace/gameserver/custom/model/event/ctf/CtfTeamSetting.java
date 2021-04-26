package ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf;

import ru.privetdruk.l2jspace.gameserver.custom.model.event.TeamSetting;
import ru.privetdruk.l2jspace.gameserver.model.location.SpawnLocation;

public class CtfTeamSetting extends TeamSetting {
    protected int id;
    protected Flag flag;
    protected Throne throne;

    public CtfTeamSetting() {
        super("name", 1, 1, null);
    }

    public CtfTeamSetting(int id, String name, Integer playersCount, int offset, SpawnLocation spawnLocation, Flag flag, Throne throne) {
        super(name, playersCount, offset, spawnLocation);

        this.id = id;
        this.flag = flag;
        this.throne = throne;
    }

    public int getId() {
        return id;
    }

    public Flag getFlag() {
        return flag;
    }

    public void setFlag(Flag flag) {
        this.flag = flag;
    }

    public Throne getThrone() {
        return throne;
    }

    public void setThrone(Throne throne) {
        this.throne = throne;
    }
}
