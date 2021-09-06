package ru.privetdruk.l2jspace.gameserver.handler.skillhandlers;

import ru.privetdruk.l2jspace.common.random.Rnd;
import ru.privetdruk.l2jspace.gameserver.enums.AiEventType;
import ru.privetdruk.l2jspace.gameserver.enums.ZoneId;
import ru.privetdruk.l2jspace.gameserver.enums.items.ShotType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.*;
import ru.privetdruk.l2jspace.gameserver.handler.ISkillHandler;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Attackable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.Summon;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.SiegeSummon;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.Formula;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class Disablers implements ISkillHandler {
    private static final SkillType[] SKILL_IDS =
            {
                    SkillType.STUN,
                    SkillType.ROOT,
                    SkillType.SLEEP,
                    SkillType.CONFUSION,
                    SkillType.AGGDAMAGE,
                    SkillType.AGGREDUCE,
                    SkillType.AGGREDUCE_CHAR,
                    SkillType.AGGREMOVE,
                    SkillType.MUTE,
                    SkillType.FAKE_DEATH,
                    SkillType.NEGATE,
                    SkillType.CANCEL_DEBUFF,
                    SkillType.PARALYZE,
                    SkillType.ERASE,
                    SkillType.BETRAY
            };

    @Override
    public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets) {
        SkillType type = skill.getSkillType();

        boolean bsps = activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOT);

        for (WorldObject obj : targets) {
            if (!(obj instanceof Creature)) {
                continue;
            }

            Creature target = (Creature) obj;
            // bypass if target is dead or invul (excluding invul from Petrification)
            if (target.isDead() || (target.isInvul() && !target.isParalyzed())) {
                continue;
            }

            if (skill.isOffensive() && target.getFirstEffect(EffectType.BLOCK_DEBUFF) != null) {
                continue;
            }

            ShieldDefense shieldDefense = Formula.calcShieldUse(activeChar, target, skill, false);

            switch (type) {
                case BETRAY:
                    if (Formula.calcSkillSuccess(activeChar, target, skill, shieldDefense, bsps)) {
                        skill.getEffects(activeChar, target, shieldDefense, bsps);
                    } else {
                        activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill));
                    }

                    break;
                case FAKE_DEATH:
                    // stun/fakedeath is not mdef dependant, it depends on lvl difference, target CON and power of stun
                    skill.getEffects(activeChar, target, shieldDefense, bsps);
                    break;
                case SLEEP, STUN, PARALYZE, ROOT:
                    if (Formula.calcSkillReflect(target, skill) == Formula.SKILL_REFLECT_SUCCEED) {
                        target = activeChar;
                    }

                    if (Formula.calcSkillSuccess(activeChar, target, skill, shieldDefense, bsps)) {
                        skill.getEffects(activeChar, target, shieldDefense, bsps);
                    } else if (activeChar instanceof Player) {
                        activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill.getId()));
                    }

                    break;
                case MUTE:
                    if (Formula.calcSkillReflect(target, skill) == Formula.SKILL_REFLECT_SUCCEED) {
                        target = activeChar;
                    }

                    if (Formula.calcSkillSuccess(activeChar, target, skill, shieldDefense, bsps)) {
                        // stop same type effect if available
                        for (AbstractEffect effect : target.getAllEffects()) {
                            if (effect.getTemplate().getStackOrder() == 99) {
                                continue;
                            }

                            if (effect.getSkill().getSkillType() == type) {
                                effect.exit();
                            }
                        }

                        skill.getEffects(activeChar, target, shieldDefense, bsps);
                    } else if (activeChar instanceof Player) {
                        activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill.getId()));
                    }

                    break;
                case CONFUSION:
                    // do nothing if not on mob
                    if (target instanceof Attackable) {
                        if (Formula.calcSkillSuccess(activeChar, target, skill, shieldDefense, bsps)) {
                            for (AbstractEffect effect : target.getAllEffects()) {
                                if (effect.getTemplate().getStackOrder() == 99) {
                                    continue;
                                }

                                if (effect.getSkill().getSkillType() == type) {
                                    effect.exit();
                                }
                            }

                            skill.getEffects(activeChar, target, shieldDefense, bsps);
                        } else if (activeChar instanceof Player) {
                            activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill));
                        }
                    } else {
                        activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.INVALID_TARGET));
                    }

                    break;
                case AGGDAMAGE:
                    if ((target instanceof Player) && (Rnd.get(100) < 75)) {
                        Player player = ((Player) target);
                        if (player.getPvpFlag() != 0 || player.isInOlympiadMode() || player.isInCombat() || player.isInsideZone(ZoneId.PVP)) {
                            player.setTarget(activeChar);
                            player.getAttack().stop();
                            player.getAI().tryToAttack(activeChar);
                        }
                    }

                    if (target instanceof Attackable) {
                        target.getAI().notifyEvent(AiEventType.AGGRESSION, activeChar, (int) (skill.getPower() / (target.getStatus().getLevel() + 7) * 150));
                    }

                    skill.getEffects(activeChar, target, shieldDefense, bsps);
                    break;
                case AGGREDUCE:
                    // TODO these skills needs to be rechecked
                    if (target instanceof Attackable) {
                        skill.getEffects(activeChar, target, shieldDefense, bsps);

                        if (skill.getPower() > 0) {
                            ((Attackable) target).getAggroList().reduceAllHate((int) skill.getPower());
                        } else {
                            int hate = ((Attackable) target).getAggroList().getHate(activeChar);
                            double diff = hate - target.getStatus().calcStat(Stats.AGGRESSION, hate, target, skill);

                            if (diff > 0) {
                                ((Attackable) target).getAggroList().reduceAllHate((int) diff);
                            }
                        }
                    }
                    break;
                case AGGREDUCE_CHAR:
                    // TODO these skills need to be rechecked
                    if (Formula.calcSkillSuccess(activeChar, target, skill, shieldDefense, bsps)) {
                        if (target instanceof Attackable) {
                            ((Attackable) target).getAggroList().stopHate(activeChar);
                        }

                        skill.getEffects(activeChar, target, shieldDefense, bsps);
                    } else {
                        if (activeChar instanceof Player) {
                            activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill));
                        }
                    }

                    break;
                case AGGREMOVE:
                    // TODO these skills needs to be rechecked
                    if (target instanceof Attackable && !target.isRaidRelated()) {
                        if (Formula.calcSkillSuccess(activeChar, target, skill, shieldDefense, bsps)) {
                            if (skill.getTargetType() == SkillTargetType.UNDEAD) {
                                if (target.isUndead()) {
                                    ((Attackable) target).getAggroList().stopHate(activeChar);
                                }
                            } else {
                                ((Attackable) target).getAggroList().stopHate(activeChar);
                            }
                        } else {
                            if (activeChar instanceof Player) {
                                activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill));
                            }
                        }
                    }

                    break;
                case ERASE:
                    // doesn't affect siege summons
                    if (Formula.calcSkillSuccess(activeChar, target, skill, shieldDefense, bsps) && !(target instanceof SiegeSummon)) {
                        Player summonOwner = ((Summon) target).getOwner();
                        Summon summonPet = summonOwner.getSummon();

                        if (summonPet != null) {
                            summonPet.unSummon(summonOwner);
                            summonOwner.sendPacket(SystemMessageId.YOUR_SERVITOR_HAS_VANISHED);
                        }
                    } else {
                        if (activeChar instanceof Player) {
                            activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill));
                        }
                    }

                    break;
                case CANCEL_DEBUFF:
                    AbstractEffect[] effects = target.getAllEffects();
                    if (effects == null || effects.length == 0) {
                        break;
                    }

                    int count = (skill.getMaxNegatedEffects() > 0) ? 0 : -2;
                    for (AbstractEffect effect : effects) {
                        if (!effect.getSkill().isDebuff() || !effect.getSkill().canBeDispeled() || effect.getTemplate().getStackOrder() == 99) {
                            continue;
                        }

                        effect.exit();

                        if (count > -1) {
                            count++;
                            if (count >= skill.getMaxNegatedEffects()) {
                                break;
                            }
                        }
                    }

                    break;
                case NEGATE:
                    if (Formula.calcSkillReflect(target, skill) == Formula.SKILL_REFLECT_SUCCEED) {
                        target = activeChar;
                    }

                    // Skills with negateId (skillId)
                    if (skill.getNegateId().length != 0) {
                        for (int id : skill.getNegateId()) {
                            if (id != 0) {
                                target.stopSkillEffects(id);
                            }
                        }
                    } else { // All others negate type skills
                        for (AbstractEffect effect : target.getAllEffects()) {
                            if (effect.getTemplate().getStackOrder() == 99) {
                                continue;
                            }

                            L2Skill effectSkill = effect.getSkill();
                            for (SkillType skillType : skill.getNegateStats()) {
                                // If power is -1 the effect is always removed without lvl check
                                if (skill.getNegateLvl() == -1) {
                                    if (effectSkill.getSkillType() == skillType || (effectSkill.getEffectType() != null && effectSkill.getEffectType() == skillType)) {
                                        effect.exit();
                                    }
                                } else { // Remove the effect according to its power.
                                    if (effectSkill.getEffectType() != null && effectSkill.getEffectAbnormalLvl() >= 0) {
                                        if (effectSkill.getEffectType() == skillType && effectSkill.getEffectAbnormalLvl() <= skill.getNegateLvl()) {
                                            effect.exit();
                                        }
                                    } else if (effectSkill.getSkillType() == skillType && effectSkill.getAbnormalLvl() <= skill.getNegateLvl()) {
                                        effect.exit();
                                    }
                                }
                            }
                        }
                    }

                    skill.getEffects(activeChar, target, shieldDefense, bsps);

                    break;
            }
        }

        if (skill.hasSelfEffects()) {
            AbstractEffect effect = activeChar.getFirstEffect(skill.getId());
            if (effect != null && effect.isSelfEffect()) {
                effect.exit();
            }

            skill.getEffectsSelf(activeChar);
        }

        activeChar.setChargedShot(bsps ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, skill.isStaticReuse());
    }

    @Override
    public SkillType[] getSkillIds() {
        return SKILL_IDS;
    }
}