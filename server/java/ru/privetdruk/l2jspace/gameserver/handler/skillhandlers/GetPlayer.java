package ru.privetdruk.l2jspace.gameserver.handler.skillhandlers;

import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillType;
import ru.privetdruk.l2jspace.gameserver.handler.ISkillHandler;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class GetPlayer implements ISkillHandler {
    private static final SkillType[] SKILL_IDS =
            {
                    SkillType.GET_PLAYER
            };

    @Override
    public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets) {
        if (activeChar.isAlikeDead())
            return;

        for (WorldObject target : targets) {
            final Player victim = target.getActingPlayer();
            if (victim == null || victim.isAlikeDead())
                continue;

            victim.instantTeleportTo(activeChar.getPosition(), 0);
        }
    }

    @Override
    public SkillType[] getSkillIds() {
        return SKILL_IDS;
    }
}