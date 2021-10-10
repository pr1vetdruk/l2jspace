package ru.privetdruk.l2jspace.gameserver.model.actor.ai.type;

import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.CastleChamberlain;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.NpcHtmlMessage;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class CastleManagerNpcAI extends CreatureAI {
    public CastleManagerNpcAI(Creature creature) {
        super(creature);
    }

    @Override
    public CastleChamberlain getActor() {
        return (CastleChamberlain) _actor;
    }

    @Override
    protected void thinkCast() {
        L2Skill skill = _currentIntention.getSkill();

        if (getActor().isSkillDisabled(skill)) {
            return;
        }

        Player player = (Player) _currentIntention.getFinalTarget();

        NpcHtmlMessage html = new NpcHtmlMessage(getActor().getId());
        if (getActor().getStatus().getMp() < skill.getMpConsume() + skill.getMpInitialConsume()) {
            html.setFile("data/html/chamberlain/support-no_mana.htm");
        } else {
            super.thinkCast();
            html.setFile("data/html/chamberlain/support-done.htm");
        }

        html.replace("%mp%", (int) getActor().getStatus().getMp());
        html.replace("%objectId%", getActor().getId());

        player.sendPacket(html);
    }
}
