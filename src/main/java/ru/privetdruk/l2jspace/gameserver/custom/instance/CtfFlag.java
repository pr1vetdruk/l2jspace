package ru.privetdruk.l2jspace.gameserver.custom.instance;

import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;
import ru.privetdruk.l2jspace.gameserver.custom.event.CTF;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.CtfPlayer;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.CtfTeamSetting;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.NpcHtmlMessage;

public class CtfFlag extends Npc {
    public CtfFlag(int objectId, NpcTemplate template) {
        super(objectId, template, "custom/event/");
    }

    @Override
    public void onInteract(Player player) {
        CTF event = (CTF) EventEngine.findActive();

        if (event == null || event.getEventState() != EventState.IN_PROGRESS || !event.getPlayers().containsKey(player.getObjectId())) {
            return;
        }

        CtfPlayer eventPlayer = (CtfPlayer) event.getPlayers().get(player.getObjectId());

        event.processInFlagRange(eventPlayer);

        show(eventPlayer);
    }

    public void show(CtfPlayer eventPlayer) {
        CtfTeamSetting team = eventPlayer.getTeamSettings();
        NpcHtmlMessage dialog = new NpcHtmlMessage(5);
        StringBuilder page = new StringBuilder("<html><head><body><center>");

        page.append("<br><br>");
        page.append("<font color=\"00FF00\">Флаг команды ").append(team.getName()).append("</font><br1>");

        if (team.getFlag().getSpawn().getNpc().equals(this)) {
            page.append("<font color=\"LEVEL\">Это ваш флаг!</font><br1>");
        } else {
            page.append("<font color=\"LEVEL\">Это флаг противника!</font><br1>");
        }

        page.append("</center></body></html>");
        dialog.setHtml(page.toString());
        eventPlayer.getPlayer().sendPacket(dialog);
    }
}