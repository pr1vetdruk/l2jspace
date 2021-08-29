package ru.privetdruk.l2jspace.gameserver.skill.l2skill;

import ru.privetdruk.l2jspace.common.data.StatSet;
import ru.privetdruk.l2jspace.gameserver.enums.items.ShotType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.ShieldDefense;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.Formula;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class L2SkillElemental extends L2Skill {
    private final int[] _seeds;
    private final boolean _seedAny;

    public L2SkillElemental(StatSet set) {
        super(set);

        _seeds = new int[3];
        _seeds[0] = set.getInteger("seed1", 0);
        _seeds[1] = set.getInteger("seed2", 0);
        _seeds[2] = set.getInteger("seed3", 0);

        if (set.getInteger("seed_any", 0) == 1)
            _seedAny = true;
        else
            _seedAny = false;
    }

    @Override
    public void useSkill(Creature activeChar, WorldObject[] targets) {
        if (activeChar.isAlikeDead())
            return;

        final boolean sps = activeChar.isChargedShot(ShotType.SPIRITSHOT);
        final boolean bsps = activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOT);

        for (WorldObject obj : targets) {
            if (!(obj instanceof Creature))
                continue;

            final Creature target = ((Creature) obj);
            if (target.isAlikeDead())
                continue;

            boolean charged = true;
            if (!_seedAny) {
                for (int _seed : _seeds) {
                    if (_seed != 0) {
                        final AbstractEffect effect = target.getFirstEffect(_seed);
                        if (effect == null || !effect.getInUse()) {
                            charged = false;
                            break;
                        }
                    }
                }
            } else {
                charged = false;
                for (int _seed : _seeds) {
                    if (_seed != 0) {
                        final AbstractEffect effect = target.getFirstEffect(_seed);
                        if (effect != null && effect.getInUse()) {
                            charged = true;
                            break;
                        }
                    }
                }
            }

            if (!charged) {
                activeChar.sendMessage("Target is not charged by elements.");
                continue;
            }

            boolean isCrit = Formula.calcMCrit(activeChar, target, this);
            ShieldDefense shieldDefense = Formula.calcShieldUse(activeChar, target, this, false);

            int damage = (int) Formula.calcMagicDam(activeChar, target, this, shieldDefense, sps, bsps, isCrit);
            if (damage > 0) {
                target.reduceCurrentHp(damage, activeChar, this);

                // Manage cast break of the target (calculating rate, sending message...)
                Formula.calcCastBreak(target, damage);

                activeChar.sendDamageMessage(target, damage, false, false, false);
            }

            // activate attacked effects, if any
            target.stopSkillEffects(getId());
            getEffects(activeChar, target, shieldDefense, bsps);
        }

        activeChar.setChargedShot(bsps ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, isStaticReuse());
    }
}