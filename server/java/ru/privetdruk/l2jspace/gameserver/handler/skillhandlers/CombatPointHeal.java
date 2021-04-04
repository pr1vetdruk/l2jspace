package ru.privetdruk.l2jspace.gameserver.handler.skillhandlers;

import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillType;
import ru.privetdruk.l2jspace.gameserver.handler.ISkillHandler;
import ru.privetdruk.l2jspace.gameserver.handler.SkillHandler;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class CombatPointHeal implements ISkillHandler {
    private static final SkillType[] SKILL_IDS =
            {
                    SkillType.COMBATPOINTHEAL
            };

    @Override
    public void useSkill(Creature actChar, L2Skill skill, WorldObject[] targets) {
        final ISkillHandler handler = SkillHandler.getInstance().getHandler(SkillType.BUFF);
        if (handler != null)
            handler.useSkill(actChar, skill, targets);

        for (WorldObject obj : targets) {
            if (!(obj instanceof Player))
                continue;

            final Player target = (Player) obj;
            if (target.isDead() || target.isInvul())
                continue;

            double cp = skill.getPower();

            if ((target.getStatus().getCp() + cp) >= target.getStatus().getMaxCp())
                cp = target.getStatus().getMaxCp() - target.getStatus().getCp();

            target.getStatus().setCp(cp + target.getStatus().getCp());

            if (actChar instanceof Player && actChar != target)
                target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S2_CP_WILL_BE_RESTORED_BY_S1).addCharName(actChar).addNumber((int) cp));
            else
                target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CP_WILL_BE_RESTORED).addNumber((int) cp));
        }
    }

    @Override
    public SkillType[] getSkillIds() {
        return SKILL_IDS;
    }
}