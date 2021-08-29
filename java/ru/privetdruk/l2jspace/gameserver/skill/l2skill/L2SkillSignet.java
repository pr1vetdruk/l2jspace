package ru.privetdruk.l2jspace.gameserver.skill.l2skill;

import ru.privetdruk.l2jspace.common.data.StatSet;
import ru.privetdruk.l2jspace.gameserver.data.xml.NpcData;
import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillTargetType;
import ru.privetdruk.l2jspace.gameserver.idfactory.IdFactory;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.EffectPoint;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.model.location.Location;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public final class L2SkillSignet extends L2Skill {
    public final int effectNpcId;
    public final int effectId;

    public L2SkillSignet(StatSet set) {
        super(set);
        effectNpcId = set.getInteger("effectNpcId", -1);
        effectId = set.getInteger("effectId", -1);
    }

    @Override
    public void useSkill(Creature caster, WorldObject[] targets) {
        if (caster.isAlikeDead())
            return;

        final NpcTemplate template = NpcData.getInstance().getTemplate(effectNpcId);
        if (template == null)
            return;

        final EffectPoint effectPoint = new EffectPoint(IdFactory.getInstance().getNextId(), template, caster);
        effectPoint.getStatus().setMaxHpMp();

        Location worldPosition = null;
        if (caster instanceof Player && getTargetType() == SkillTargetType.GROUND)
            worldPosition = ((Player) caster).getCast().getSignetLocation();

        getEffects(caster, effectPoint);

        effectPoint.setInvul(true);
        effectPoint.spawnMe((worldPosition != null) ? worldPosition : caster.getPosition());
    }
}