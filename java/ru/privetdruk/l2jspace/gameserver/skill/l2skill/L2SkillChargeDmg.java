package ru.privetdruk.l2jspace.gameserver.skill.l2skill;

import ru.privetdruk.l2jspace.common.data.StatSet;

import ru.privetdruk.l2jspace.gameserver.enums.items.ShotType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.ShieldDefense;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.Formula;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class L2SkillChargeDmg extends L2Skill {
    public L2SkillChargeDmg(StatSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature caster, WorldObject[] targets) {
        if (caster.isAlikeDead())
            return;

        double modifier = 0;

        if (caster instanceof Player) {
            modifier = 0.8 + 0.2 * (((Player) caster).getCharges() + getNumCharges());
        }

        final boolean ss = caster.isChargedShot(ShotType.SOULSHOT);

        for (WorldObject obj : targets) {
            if (!(obj instanceof Creature))
                continue;

            final Creature target = ((Creature) obj);
            if (target.isAlikeDead())
                continue;

            // Calculate skill evasion
            boolean skillIsEvaded = Formula.calcPhysicalSkillEvasion(target, this);
            if (skillIsEvaded) {
                if (caster instanceof Player)
                    ((Player) caster).sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DODGES_ATTACK).addCharName(target));

                if (target instanceof Player)
                    ((Player) target).sendPacket(SystemMessage.getSystemMessage(SystemMessageId.AVOIDED_S1_ATTACK).addCharName(caster));

                continue;
            }

            boolean isCrit = getBaseCritRate() > 0 && Formula.calcCrit(getBaseCritRate() * 10 * Formula.getSTRBonus(caster));
            ShieldDefense shieldDefense = Formula.calcShieldUse(caster, target, this, isCrit);
            double damage = Formula.calcPhysicalSkillDamage(caster, target, this, shieldDefense, isCrit, ss);

            if (damage > 0) {
                byte reflect = Formula.calcSkillReflect(target, this);
                if (hasEffects()) {
                    if ((reflect & Formula.SKILL_REFLECT_SUCCEED) != 0) {
                        caster.stopSkillEffects(getId());
                        getEffects(target, caster);
                        caster.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(this));
                    } else {
                        // activate attacked effects, if any
                        target.stopSkillEffects(getId());
                        if (Formula.calcSkillSuccess(caster, target, this, shieldDefense, true)) {
                            getEffects(caster, target, shieldDefense, false);
                            target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(this));
                        } else
                            caster.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(this));
                    }
                }

                double finalDamage = damage * modifier;
                target.reduceCurrentHp(finalDamage, caster, this);

                // vengeance reflected damage
                if ((reflect & Formula.SKILL_REFLECT_VENGEANCE) != 0)
                    caster.reduceCurrentHp(damage, target, this);

                caster.sendDamageMessage(target, (int) finalDamage, false, isCrit, false);
            } else
                caster.sendDamageMessage(target, 0, false, false, true);
        }

        if (hasSelfEffects()) {
            final AbstractEffect effect = caster.getFirstEffect(getId());
            if (effect != null && effect.isSelfEffect())
                effect.exit();

            getEffectsSelf(caster);
        }

        caster.setChargedShot(ShotType.SOULSHOT, isStaticReuse());
    }
}