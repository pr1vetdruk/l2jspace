package ru.privetdruk.l2jspace.gameserver.custom.instance;

import ru.privetdruk.l2jspace.config.custom.EventConfig;
import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventTeamType;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.CtfTeamSetting;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Folk;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ActionFailed;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.NpcHtmlMessage;

import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState.IN_PROGRESS;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState.REGISTRATION;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventTeamType.SHUFFLE;

public class EventManager extends Folk {
    public EventManager(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onInteract(Player player) {
        EventEngine event = EventEngine.findActive();

        if (event == null) {
            return;
        }

        EventState eventState = event.getEventState();

        switch (event.getEventType()) {
            case CTF: {
                if (eventState != IN_PROGRESS && eventState != REGISTRATION) {

                } else if (eventState != IN_PROGRESS && event.getTeamMode() == SHUFFLE && event.getPlayers().size() >= event.getSettings().getMaxPlayers()) {

                } else if (player.isCursedWeaponEquipped() && !EventConfig.CTF.JOIN_CURSED_WEAPON) {
                    page.append("<font color=\"FFFF00\">You can't participate to this event with a cursed Weapon.</font><br>");
                } else if (eventState == REGISTRATION && eventPlayer.getLevel() >= setting.getMinLevel() && eventPlayer.getLevel() <= setting.getMaxLevel()) {
                    synchronized (ctf.getPlayers()) {
                        if (ctf.getPlayers().contains(eventPlayer)) {
                            if (NO.name().equals(CTF_EVEN_TEAMS) || BALANCE.name().equals(CTF_EVEN_TEAMS)) {
                                page.append("You participated already in team <font color=\"LEVEL\">").append(eventPlayer.teamNameCtf).append("</font><br><br>");
                            } else if (CTF_EVEN_TEAMS.equals("SHUFFLE")) {
                                page.append("<center><font color=\"3366CC\">You participated already!</font></center><br><br>");
                            }

                            page.append("<center>Joined Players: <font color=\"00FF00\">").append(ctf.getPlayers().size()).append("</font></center><br>");
                            page.append("<center><font color=\"3366CC\">Wait till event start or remove your participation!</font><center>");
                            page.append("<center><button value=\"Remove\" action=\"bypass -h npc_")
                                    .append(objectId).append("_ctf_player_leave\" width=85 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></center>");
                        } else {
                            page.append("<center><font color=\"3366CC\">You want to participate in the event?</font></center><br>");
                            page.append("<center><td width=\"200\">Min lvl: <font color=\"00FF00\">").append(setting.getMinLevel()).append("</font></center></td><br>");
                            page.append("<center><td width=\"200\">Max lvl: <font color=\"00FF00\">").append(setting.getMaxLevel()).append("</font></center></td><br><br>");
                            page.append("<center><font color=\"3366CC\">Teams:</font></center><br>");
                            if (NO.name().equals(CTF_EVEN_TEAMS) || BALANCE.name().equals(CTF_EVEN_TEAMS)) {
                                page.append("<center><table border=\"0\">");
                                for (CtfTeamSetting team : ctf.getTeamSetting()) {
                                    page.append("<tr><td width=\"100\"><font color=\"LEVEL\">")
                                            .append(team.getName()).append("</font>&nbsp;(").append(team.getPlayers()).append(" joined)</td>");
                                    page.append("<center><td width=\"60\"><button value=\"Join\" action=\"bypass -h npc_")
                                            .append(objectId).append("_ctf_player_join ").append(team.getName())
                                            .append("\" width=85 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></center></td></tr>");
                                }
                                page.append("</table></center>");
                            } else if (SHUFFLE.name().equals(CTF_EVEN_TEAMS)) {
                                page.append("<center>");

                                for (CtfTeamSetting team : ctf.getTeamSetting()) {
                                    page.append("<tr><td width=\"100\"><font color=\"LEVEL\">").append(team.getName()).append("</font> &nbsp;</td>");
                                }

                                page.append("</center><br>");

                                page.append("<center><button value=\"Join Event\" action=\"bypass -h npc_")
                                        .append(objectId)
                                        .append("_ctf_player_join eventShuffle\" width=85 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></center>");
                                page.append("<center><font color=\"3366CC\">Teams will be randomly generated!</font></center><br>");
                                page.append("<center>Joined Players:</font> <font color=\"LEVEL\">").append(ctf.getPlayers().size()).append("</center></font><br>");
                                page.append("<center>Reward: <font color=\"LEVEL\">").append(setting.getReward().getAmount())
                                        .append(" ").append(ItemTable.getInstance().getTemplate(setting.getReward().getId()).getName()).append("</center></font>");
                            }
                        }
                    }
                } else if (eventState == START) {
                    page.append("<center>").append(eventName).append(" match is in progress.</center>");
                } else if (eventPlayer.getLevel() < setting.getMinLevel() || eventPlayer.getLevel() > setting.getMaxLevel()) {
                    page.append("Your lvl: <font color=\"00FF00\">").append(eventPlayer.getLevel()).append("</font><br>");
                    page.append("Min lvl: <font color=\"00FF00\">").append(eventPlayer.getLevel()).append("</font><br>");
                    page.append("Max lvl: <font color=\"00FF00\">").append(setting.getMaxLevel()).append("</font><br><br>");
                    page.append("<font color=\"FFFF00\">You can't participate to this event.</font><br>");
                }
            }
        }


        player.curs
    }

    @Override
    public void showChatWindow(Player player, String filename) {
        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        html.setFile(filename);
        html.replace("%objectId%", getObjectId());
        player.sendPacket(html);

        player.sendPacket(ActionFailed.STATIC_PACKET);
    }

    @Override
    public String getHtmlPath(int npcId, int val) {
        String filename = "";
        if (val == 0)
            filename = "" + npcId;
        else
            filename = npcId + "-" + val;

        return "data/html/custom/event/" + filename + ".htm";
    }

    void qwe() {
        try {
            CTF ctf = find(REGISTRATION);

            String eventName = ctf.getEventIdentifier();

            GeneralSetting setting = ctf.getGeneralSetting();

            NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
            StringBuilder page = new StringBuilder();

            page.append("<html><title>").append(eventName).append("</title><body>");
            page.append("<center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32></center><br1>");
            page.append("<center><font color=\"3366CC\">Current event:</font></center><br1>");
            page.append("<center>Name:&nbsp;<font color=\"00FF00\">").append(eventName).append("</font></center><br1>");
            page.append("<center>Description:&nbsp;<font color=\"00FF00\">")
                    .append(setting.getEventDescription()).append("</font></center><br><br>");

            State eventState = ctf.getEventState();

            if (eventState != START && eventState != REGISTRATION) {
                page.append("<center>Wait till the admin/gm start the participation.</center>");
            } else if (eventState != START && SHUFFLE.name().equals(CTF_EVEN_TEAMS) && !ctf.checkMaxPlayers()) {
                page.append("Currently participated: <font color=\"00FF00\">").append(ctf.getPlayers().size()).append(".</font><br>");
                page.append("Max players: <font color=\"00FF00\">").append(setting.getMaxPlayers()).append("</font><br><br>");
                page.append("<font color=\"FFFF00\">You can't participate to this event.</font><br>");
            } else if (eventPlayer.isCursedWeaponEquiped() && !Config.CTF_JOIN_CURSED) {
                page.append("<font color=\"FFFF00\">You can't participate to this event with a cursed Weapon.</font><br>");
            } else if (eventState == REGISTRATION && eventPlayer.getLevel() >= setting.getMinLevel() && eventPlayer.getLevel() <= setting.getMaxLevel()) {
                synchronized (ctf.getPlayers()) {
                    if (ctf.getPlayers().contains(eventPlayer)) {
                        if (NO.name().equals(CTF_EVEN_TEAMS) || BALANCE.name().equals(CTF_EVEN_TEAMS)) {
                            page.append("You participated already in team <font color=\"LEVEL\">").append(eventPlayer.teamNameCtf).append("</font><br><br>");
                        } else if (CTF_EVEN_TEAMS.equals("SHUFFLE")) {
                            page.append("<center><font color=\"3366CC\">You participated already!</font></center><br><br>");
                        }

                        page.append("<center>Joined Players: <font color=\"00FF00\">").append(ctf.getPlayers().size()).append("</font></center><br>");
                        page.append("<center><font color=\"3366CC\">Wait till event start or remove your participation!</font><center>");
                        page.append("<center><button value=\"Remove\" action=\"bypass -h npc_")
                                .append(objectId).append("_ctf_player_leave\" width=85 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></center>");
                    } else {
                        page.append("<center><font color=\"3366CC\">You want to participate in the event?</font></center><br>");
                        page.append("<center><td width=\"200\">Min lvl: <font color=\"00FF00\">").append(setting.getMinLevel()).append("</font></center></td><br>");
                        page.append("<center><td width=\"200\">Max lvl: <font color=\"00FF00\">").append(setting.getMaxLevel()).append("</font></center></td><br><br>");
                        page.append("<center><font color=\"3366CC\">Teams:</font></center><br>");
                        if (NO.name().equals(CTF_EVEN_TEAMS) || BALANCE.name().equals(CTF_EVEN_TEAMS)) {
                            page.append("<center><table border=\"0\">");
                            for (CtfTeamSetting team : ctf.getTeamSetting()) {
                                page.append("<tr><td width=\"100\"><font color=\"LEVEL\">")
                                        .append(team.getName()).append("</font>&nbsp;(").append(team.getPlayers()).append(" joined)</td>");
                                page.append("<center><td width=\"60\"><button value=\"Join\" action=\"bypass -h npc_")
                                        .append(objectId).append("_ctf_player_join ").append(team.getName())
                                        .append("\" width=85 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></center></td></tr>");
                            }
                            page.append("</table></center>");
                        } else if (SHUFFLE.name().equals(CTF_EVEN_TEAMS)) {
                            page.append("<center>");

                            for (CtfTeamSetting team : ctf.getTeamSetting()) {
                                page.append("<tr><td width=\"100\"><font color=\"LEVEL\">").append(team.getName()).append("</font> &nbsp;</td>");
                            }

                            page.append("</center><br>");

                            page.append("<center><button value=\"Join Event\" action=\"bypass -h npc_")
                                    .append(objectId)
                                    .append("_ctf_player_join eventShuffle\" width=85 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></center>");
                            page.append("<center><font color=\"3366CC\">Teams will be randomly generated!</font></center><br>");
                            page.append("<center>Joined Players:</font> <font color=\"LEVEL\">").append(ctf.getPlayers().size()).append("</center></font><br>");
                            page.append("<center>Reward: <font color=\"LEVEL\">").append(setting.getReward().getAmount())
                                    .append(" ").append(ItemTable.getInstance().getTemplate(setting.getReward().getId()).getName()).append("</center></font>");
                        }
                    }
                }
            } else if (eventState == START) {
                page.append("<center>").append(eventName).append(" match is in progress.</center>");
            } else if (eventPlayer.getLevel() < setting.getMinLevel() || eventPlayer.getLevel() > setting.getMaxLevel()) {
                page.append("Your lvl: <font color=\"00FF00\">").append(eventPlayer.getLevel()).append("</font><br>");
                page.append("Min lvl: <font color=\"00FF00\">").append(eventPlayer.getLevel()).append("</font><br>");
                page.append("Max lvl: <font color=\"00FF00\">").append(setting.getMaxLevel()).append("</font><br><br>");
                page.append("<font color=\"FFFF00\">You can't participate to this event.</font><br>");
            }

            page.append("</body></html>");
            adminReply.setHtml(page.toString());
            eventPlayer.sendPacket(adminReply);

            // Send a Server->Client ActionFailed to the PlayerInstance in order to avoid that the client wait another packet
            eventPlayer.sendPacket(ActionFailed.STATIC_PACKET);
        } catch (Exception e) {
            LOGGER.severe("CTF.showEventHtlm(" + eventPlayer.getName() + ", " + objectId + ")]: exception" + e.getMessage());
        }
    }
}
