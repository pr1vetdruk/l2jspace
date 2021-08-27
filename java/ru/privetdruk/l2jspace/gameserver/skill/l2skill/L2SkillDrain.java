package ru.privetdruk.l2jspace.gameserver.skill.l2skill;

import ru.privetdruk.l2jspace.common.data.StatSet;

import ru.privetdruk.l2jspace.gameserver.enums.items.ShotType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.ShieldDefense;
import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillTargetType;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.Formula;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class L2SkillDrain extends L2Skill {
    private final float _absorbPart;
    private final int _absorbAbs;

    public L2SkillDrain(StatSet set) {
        super(set);

        _absorbPart = set.getFloat("absorbPart", 0.f);
        _absorbAbs = set.getInteger("absorbAbs", 0);
    }

    @Override
    public void useSkill(Creature activeChar, WorldObject[] targets) {
        if (activeChar.isAlikeDead())
            return;

        final boolean sps = activeChar.isChargedShot(ShotType.SPIRITSHOT);
        final boolean bsps = activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOT);
        final boolean isPlayable = activeChar instanceof Playable;

        for (WorldObject obj : targets) {
            if (!(obj instanceof Creature))
                continue;

            final Creature target = ((Creature) obj);
            if (target.isAlikeDead() && getTargetType() != SkillTargetType.CORPSE_MOB)
                continue;

            if (activeChar != target && target.isInvul())
                continue; // No effect on invulnerable chars unless they cast it themselves.

            boolean isCrit = Formula.calcMCrit(activeChar, target, this);
            ShieldDefense shieldDefense = Formula.calcShieldUse(activeChar, target, this, false);
            int damage = (int) Formula.calcMagicDam(activeChar, target, this, shieldDefense, sps, bsps, isCrit);

            if (damage > 0) {
                int targetCp = 0;
                if (target instanceof Player)
                    targetCp = (int) ((Player) target).getStatus().getCp();

                final int targetHp = (int) target.getStatus().getHp();

                int drain = 0;
                if (isPlayable && targetCp > 0) {
                    if (damage < targetCp)
                        drain = 0;
                    else
                        drain = damage - targetCp;
                } else if (damage > targetHp)
                    drain = targetHp;
                else
                    drain = damage;

                activeChar.getStatus().addHp(_absorbAbs + _absorbPart * drain);

                // That section is launched for drain skills made on ALIVE targets.
                if (!target.isDead() || getTargetType() != SkillTargetType.CORPSE_MOB) {
                    // Manage cast break of the target (calculating rate, sending message...)
                    Formula.calcCastBreak(target, damage);

                    activeChar.sendDamageMessage(target, damage, isCrit, false, false);

                    if (hasEffects() && getTargetType() != SkillTargetType.CORPSE_MOB) {
                        // ignoring vengance-like reflections
                        if ((Formula.calcSkillReflect(target, this) & Formula.SKILL_REFLECT_SUCCEED) > 0) {
                            activeChar.stopSkillEffects(getId());
                            getEffects(target, activeChar);
                            activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(getId()));
                        } else {
                            // activate attacked effects, if any
                            target.stopSkillEffects(getId());
                            if (Formula.calcSkillSuccess(activeChar, target, this, shieldDefense, bsps))
                                getEffects(activeChar, target);
                            else
                                activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(getId()));
                        }
                    }
                    target.reduceCurrentHp(damage, activeChar, this);
                }
            }
        }

        if (hasSelfEffects()) {
            final AbstractEffect effect = activeChar.getFirstEffect(getId());
            if (effect != null && effect.isSelfEffect())
                effect.exit();

            getEffectsSelf(activeChar);
        }

        activeChar.setChargedShot(bsps ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, isStaticReuse());
    }

    public float getAbsorbPart() {
        return _absorbPart;
    }

    public int getAbsorbAbs() {
        return _absorbAbs;
    }
}