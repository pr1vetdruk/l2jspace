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
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.Formula;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class Blow implements ISkillHandler {
    private static final SkillType[] SKILL_IDS =
            {
                    SkillType.BLOW
            };

    @Override
    public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets) {
        if (activeChar.isAlikeDead())
            return;

        final boolean ss = activeChar.isChargedShot(ShotType.SOULSHOT);

        for (WorldObject obj : targets) {
            if (!(obj instanceof Creature))
                continue;

            final Creature target = ((Creature) obj);
            if (target.isAlikeDead())
                continue;

            if (Formula.calcBlowRate(activeChar, target, skill)) {
                // Calculate skill evasion.
                if (Formula.calcPhysicalSkillEvasion(target, skill)) {
                    if (activeChar instanceof Player) {
                        activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DODGES_ATTACK).addCharName(target));
                    }

                    if (target instanceof Player) {
                        target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.AVOIDED_S1_ATTACK).addCharName(activeChar));
                    }

                    continue;
                }

                boolean isCrit = skill.getBaseCritRate() > 0 && Formula.calcCrit(skill.getBaseCritRate() * 10 * Formula.getSTRBonus(activeChar));
                ShieldDefense shieldDefense = Formula.calcShieldUse(activeChar, target, skill, isCrit);

                // Calculate skill reflect
                byte reflect = Formula.calcSkillReflect(target, skill);
                if (skill.hasEffects()) {
                    if (reflect == Formula.SKILL_REFLECT_SUCCEED) {
                        activeChar.stopSkillEffects(skill.getId());
                        skill.getEffects(target, activeChar);
                        activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(skill));
                    } else {
                        target.stopSkillEffects(skill.getId());
                        if (Formula.calcSkillSuccess(activeChar, target, skill, shieldDefense, true)) {
                            skill.getEffects(activeChar, target, shieldDefense, false);
                            target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(skill));
                        } else {
                            activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill));
                        }
                    }
                }

                double damage = (int) Formula.calcBlowDamage(activeChar, target, skill, shieldDefense, ss);
                if (isCrit) {
                    damage *= 2;
                }

                target.reduceCurrentHp(damage, activeChar, skill);

                // vengeance reflected damage
                if ((reflect & Formula.SKILL_REFLECT_VENGEANCE) != 0) {
                    if (target instanceof Player)
                        target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.COUNTERED_S1_ATTACK).addCharName(activeChar));

                    if (activeChar instanceof Player)
                        activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_PERFORMING_COUNTERATTACK).addCharName(target));

                    // Formula from Diego post, 700 from rpg tests
                    double vegdamage = (700 * target.getStatus().getPAtk(activeChar) / activeChar.getStatus().getPDef(target));
                    activeChar.reduceCurrentHp(vegdamage, target, skill);
                }

                // Manage cast break of the target (calculating rate, sending message...)
                Formula.calcCastBreak(target, damage);

                // Send damage message.
                activeChar.sendDamageMessage(target, (int) damage, false, true, false);

                activeChar.setChargedShot(ShotType.SOULSHOT, skill.isStaticReuse());
            }

            // Possibility of a lethal strike
            Formula.calcLethalHit(activeChar, target, skill);

            if (skill.hasSelfEffects()) {
                final AbstractEffect effect = activeChar.getFirstEffect(skill.getId());
                if (effect != null && effect.isSelfEffect())
                    effect.exit();

                skill.getEffectsSelf(activeChar);
            }
        }
    }

    @Override
    public SkillType[] getSkillIds() {
        return SKILL_IDS;
    }
}