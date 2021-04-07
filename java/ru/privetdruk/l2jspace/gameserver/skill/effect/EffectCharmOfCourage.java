package ru.privetdruk.l2jspace.gameserver.skill.effect;

import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectFlag;
import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.EtcStatusUpdate;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectCharmOfCourage extends AbstractEffect {
    public EffectCharmOfCourage(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.CHARM_OF_COURAGE;
    }

    @Override
    public boolean onStart() {
        if (getEffected() instanceof Player) {
            getEffected().broadcastPacket(new EtcStatusUpdate((Player) getEffected()));
            return true;
        }
        return false;
    }

    @Override
    public void onExit() {
        if (getEffected() instanceof Player)
            getEffected().broadcastPacket(new EtcStatusUpdate((Player) getEffected()));
    }

    @Override
    public boolean onActionTime() {
        return false;
    }

    @Override
    public int getEffectFlags() {
        return EffectFlag.CHARM_OF_COURAGE.getMask();
    }
}