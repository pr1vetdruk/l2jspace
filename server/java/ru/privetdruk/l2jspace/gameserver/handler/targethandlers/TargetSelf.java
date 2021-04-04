package ru.privetdruk.l2jspace.gameserver.handler.targethandlers;

import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillTargetType;
import ru.privetdruk.l2jspace.gameserver.handler.ITargetHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class TargetSelf implements ITargetHandler {
    @Override
    public SkillTargetType getTargetType() {
        return SkillTargetType.SELF;
    }

    @Override
    public Creature[] getTargetList(Creature caster, Creature target, L2Skill skill) {
        return new Creature[]
                {
                        caster
                };
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