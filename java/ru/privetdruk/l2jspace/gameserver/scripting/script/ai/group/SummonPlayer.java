package ru.privetdruk.l2jspace.gameserver.scripting.script.ai.group;

import ru.privetdruk.l2jspace.common.random.Rnd;
import ru.privetdruk.l2jspace.gameserver.enums.actors.NpcSkillType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Attackable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.scripting.script.ai.AttackableAIScript;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

/**
 * This script holds Portas/Perums monsters behavior. They got a luck to teleport you near them if they are under attack.
 */
public class SummonPlayer extends AttackableAIScript {
    private static final int PORTA = 20213;
    private static final int PERUM = 20221;

    public SummonPlayer() {
        super("ai/group");
    }

    @Override
    protected void registerNpcs() {
        addAttackId(PORTA, PERUM);
        addSpellFinishedId(PORTA, PERUM);
    }

    @Override
    public String onAttack(Npc npc, Creature attacker, int damage, L2Skill skill) {
        if (!(attacker instanceof Player))
            return null;

        if (npc.getScriptValue() == 0) {
            final double distance = npc.distance3D(attacker);
            if (distance > 300) {
                if (Rnd.nextBoolean()) {
                    npc.getAI().tryToCast(attacker, Rnd.get(npc.getTemplate().getSkills(NpcSkillType.TELEPORT)));
                    npc.setScriptValue(1);
                }
            } else if (distance > 100) {
                final int chance = Rnd.get(100);

                if ((((Attackable) npc).getAggroList().getMostHatedCreature() == attacker && chance < 50) || chance < 10) {
                    npc.getAI().tryToCast(attacker, Rnd.get(npc.getTemplate().getSkills(NpcSkillType.TELEPORT)));
                    npc.setScriptValue(1);
                }
            }
        }
        return super.onAttack(npc, attacker, damage, skill);
    }

    @Override
    public String onSpellFinished(Npc npc, Player player, L2Skill skill) {
        if (skill.getId() == 4161) {
            npc.setScriptValue(0);

            if (((Attackable) npc).getAggroList().getMostHatedCreature() == player && Rnd.get(100) < 33)
                npc.getAI().tryToCast(player, Rnd.get(npc.getTemplate().getSkills(NpcSkillType.SHORT_RANGE)));
        }
        return super.onSpellFinished(npc, player, skill);
    }
}