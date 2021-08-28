package ru.privetdruk.l2jspace.gameserver.handler.skillhandlers;

import java.util.List;

import ru.privetdruk.l2jspace.common.util.ArraysUtil;

import ru.privetdruk.l2jspace.gameserver.enums.items.ShotType;
import ru.privetdruk.l2jspace.gameserver.enums.items.WeaponType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.ShieldDefense;
import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillType;
import ru.privetdruk.l2jspace.gameserver.handler.ISkillHandler;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.Formula;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;
import ru.privetdruk.l2jspace.gameserver.skill.effect.EffectFear;

public class Pdam implements ISkillHandler {
    private static final SkillType[] SKILL_IDS =
            {
                    SkillType.PDAM,
                    SkillType.FATAL
            };

    @Override
    public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets) {
        if (activeChar.isAlikeDead()) {
            return;
        }

        boolean ss = activeChar.isChargedShot(ShotType.SOULSHOT);

        ItemInstance weapon = activeChar.getActiveWeaponInstance();

        for (WorldObject obj : targets) {
            if (!(obj instanceof Creature)) {
                continue;
            }

            Creature target = ((Creature) obj);
            if (target.isDead()) {
                continue;
            }

            if (target instanceof Playable && ArraysUtil.contains(EffectFear.DOESNT_AFFECT_PLAYABLE, skill.getId())) {
                continue;
            }

            // Calculate skill evasion. As Dodge blocks only melee skills, make an exception with bow weapons.
            if (weapon != null && weapon.getItemType() != WeaponType.BOW && Formula.calcPhysicalSkillEvasion(target, skill)) {
                if (activeChar instanceof Player) {
                    activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DODGES_ATTACK).addCharName(target));
                }

                if (target instanceof Player) {
                    target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.AVOIDED_S1_ATTACK).addCharName(activeChar));
                }

                // no futher calculations needed.
                continue;
            }

            boolean isCrit = skill.getBaseCritRate() > 0 && Formula.calcCrit(skill.getBaseCritRate() * 10 * Formula.getSTRBonus(activeChar));
            ShieldDefense shieldDefense = Formula.calcShieldUse(activeChar, target, skill, isCrit);
            byte reflect = Formula.calcSkillReflect(target, skill);

            if (skill.hasEffects() && target.getFirstEffect(EffectType.BLOCK_DEBUFF) == null) {
                if ((reflect & Formula.SKILL_REFLECT_SUCCEED) != 0) {
                    activeChar.stopSkillEffects(skill.getId());
                    skill.getEffects(target, activeChar);
                } else {
                    // activate attacked effects, if any
                    target.stopSkillEffects(skill.getId());
                    skill.getEffects(activeChar, target, shieldDefense, false);
                }
            }

            int damage = (int) Formula.calcPhysicalSkillDamage(activeChar, target, skill, shieldDefense, isCrit, ss);
            if (damage > 0) {
                activeChar.sendDamageMessage(target, damage, false, isCrit, false);

                // Possibility of a lethal strike
                Formula.calcLethalHit(activeChar, target, skill);

                target.reduceCurrentHp(damage, activeChar, skill);

                // vengeance reflected damage
                if ((reflect & Formula.SKILL_REFLECT_VENGEANCE) != 0) {
                    if (target instanceof Player)
                        target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.COUNTERED_S1_ATTACK).addCharName(activeChar));

                    if (activeChar instanceof Player)
                        activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_PERFORMING_COUNTERATTACK).addCharName(target));

                    double vegdamage = (700 * target.getStatus().getPAtk(activeChar) / activeChar.getStatus().getPDef(target));
                    activeChar.reduceCurrentHp(vegdamage, target, skill);
                }
            } else
                activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ATTACK_FAILED));
        }

        if (skill.hasSelfEffects()) {
            final AbstractEffect effect = activeChar.getFirstEffect(skill.getId());
            if (effect != null && effect.isSelfEffect())
                effect.exit();

            skill.getEffectsSelf(activeChar);
        }

        if (skill.isSuicideAttack())
            activeChar.doDie(activeChar);

        activeChar.setChargedShot(ShotType.SOULSHOT, skill.isStaticReuse());
    }

    @Override
    public SkillType[] getSkillIds() {
        return SKILL_IDS;
    }
}