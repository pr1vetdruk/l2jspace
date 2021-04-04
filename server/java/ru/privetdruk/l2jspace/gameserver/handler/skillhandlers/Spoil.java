package ru.privetdruk.l2jspace.gameserver.handler.skillhandlers;

import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillType;
import ru.privetdruk.l2jspace.gameserver.handler.ISkillHandler;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Monster;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.skill.Formulas;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class Spoil implements ISkillHandler {
    private static final SkillType[] SKILL_IDS =
            {
                    SkillType.SPOIL
            };

    @Override
    public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets) {
        if (!(activeChar instanceof Player))
            return;

        if (targets == null)
            return;

        for (final WorldObject tgt : targets) {
            if (!(tgt instanceof Monster))
                continue;

            final Monster target = (Monster) tgt;
            if (target.isDead())
                continue;

            if (target.getSpoilState().isSpoiled()) {
                activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ALREADY_SPOILED));
                continue;
            }

            if (Formulas.calcMagicSuccess(activeChar, (Creature) tgt, skill)) {
                target.getSpoilState().setSpoilerId(activeChar.getObjectId());
                activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SPOIL_SUCCESS));
            } else
                activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill.getId()));
        }
    }

    @Override
    public SkillType[] getSkillIds() {
        return SKILL_IDS;
    }
}