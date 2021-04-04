package ru.privetdruk.l2jspace.gameserver.skill.l2skills;

import ru.privetdruk.l2jspace.common.data.StatSet;

import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillTargetType;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;
import ru.privetdruk.l2jspace.gameserver.skill.effects.EffectSeed;

public class L2SkillSeed extends L2Skill {
    public L2SkillSeed(StatSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature caster, WorldObject[] targets) {
        if (caster.isAlikeDead())
            return;

        // Update Seeds Effects
        for (WorldObject obj : targets) {
            if (!(obj instanceof Creature))
                continue;

            final Creature target = ((Creature) obj);
            if (target.isAlikeDead() && getTargetType() != SkillTargetType.CORPSE_MOB)
                continue;

            EffectSeed oldEffect = (EffectSeed) target.getFirstEffect(getId());
            if (oldEffect == null)
                getEffects(caster, target);
            else
                oldEffect.increasePower();

            for (AbstractEffect effect : target.getAllEffects())
                if (effect.getEffectType() == EffectType.SEED)
                    effect.rescheduleEffect();
        }
    }
}