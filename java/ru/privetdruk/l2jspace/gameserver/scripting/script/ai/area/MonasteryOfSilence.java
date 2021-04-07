package ru.privetdruk.l2jspace.gameserver.scripting.script.ai.area;

import ru.privetdruk.l2jspace.gameserver.enums.ScriptEventType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.NpcStringId;
import ru.privetdruk.l2jspace.gameserver.scripting.script.ai.AttackableAIScript;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

/**
 * This script holds MoS monsters behavior. If they see you with an equipped weapon, they will speak and attack you.
 */
public class MonasteryOfSilence extends AttackableAIScript {
    private static final int[] BROTHERS_SEEKERS_MONKS =
            {
                    22124,
                    22126,
                    22129
            };

    public MonasteryOfSilence() {
        super("ai/area");
    }

    @Override
    protected void registerNpcs() {
        addEventIds(BROTHERS_SEEKERS_MONKS, ScriptEventType.ON_AGGRO, ScriptEventType.ON_CREATURE_SEE, ScriptEventType.ON_SPELL_FINISHED);
    }

    @Override
    public String onAggro(Npc npc, Player player, boolean isPet) {
        if (!npc.isInCombat() && player.getActiveWeaponInstance() != null) {
            npc.getAI().tryToCast(player, 4589, 8);
            return super.onAggro(npc, player, isPet);
        }
        return null;
    }

    @Override
    public String onCreatureSee(Npc npc, Creature creature) {
        if (creature instanceof Player) {
            final Player player = creature.getActingPlayer();
            if (!npc.isInCombat() && player.getActiveWeaponInstance() != null)
                npc.getAI().tryToCast(player, 4589, 8);
        }
        return super.onCreatureSee(npc, creature);
    }

    @Override
    public String onSpellFinished(Npc npc, Player player, L2Skill skill) {
        if (skill.getId() == 4589 && npc.getScriptValue() == 0) {
            npc.broadcastNpcSay(NpcStringId.ID_1022122, player.getName());
            npc.forceAttack(player, 10000);
            npc.setScriptValue(1);
        }
        return super.onSpellFinished(npc, player, skill);
    }
}