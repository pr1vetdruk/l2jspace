package ru.privetdruk.l2jspace.gameserver.scripting.script.ai.group;

import java.util.HashMap;
import java.util.Map;

import ru.privetdruk.l2jspace.gameserver.enums.ScriptEventType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.scripting.script.ai.AttackableAIScript;

/**
 * Angel spawns... When one of the angels in the keys dies, the other angel will spawn.
 */
public class PolymorphingAngel extends AttackableAIScript {
    private static final Map<Integer, Integer> ANGELSPAWNS = new HashMap<>(5);

    static {
        ANGELSPAWNS.put(20830, 20859);
        ANGELSPAWNS.put(21067, 21068);
        ANGELSPAWNS.put(21062, 21063);
        ANGELSPAWNS.put(20831, 20860);
        ANGELSPAWNS.put(21070, 21071);
    }

    public PolymorphingAngel() {
        super("ai/group");
    }

    @Override
    protected void registerNpcs() {
        addEventIds(ANGELSPAWNS.keySet(), ScriptEventType.ON_KILL);
    }

    @Override
    public String onKill(Npc npc, Creature killer) {
        final Npc angel = addSpawn(ANGELSPAWNS.get(npc.getNpcId()), npc, false, 0, false);
        angel.forceAttack(killer, 200);

        return super.onKill(npc, killer);
    }
}