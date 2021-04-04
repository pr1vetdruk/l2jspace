package ru.privetdruk.l2jspace.gameserver.skill.effects;

import ru.privetdruk.l2jspace.gameserver.enums.skills.AbnormalEffect;
import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectClanGate extends AbstractEffect {
    public EffectClanGate(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);
    }

    @Override
    public boolean onStart() {
        getEffected().startAbnormalEffect(AbnormalEffect.MAGIC_CIRCLE);

        if (getEffected() instanceof Player) {
            final Clan clan = ((Player) getEffected()).getClan();
            if (clan != null)
                clan.broadcastToOtherOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.COURT_MAGICIAN_CREATED_PORTAL), ((Player) getEffected()));
        }

        return true;
    }

    @Override
    public boolean onActionTime() {
        return false;
    }

    @Override
    public void onExit() {
        getEffected().stopAbnormalEffect(AbnormalEffect.MAGIC_CIRCLE);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.CLAN_GATE;
    }
}