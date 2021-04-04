package ru.privetdruk.l2jspace.gameserver.handler.targethandlers;

import java.util.ArrayList;
import java.util.List;

import ru.privetdruk.l2jspace.gameserver.enums.ZoneId;
import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillTargetType;
import ru.privetdruk.l2jspace.gameserver.geoengine.GeoEngine;
import ru.privetdruk.l2jspace.gameserver.handler.ITargetHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Attackable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class TargetAura implements ITargetHandler {
    @Override
    public SkillTargetType getTargetType() {
        return SkillTargetType.AURA;
    }

    @Override
    public Creature[] getTargetList(Creature caster, Creature target, L2Skill skill) {
        final List<Creature> list = new ArrayList<>();
        for (Creature creature : caster.getKnownTypeInRadius(Creature.class, skill.getSkillRadius())) {
            if (creature.isDead() || !GeoEngine.getInstance().canSeeTarget(caster, creature))
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
        return caster;
    }

    @Override
    public boolean meetCastConditions(Playable caster, Creature target, L2Skill skill, boolean isCtrlPressed) {
        if (skill.isOffensive() && caster.isInsideZone(ZoneId.PEACE)) {
            caster.sendPacket(SystemMessageId.CANT_ATK_PEACEZONE);
            return false;
        }
        return true;
    }
}