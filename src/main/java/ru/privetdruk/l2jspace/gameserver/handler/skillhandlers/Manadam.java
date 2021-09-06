package ru.privetdruk.l2jspace.gameserver.handler.skillhandlers;

import ru.privetdruk.l2jspace.gameserver.enums.items.ShotType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
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

public class Manadam implements ISkillHandler {
    private static final SkillType[] SKILL_IDS =
            {
                    SkillType.MANADAM
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
            if (Formula.calcSkillReflect(target, skill) == Formula.SKILL_REFLECT_SUCCEED) {
                target = activeChar;
            }

            boolean acted = Formula.calcMagicAffected(activeChar, target, skill);
            if (target.isInvul() || !acted) {
                activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.MISSED_TARGET));
            }
            else {
                if (skill.hasEffects()) {
                    target.stopSkillEffects(skill.getId());

                    ShieldDefense shieldDefense = Formula.calcShieldUse(activeChar, target, skill, false);
                    if (Formula.calcSkillSuccess(activeChar, target, skill, shieldDefense, bsps)) {
                        skill.getEffects(activeChar, target, shieldDefense, bsps);
                    } else {
                        activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill));
                    }
                }

                double damage = Formula.calcManaDam(activeChar, target, skill, sps, bsps);

                double mp = (Math.min(damage, target.getStatus().getMp()));
                target.getStatus().reduceMp(mp);
                if (damage > 0) {
                    target.stopEffects(EffectType.SLEEP);
                    target.stopEffects(EffectType.IMMOBILE_UNTIL_ATTACKED);
                }

                if (target instanceof Player) {
                    target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S2_MP_HAS_BEEN_DRAINED_BY_S1).addCharName(activeChar).addNumber((int) mp));
                }

                if (activeChar instanceof Player) {
                    activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_OPPONENTS_MP_WAS_REDUCED_BY_S1).addNumber((int) mp));
                }
            }
        }

        if (skill.hasSelfEffects()) {
            final AbstractEffect effect = activeChar.getFirstEffect(skill.getId());
            if (effect != null && effect.isSelfEffect())
                effect.exit();

            skill.getEffectsSelf(activeChar);
        }
        activeChar.setChargedShot(bsps ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, skill.isStaticReuse());
    }

    @Override
    public SkillType[] getSkillIds() {
        return SKILL_IDS;
    }
}