package ru.privetdruk.l2jspace.gameserver.handler.skillhandlers;

import java.util.List;

import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillType;
import ru.privetdruk.l2jspace.gameserver.handler.ISkillHandler;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Monster;
import ru.privetdruk.l2jspace.gameserver.model.holder.IntIntHolder;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class Sweep implements ISkillHandler {
    private static final SkillType[] SKILL_IDS =
            {
                    SkillType.SWEEP
            };

    @Override
    public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets) {
        if (!(activeChar instanceof Player)) {
            return;
        }

        Player player = (Player) activeChar;

        for (WorldObject target : targets) {
            if (!(target instanceof Monster)) {
                continue;
            }

            Monster monster = ((Monster) target);

            if (monster.getSpoilState().getSpoilerId() == player.getObjectId()) {
                List<IntIntHolder> items = monster.getSpoilState();
                if (items.isEmpty()) {
                    continue;
                }

                // Reward spoiler, based on sweep items retained on List.
                for (IntIntHolder item : items) {
                    if (player.isInParty()) {
                        player.getParty().distributeItem(player, item, true, monster);
                    } else {
                        player.addItem("Sweep", item.getId(), item.getValue(), player, true);
                    }
                }

                // Reset variables.
                monster.getSpoilState().clear();
            }

            monster.endDecayTask();
        }
    }

    @Override
    public SkillType[] getSkillIds() {
        return SKILL_IDS;
    }
}