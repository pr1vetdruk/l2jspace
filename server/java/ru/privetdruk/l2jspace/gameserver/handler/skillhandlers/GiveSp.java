package ru.privetdruk.l2jspace.gameserver.handler.skillhandlers;

import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillType;
import ru.privetdruk.l2jspace.gameserver.handler.ISkillHandler;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class GiveSp implements ISkillHandler {
    private static final SkillType[] SKILL_IDS =
            {
                    SkillType.GIVE_SP
            };

    @Override
    public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets) {
        final int spToAdd = (int) skill.getPower();

        for (WorldObject obj : targets) {
            final Creature target = (Creature) obj;
            if (target != null)
                target.addExpAndSp(0, spToAdd);
        }
    }

    @Override
    public SkillType[] getSkillIds() {
        return SKILL_IDS;
    }
}