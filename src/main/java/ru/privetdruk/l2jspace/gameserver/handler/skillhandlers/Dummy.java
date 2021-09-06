package ru.privetdruk.l2jspace.gameserver.handler.skillhandlers;

import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillType;
import ru.privetdruk.l2jspace.gameserver.handler.ISkillHandler;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class Dummy implements ISkillHandler {
    private static final SkillType[] SKILL_IDS =
            {
                    SkillType.DUMMY,
                    SkillType.BEAST_FEED,
                    SkillType.DELUXE_KEY_UNLOCK
            };

    @Override
    public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets) {
        if (!(activeChar instanceof Player))
            return;

        if (skill.getSkillType() == SkillType.BEAST_FEED) {
            final WorldObject target = targets[0];
            if (target == null)
                return;
        }
    }

    @Override
    public SkillType[] getSkillIds() {
        return SKILL_IDS;
    }
}