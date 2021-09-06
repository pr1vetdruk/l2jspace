package ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf;

import ru.privetdruk.l2jspace.gameserver.custom.model.NpcInfoShort;
import ru.privetdruk.l2jspace.gameserver.model.spawn.Spawn;

public class Throne {
    private NpcInfoShort npc;
    private Spawn spawn;
    private int offsetZ;

    public Throne(NpcInfoShort npc, int offsetZ) {
        this.npc = npc;
        this.spawn = null;
        this.offsetZ = offsetZ;
    }

    public NpcInfoShort getNpc() {
        return npc;
    }

    public void setNpc(NpcInfoShort npc) {
        this.npc = npc;
    }

    public Spawn getSpawn() {
        return spawn;
    }

    public void setSpawn(Spawn spawn) {
        this.spawn = spawn;
    }

    public int getOffsetZ() {
        return offsetZ;
    }

    public void setOffsetZ(int offsetZ) {
        this.offsetZ = offsetZ;
    }
}
