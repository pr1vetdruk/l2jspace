package ru.privetdruk.l2jspace.gameserver.handler.targethandlers;

import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillTargetType;
import ru.privetdruk.l2jspace.gameserver.geoengine.GeoEngine;
import ru.privetdruk.l2jspace.gameserver.handler.ITargetHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Attackable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

import java.util.ArrayList;
import java.util.List;

public class TargetFrontArea implements ITargetHandler {
    @Override
    public SkillTargetType getTargetType() {
        return SkillTargetType.FRONT_AREA;
    }

    @Override
    public Creature[] getTargetList(Creature caster, Creature target, L2Skill skill) {
        final List<Creature> list = new ArrayList<>();
        list.add(target);

        for (Creature creature : target.getKnownTypeInRadius(Creature.class, skill.getSkillRadius())) {
            if (creature == caster || !creature.isInFrontOf(caster) || creature.isDead() || !GeoEngine.getInstance().canSeeTarget(target, creature))
                continue;

            if (caster instanceof Playable && (creature instanceof Attackable || creature instanceof Playable)) {
                if (creature.isAttackableWithoutForceBy((Playable) caster))
                    list.add(creature);
            } else if (caster instanceof Attackable && creature instanceof Playable) {
                if (creature.isAttackableBy(caster))
                    list.add(creature);
            }
        }

        if (list.isEmpty())
            return EMPTY_TARGET_ARRAY;

        return list.toArray(new Creature[list.size()]);
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
}