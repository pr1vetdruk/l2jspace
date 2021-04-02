package net.sf.l2j.gameserver.handler.targethandlers;

import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.gameserver.enums.skills.SkillTargetType;
import net.sf.l2j.gameserver.geoengine.GeoEngine;
import net.sf.l2j.gameserver.handler.ITargetHandler;
import net.sf.l2j.gameserver.model.actor.Attackable;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.skills.L2Skill;

public class TargetAreaCorpseMob implements ITargetHandler {
    @Override
    public SkillTargetType getTargetType() {
        return SkillTargetType.AREA_CORPSE_MOB;
    }

    @Override
    public Creature[] getTargetList(Creature caster, Creature target, L2Skill skill) {
        final List<Creature> list = new ArrayList<>();
        list.add(target);

        for (Creature creature : target.getKnownTypeInRadius(Creature.class, skill.getSkillRadius())) {
            if (creature == caster || creature.isDead() || !GeoEngine.getInstance().canSeeTarget(target, creature))
                continue;

            if (caster instanceof Playable && (creature instanceof Attackable || creature instanceof Playable)) {
                if (creature.isAttackableWithoutForceBy((Playable) caster))
                    list.add(creature);
            } else if (caster instanceof Attackable && creature instanceof Playable) {
                if (creature.isAttackableBy(caster))
                    list.add(creature);
            }
        }

        return list.toArray(new Creature[list.size()]);
    }

    @Override
    public Creature getFinalTarget(Creature caster, Creature target, L2Skill skill) {
        return target;
    }

    @Override
    public boolean meetCastConditions(Playable caster, Creature target, L2Skill skill, boolean isCtrlPressed) {
        if ((!(target instanceof Attackable)) || !target.isDead()) {
            caster.sendPacket(SystemMessageId.INVALID_TARGET);
            return false;
        }
        return true;
    }
}