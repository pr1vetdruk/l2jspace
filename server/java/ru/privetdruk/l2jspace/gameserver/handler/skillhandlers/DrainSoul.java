package ru.privetdruk.l2jspace.gameserver.handler.skillhandlers;

import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillType;
import ru.privetdruk.l2jspace.gameserver.handler.ISkillHandler;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Monster;
import ru.privetdruk.l2jspace.gameserver.scripting.QuestState;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class DrainSoul implements ISkillHandler {
    private static final String qn = "Q350_EnhanceYourWeapon";

    private static final SkillType[] SKILL_IDS =
            {
                    SkillType.DRAIN_SOUL
            };

    @Override
    public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets) {
        // Check player.
        if (activeChar == null || activeChar.isDead() || !(activeChar instanceof Player))
            return;

        // Check quest condition.
        final Player player = (Player) activeChar;
        QuestState st = player.getQuestList().getQuestState(qn);
        if (st == null || !st.isStarted())
            return;

        // Get target.
        WorldObject target = targets[0];
        if (!(target instanceof Monster))
            return;

        // Check monster.
        final Monster mob = (Monster) target;
        if (mob.isDead())
            return;

        // Range condition, cannot be higher than skill's effectRange.
        if (!player.isIn3DRadius(mob, skill.getEffectRange()))
            return;

        // Register.
        mob.registerAbsorber(player);
    }

    @Override
    public SkillType[] getSkillIds() {
        return SKILL_IDS;
    }
}