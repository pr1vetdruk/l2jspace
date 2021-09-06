package ru.privetdruk.l2jspace.gameserver.handler.skillhandlers;

import ru.privetdruk.l2jspace.common.math.MathUtil;
import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillType;
import ru.privetdruk.l2jspace.gameserver.handler.ISkillHandler;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ValidateLocation;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class InstantJump implements ISkillHandler {
    private static final SkillType[] SKILL_IDS =
            {
                    SkillType.INSTANT_JUMP
            };

    @Override
    public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets) {
        Creature target = (Creature) targets[0];

        int px = target.getX();
        int py = target.getY();
        double ph = MathUtil.convertHeadingToDegree(target.getHeading());

        ph += 180;

        if (ph > 360)
            ph -= 360;

        ph = (Math.PI * ph) / 180;

        int x = (int) (px + (25 * Math.cos(ph)));
        int y = (int) (py + (25 * Math.sin(ph)));
        int z = target.getZ();

        // Abort attack, cast and move.
        activeChar.abortAll(false);

        // Teleport the actor.
        activeChar.setXYZ(x, y, z);
        activeChar.broadcastPacket(new ValidateLocation(activeChar));
    }

    @Override
    public SkillType[] getSkillIds() {
        return SKILL_IDS;
    }
}