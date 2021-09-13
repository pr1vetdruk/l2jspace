package ru.privetdruk.l2jspace.gameserver.custom.model.event;

import ru.privetdruk.l2jspace.gameserver.data.sql.SpawnTable;
import ru.privetdruk.l2jspace.gameserver.data.xml.NpcData;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.model.location.SpawnLocation;
import ru.privetdruk.l2jspace.gameserver.model.spawn.Spawn;

public class EventNpc {
    private final NpcTemplate npcTemplate;
    private final String name;
    private final String title;
    private final SpawnLocation location;

    private Npc npc;

    public EventNpc(int npcId, String name, String title, SpawnLocation location) {
        this.npcTemplate = NpcData.getInstance().getTemplate(npcId);
        this.name = name == null ? "LastEmperor" : name;
        this.title = title == null ? "Event NPC" : title;
        this.location = location;
    }

    public Npc getNpc() {
        return npc;
    }

    public SpawnLocation getLocation() {
        return location;
    }

    public void spawn() {
        Spawn spawn = new Spawn(npcTemplate);

        spawn.setLoc(location);
        spawn.setRespawnDelay(1);
        SpawnTable.getInstance().addSpawn(spawn, false);
        spawn.doSpawn(true);

        Npc npc = spawn.getNpc();
        npc.setMortal(false);
        npc.decayMe();
        npc.setTitle(title);
        npc.setName(name);

        npc.spawnMe(npc.getX(), npc.getY(), npc.getZ());
    }

    public void unspawn() {
        SpawnTable.getInstance().deleteSpawn(npc.getSpawn(), true);
        npc.deleteMe();
    }
}
