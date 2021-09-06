package ru.privetdruk.l2jspace.gameserver.handler.skillhandlers;

import ru.privetdruk.l2jspace.gameserver.enums.items.ShotType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.ShieldDefense;
import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillType;
import ru.privetdruk.l2jspace.gameserver.handler.ISkillHandler;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.skill.Formula;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class CpDamPercent implements ISkillHandler {
    private static final SkillType[] SKILL_IDS =
            {
                    SkillType.CPDAMPERCENT
            };

    @Override
    public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets) {
        if (activeChar.isAlikeDead())
            return;

        final boolean bsps = activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOT);

        for (WorldObject obj : targets) {
            if (!(obj instanceof Player))
                continue;

            final Player target = ((Player) obj);
            if (target.isDead() || target.isInvul())
                continue;

            ShieldDefense shieldDefense = Formula.calcShieldUse(activeChar, target, skill, false);

            int damage = (int) (target.getStatus().getCp() * (skill.getPower() / 100));

            // Manage cast break of the target (calculating rate, sending message...)
            Formula.calcCastBreak(target, damage);

            skill.getEffects(activeChar, target, shieldDefense, bsps);
            activeChar.sendDamageMessage(target, damage, false, false, false);
            target.getStatus().setCp(target.getStatus().getCp() - damage);

            // Custom message to see Wrath damage on target
            target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_GAVE_YOU_S2_DMG).addCharName(activeChar).addNumber(damage));
        }
        activeChar.setChargedShot(ShotType.SOULSHOT, skill.isStaticReuse());
    }

    @Override
    public SkillType[] getSkillIds() {
        return SKILL_IDS;
    }
}