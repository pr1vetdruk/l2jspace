package ru.privetdruk.l2jspace.gameserver.handler.targethandlers;

import java.util.ArrayList;
import java.util.List;

import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillTargetType;
import ru.privetdruk.l2jspace.gameserver.geoengine.GeoEngine;
import ru.privetdruk.l2jspace.gameserver.handler.ITargetHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.*;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class TargetArea implements ITargetHandler {
    @Override
    public SkillTargetType getTargetType() {
        return SkillTargetType.AREA;
    }

    @Override
    public Creature[] getTargetList(Creature caster, Creature target, L2Skill skill) {
        List<Creature> list = configureTarget(caster, target, skill);

        if (list.isEmpty())
            return EMPTY_TARGET_ARRAY;

        return list.toArray(new Creature[0]);
    }

    @Override
    public Creature getFinalTarget(Creature caster, Creature target, L2Skill skill) {
        if (target == null || target == caster || target.isDead())
            return null;

        return target;
    }

    @Override
    public boolean meetCastConditions(Playable caster, Creature target, L2Skill skill, boolean isCtrlPressed) {
        if (skill.isOffensive()) {
            if (!target.isAttackableBy(caster) || (!isCtrlPressed && !target.isAttackableWithoutForceBy(caster.getActingPlayer()))) {
                caster.sendPacket(SystemMessageId.INVALID_TARGET);
                return false;
            }
        }
        return true;
    }

    public static List<Creature> configureTarget(Creature caster, Creature target, L2Skill skill) {
        List<Creature> list = new ArrayList<>();
        list.add(target);

        boolean casterIsPlayable = caster instanceof Playable;
        boolean casterIsAttackable = caster instanceof Attackable;

        Player casterPlayer = null;
        if (caster instanceof Player) {
            casterPlayer = (Player) caster;
        } else if (caster instanceof Summon) {
            casterPlayer = ((Summon) caster).getOwner();
        }

        for (Creature creatureInRadius : target.getKnownTypeInRadius(Creature.class, skill.getSkillRadius())) {
            if (creatureInRadius == caster
                    || creatureInRadius.isDead()
                    || !GeoEngine.getInstance().canSeeTarget(target, creatureInRadius)) {
                continue;
            }

            if (casterIsPlayable && (creatureInRadius instanceof Attackable || creatureInRadius instanceof Playable)) {
                if (casterPlayer != null) {
                    Player playerInRadius = null;

                    if (creatureInRadius instanceof Player) {
                        playerInRadius = (Player) creatureInRadius;
                    } else if (creatureInRadius instanceof Summon) {
                        playerInRadius = ((Summon) creatureInRadius).getOwner();
                    }

                    if (playerInRadius != null &&
                            ((playerInRadius.isEventPlayer() && !casterPlayer.isEventPlayer())
                                    || (!playerInRadius.isEventPlayer() && casterPlayer.isEventPlayer()))) {
                        continue;
                    }
                }

                if (creatureInRadius.isAttackableWithoutForceBy((Playable) caster)) {
                    list.add(creatureInRadius);
                }
            } else if (casterIsAttackable && creatureInRadius instanceof Playable) {
                if (creatureInRadius.isAttackableBy(caster)) {
                    list.add(creatureInRadius);
                }
            }
        }

        return list;
    }
}