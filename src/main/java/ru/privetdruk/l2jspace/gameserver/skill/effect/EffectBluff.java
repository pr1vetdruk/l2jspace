package ru.privetdruk.l2jspace.gameserver.skill.effect;

import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Folk;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.SiegeSummon;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.StartRotation;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.StopRotation;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectBluff extends AbstractEffect {
    public EffectBluff(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.BLUFF;
    }

    @Override
    public boolean onStart() {
        if (getEffected() instanceof SiegeSummon || getEffected() instanceof Folk || getEffected().isRaidRelated() || (getEffected() instanceof Npc && ((Npc) getEffected()).getNpcId() == 35062))
            return false;

        getEffected().broadcastPacket(new StartRotation(getEffected().getId(), getEffected().getHeading(), 1, 65535));
        getEffected().broadcastPacket(new StopRotation(getEffected().getId(), getEffector().getHeading(), 65535));
        getEffected().getPosition().setHeading(getEffector().getHeading());
        return true;
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}