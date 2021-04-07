package ru.privetdruk.l2jspace.gameserver.skill.l2skill;

import ru.privetdruk.l2jspace.common.data.StatSet;

import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.spawn.Spawn;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class L2SkillSpawn extends L2Skill {
    private final int _npcId;
    private final int _despawnDelay;

    public L2SkillSpawn(StatSet set) {
        super(set);

        _npcId = set.getInteger("npcId", 0);
        _despawnDelay = set.getInteger("despawnDelay", 0);
    }

    @Override
    public void useSkill(Creature caster, WorldObject[] targets) {
        if (caster.isAlikeDead())
            return;

        try {
            // Create spawn.
            final Spawn spawn = new Spawn(_npcId);
            spawn.setRespawnState(false);
            spawn.setLoc(caster.getPosition());

            // Spawn NPC.
            final Npc npc = spawn.doSpawn(false);
            if (_despawnDelay > 0)
                npc.scheduleDespawn(_despawnDelay);
        } catch (Exception e) {
            LOGGER.error("Failed to initialize a spawn.", e);
        }
    }
}