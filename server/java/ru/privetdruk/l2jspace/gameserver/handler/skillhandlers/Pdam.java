package ru.privetdruk.l2jspace.gameserver.handler.skillhandlers;

import java.util.List;

import ru.privetdruk.l2jspace.common.util.ArraysUtil;

import ru.privetdruk.l2jspace.gameserver.enums.items.ShotType;
import ru.privetdruk.l2jspace.gameserver.enums.items.WeaponType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
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
import ru.privetdruk.l2jspace.gameserver.skill.Formulas;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;
import ru.privetdruk.l2jspace.gameserver.skill.effects.EffectFear;

public class Pdam implements ISkillHandler {
    private static final SkillType[] SKILL_IDS =
            {
                    SkillType.PDAM,
                    SkillType.FATAL
            };

    @Override
    public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets) {
        if (activeChar.isAlikeDead())
            return;

        final boolean ss = activeChar.isChargedShot(ShotType.SOULSHOT);

        final ItemInstance weapon = activeChar.getActiveWeaponInstance();

        for (WorldObject obj : targets) {
            if (!(obj instanceof Creature))
                continue;

            final Creature target = ((Creature) obj);
            if (target.isDead())
                continue;

            if (target instanceof Playable && ArraysUtil.contains(EffectFear.DOESNT_AFFECT_PLAYABLE, skill.getId()))
                continue;

            // Calculate skill evasion. As Dodge blocks only melee skills, make an exception with bow weapons.
            if (weapon != null && weapon.getItemType() != WeaponType.BOW && Formulas.calcPhysicalSkillEvasion(target, skill)) {
                if (activeChar instanceof Player)
                    activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DODGES_ATTACK).addCharName(target));

                if (target instanceof Player)
                    target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.AVOIDED_S1_ATTACK).addCharName(activeChar));

                // no futher calculations needed.
                continue;
            }

            final byte shld = Formulas.calcShldUse(activeChar, target, skill);
            final byte reflect = Formulas.calcSkillReflect(target, skill);

            int damage = (int) Formulas.calcPhysDam(activeChar, target, skill, shld, false, ss);

            // PDAM critical chance not affected by buffs, only by STR. Only some skills are meant to crit.
            final boolean crit = skill.getBaseCritRate() > 0 && Formulas.calcCrit(skill.getBaseCritRate() * 10 * Formulas.getSTRBonus(activeChar));
            if (crit)
                damage *= 2;

            if (skill.hasEffects() && target.getFirstEffect(EffectType.BLOCK_DEBUFF) == null) {
                List<AbstractEffect> effects;
                if ((reflect & Formulas.SKILL_REFLECT_SUCCEED) != 0) {
                    activeChar.stopSkillEffects(skill.getId());

                    effects = skill.getEffects(target, activeChar);
                    if (effects != null && !effects.isEmpty())
                        activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(skill));
                } else {
                    // activate attacked effects, if any
                    target.stopSkillEffects(skill.getId());

                    effects = skill.getEffects(activeChar, target, shld, false);
                    if (effects != null && !effects.isEmpty())
                        target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(skill));
                }
            }

            if (damage > 0) {
                activeChar.sendDamageMessage(target, damage, false, crit, false);

                // Possibility of a lethal strike
                Formulas.calcLethalHit(activeChar, target, skill);

                target.reduceCurrentHp(damage, activeChar, skill);

                // vengeance reflected damage
                if ((reflect & Formulas.SKILL_REFLECT_VENGEANCE) != 0) {
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