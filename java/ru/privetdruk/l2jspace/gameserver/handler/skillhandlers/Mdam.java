package ru.privetdruk.l2jspace.gameserver.handler.skillhandlers;

import ru.privetdruk.l2jspace.gameserver.enums.items.ShotType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.ShieldDefense;
import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillType;
import ru.privetdruk.l2jspace.gameserver.handler.ISkillHandler;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.Formula;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class Mdam implements ISkillHandler {
    private static final SkillType[] SKILL_IDS =
            {
                    SkillType.MDAM,
                    SkillType.DEATHLINK
            };

    @Override
    public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets) {
        if (activeChar.isAlikeDead()) {
            return;
        }

        boolean sps = activeChar.isChargedShot(ShotType.SPIRITSHOT);
        boolean bsps = activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOT);

        for (WorldObject obj : targets) {
            if (!(obj instanceof Creature)) {
                continue;
            }

            Creature target = ((Creature) obj);
            if (target.isDead()) {
                continue;
            }

            boolean isCrit = Formula.calcMCrit(activeChar, target, skill);
            ShieldDefense sDef = Formula.calcShieldUse(activeChar, target, skill, false);
            final byte reflect = Formula.calcSkillReflect(target, skill);

            int damage = (int) Formula.calcMagicDam(activeChar, target, skill, sDef, sps, bsps, isCrit);
            if (damage > 0) {
                // Manage cast break of the target (calculating rate, sending message...)
                Formula.calcCastBreak(target, damage);

                // vengeance reflected damage
                if ((reflect & Formula.SKILL_REFLECT_VENGEANCE) != 0) {
                    activeChar.reduceCurrentHp(damage, target, skill);
                } else {
                    activeChar.sendDamageMessage(target, damage, isCrit, false, false);
                    target.reduceCurrentHp(damage, activeChar, skill);
                }

                if (skill.hasEffects() && target.getFirstEffect(EffectType.BLOCK_DEBUFF) == null) {
                    if ((reflect & Formula.SKILL_REFLECT_SUCCEED) != 0) { // reflect skill effects

                        activeChar.stopSkillEffects(skill.getId());
                        skill.getEffects(target, activeChar);
                        activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(skill));
                    } else {
                        // activate attacked effects, if any
                        target.stopSkillEffects(skill.getId());
                        if (Formula.calcSkillSuccess(activeChar, target, skill, sDef, bsps)) {
                            skill.getEffects(activeChar, target, sDef, bsps);
                        } else {
                            activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill.getId()));
                        }
                    }
                }
            }
        }

        if (skill.hasSelfEffects()) {
            final AbstractEffect effect = activeChar.getFirstEffect(skill.getId());
            if (effect != null && effect.isSelfEffect())
                effect.exit();

            skill.getEffectsSelf(activeChar);
        }

        if (skill.isSuicideAttack())
            activeChar.doDie(activeChar);

        activeChar.setChargedShot(bsps ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, skill.isStaticReuse());
    }

    @Override
    public SkillType[] getSkillIds() {
        return SKILL_IDS;
    }
}