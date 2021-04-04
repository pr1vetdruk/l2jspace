package ru.privetdruk.l2jspace.gameserver.handler.targethandlers;

import java.util.ArrayList;
import java.util.List;

import ru.privetdruk.l2jspace.common.util.ArraysUtil;

import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillTargetType;
import ru.privetdruk.l2jspace.gameserver.handler.ITargetHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Attackable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class TargetClan implements ITargetHandler {
    @Override
    public SkillTargetType getTargetType() {
        return SkillTargetType.CLAN;
    }

    @Override
    public Creature[] getTargetList(Creature caster, Creature target, L2Skill skill) {
        final List<Creature> list = new ArrayList<>();
        // TODO : Currently SkillTargetType.CLAN is used only by NPC skills.
        if (caster instanceof Attackable) {
            list.add(caster);
            for (Attackable attackable : caster.getKnownTypeInRadius(Attackable.class, skill.getCastRange())) {
                if (attackable.isDead() || !ArraysUtil.contains(((Attackable) caster).getTemplate().getClans(), attackable.getTemplate().getClans()))
                    continue;

                list.add(attackable);
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
        return true;
    }
}