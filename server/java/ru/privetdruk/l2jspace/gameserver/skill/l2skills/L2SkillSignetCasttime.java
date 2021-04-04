package ru.privetdruk.l2jspace.gameserver.skill.l2skills;

import ru.privetdruk.l2jspace.common.data.StatSet;

import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public final class L2SkillSignetCasttime extends L2Skill {
    public final int effectNpcId;
    public final int effectId;

    public L2SkillSignetCasttime(StatSet set) {
        super(set);
        effectNpcId = set.getInteger("effectNpcId", -1);
        effectId = set.getInteger("effectId", -1);
    }

    @Override
    public void useSkill(Creature caster, WorldObject[] targets) {
        if (caster.isAlikeDead())
            return;

        getEffectsSelf(caster);
    }
}