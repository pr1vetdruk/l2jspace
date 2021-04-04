package ru.privetdruk.l2jspace.gameserver.handler.targethandlers;

import java.util.ArrayList;
import java.util.List;

import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillTargetType;
import ru.privetdruk.l2jspace.gameserver.geoengine.GeoEngine;
import ru.privetdruk.l2jspace.gameserver.handler.ITargetHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Attackable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class TargetAreaSummon implements ITargetHandler {
    @Override
    public SkillTargetType getTargetType() {
        return SkillTargetType.AREA_SUMMON;
    }

    @Override
    public Creature[] getTargetList(Creature caster, Creature target, L2Skill skill) {
        if (!(caster instanceof Playable))
            return EMPTY_TARGET_ARRAY;

        final List<Creature> list = new ArrayList<>();
        for (Creature creature : target.getKnownTypeInRadius(Creature.class, skill.getSkillRadius())) {
            if (creature == caster || creature.isDead() || !GeoEngine.getInstance().canSeeTarget(target, creature))
                continue;

            if (creature instanceof Attackable || creature instanceof Playable) {
                if (creature.isAttackableWithoutForceBy((Playable) caster))
                    list.add(creature);
            }
        }

        if (list.isEmpty())
            return EMPTY_TARGET_ARRAY;

        return list.toArray(new Creature[list.size()]);
    }

    @Override
    public Creature getFinalTarget(Creature caster, Creature target, L2Skill skill) {
        final Creature summon = caster.getSummon();
        if (summon == null)
            return null;

        return summon;
    }

    @Override
    public boolean meetCastConditions(Playable caster, Creature target, L2Skill skill, boolean isCtrlPressed) {
        return true;
    }
}