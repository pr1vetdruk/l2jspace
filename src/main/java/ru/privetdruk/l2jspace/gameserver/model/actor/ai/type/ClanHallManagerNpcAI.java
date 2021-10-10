package ru.privetdruk.l2jspace.gameserver.model.actor.ai.type;

import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.ClanHallManagerNpc;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.NpcHtmlMessage;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class ClanHallManagerNpcAI extends CreatureAI {
    public ClanHallManagerNpcAI(Creature creature) {
        super(creature);
    }

    @Override
    public ClanHallManagerNpc getActor() {
        return (ClanHallManagerNpc) _actor;
    }

    @Override
    protected void thinkCast() {
        final L2Skill skill = _currentIntention.getSkill();

        if (getActor().isSkillDisabled(skill))
            return;

        final Player player = (Player) _currentIntention.getFinalTarget();

        final NpcHtmlMessage html = new NpcHtmlMessage(getActor().getId());
        if (getActor().getStatus().getMp() < skill.getMpConsume() + skill.getMpInitialConsume())
            html.setFile("data/html/clanHallManager/support-no_mana.htm");
        else {
            super.thinkCast();

            html.setFile("data/html/clanHallManager/support-done.htm");
        }

        html.replace("%mp%", (int) getActor().getStatus().getMp());
        html.replace("%objectId%", getActor().getId());
        player.sendPacket(html);
    }
}