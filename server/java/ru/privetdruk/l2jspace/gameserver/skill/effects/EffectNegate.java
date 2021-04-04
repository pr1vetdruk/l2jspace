package ru.privetdruk.l2jspace.gameserver.skill.effects;

import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectNegate extends AbstractEffect {
    public EffectNegate(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.NEGATE;
    }

    @Override
    public boolean onStart() {
        for (int negateSkillId : getSkill().getNegateId()) {
            if (negateSkillId != 0)
                getEffected().stopSkillEffects(negateSkillId);
        }

        for (SkillType negateSkillType : getSkill().getNegateStats())
            getEffected().stopSkillEffects(negateSkillType, getSkill().getNegateLvl());

        return true;
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}