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

public class HealPercent implements ISkillHandler {
    private static final SkillType[] SKILL_IDS =
            {
                    SkillType.HEAL_PERCENT,
                    SkillType.MANAHEAL_PERCENT
            };

    @Override
    public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets) {
        final ISkillHandler handler = SkillHandler.getInstance().getHandler(SkillType.BUFF);
        if (handler != null)
            handler.useSkill(activeChar, skill, targets);

        final boolean isHp = skill.getSkillType() == SkillType.HEAL_PERCENT;

        for (WorldObject obj : targets) {
            if (!(obj instanceof Creature))
                continue;

            final Creature target = ((Creature) obj);
            if (!target.canBeHealed())
                continue;

            double amount;
            if (isHp)
                amount = target.getStatus().addHp(target.getStatus().getMaxHp() * skill.getPower() / 100.);
            else
                amount = target.getStatus().addMp(target.getStatus().getMaxMp() * skill.getPower() / 100.);

            if (target instanceof Player) {
                SystemMessage sm;
                if (isHp) {
                    if (activeChar != target)
                        sm = SystemMessage.getSystemMessage(SystemMessageId.S2_HP_RESTORED_BY_S1).addCharName(activeChar);
                    else
                        sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HP_RESTORED);
                } else {
                    if (activeChar != target)
                        sm = SystemMessage.getSystemMessage(SystemMessageId.S2_MP_RESTORED_BY_S1).addCharName(activeChar);
                    else
                        sm = SystemMessage.getSystemMessage(SystemMessageId.S1_MP_RESTORED);
                }
                sm.addNumber((int) amount);
                target.sendPacket(sm);
            }
        }
    }

    @Override
    public SkillType[] getSkillIds() {
        return SKILL_IDS;
    }
}