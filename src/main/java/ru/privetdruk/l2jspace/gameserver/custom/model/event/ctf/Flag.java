package ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf;

import ru.privetdruk.l2jspace.gameserver.custom.model.NpcInfoShort;
import ru.privetdruk.l2jspace.gameserver.model.spawn.Spawn;

public class Flag {
    private NpcInfoShort npc;
    private int itemId;
    private Spawn spawn;
    private Boolean taken;

    public Flag(NpcInfoShort npc, int itemId, Boolean taken) {
        this.npc = npc;
        this.itemId = itemId;
        this.spawn = null;
        this.taken = taken;
    }

    public NpcInfoShort getNpc() {
        return npc;
    }

    public void setNpc(NpcInfoShort npc) {
        this.npc = npc;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public Spawn getSpawn() {
        return spawn;
    }

    public void setSpawn(Spawn spawn) {
        this.spawn = spawn;
    }

    public Boolean isTaken() {
        return taken;
    }

    public void setTaken(Boolean taken) {
        this.taken = taken;
    }
}
