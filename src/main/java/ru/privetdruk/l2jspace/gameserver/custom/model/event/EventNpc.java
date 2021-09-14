package ru.privetdruk.l2jspace.gameserver.custom.model.event;

import ru.privetdruk.l2jspace.gameserver.data.sql.SpawnTable;
import ru.privetdruk.l2jspace.gameserver.data.xml.NpcData;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.model.location.SpawnLocation;
import ru.privetdruk.l2jspace.gameserver.model.spawn.Spawn;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SocialAction;

public class EventNpc {
    private final EventType event;
    private final NpcTemplate npcTemplate;
    private final String name;
    private final String title;
    private final SpawnLocation location;
    private final int roundVictorySkillAnimationId;
    private final int victorySkillAnimationId;
    private final int socialActionId;

    private Npc npc;

    public EventNpc(int victorySkillAnimationId,
                    int roundVictorySkillAnimationId,
                    int socialActionId,
                    EventType event,
                    int npcId,
                    String name,
                    String title,
                    SpawnLocation location) {
        this.event = event;
        this.npcTemplate = NpcData.getInstance().getTemplate(npcId);
        this.name = name == null ? "Event NPC" : name;
        this.title = title == null ? event.getName() : title;
        this.location = location;
        this.roundVictorySkillAnimationId = roundVictorySkillAnimationId;
        this.victorySkillAnimationId = victorySkillAnimationId;
        this.socialActionId = socialActionId;
    }

    public Npc getNpc() {
        return npc;
    }

    public SpawnLocation getLocation() {
        return location;
    }

    public void spawn() {
        npcTemplate.setUsingServerSideName(true);
        npcTemplate.setUsingServerSideTitle(true);

        Spawn spawn = new Spawn(npcTemplate);

        spawn.setLoc(location);
        spawn.setRespawnDelay(1);
        SpawnTable.getInstance().addSpawn(spawn, false);
        spawn.doSpawn(true);

        Npc spawnNpc = spawn.getNpc();
        spawnNpc.setMortal(false);
        spawnNpc.decayMe();
        spawnNpc.setTitle(title);
        spawnNpc.setName(name);

        spawnNpc.spawnMe(spawnNpc.getX(), spawnNpc.getY(), spawnNpc.getZ());

        npc = spawnNpc;
    }

    public void unspawn() {
        SpawnTable.getInstance().deleteSpawn(npc.getSpawn(), true);
        npc.deleteMe();
    }

    public void playVictoryAnimation() {
        playSkillAnimation(victorySkillAnimationId);
    }

    public void playRoundVictoryAnimation() {
        playSkillAnimation(roundVictorySkillAnimationId);
    }

    private void playSkillAnimation(int skillId) {
        if (skillId > 0) {
            npc.performSkillAnimation(skillId);
        }

        playSocialAnimation();
    }

    private void playSocialAnimation() {
        if (socialActionId > 0) {
            npc.broadcastPacket(new SocialAction(npc, socialActionId));
        }
    }
}
