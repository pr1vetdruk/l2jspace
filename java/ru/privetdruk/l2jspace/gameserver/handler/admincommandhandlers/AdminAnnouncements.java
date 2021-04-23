package ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers;

import ru.privetdruk.l2jspace.gameserver.data.xml.AdminData;
import ru.privetdruk.l2jspace.gameserver.data.xml.AnnouncementData;
import ru.privetdruk.l2jspace.gameserver.enums.SayType;
import ru.privetdruk.l2jspace.gameserver.handler.IAdminCommandHandler;
import ru.privetdruk.l2jspace.gameserver.model.World;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.CreatureSay;

public class AdminAnnouncements implements IAdminCommandHandler {
    private static final String[] ADMIN_COMMANDS =
            {
                    "admin_announce",
                    "admin_ann",
                    "admin_say",
                    "admin_gmchat"
            };

    @Override
    public void useAdminCommand(String command, Player player) {
        AnnouncementData announcement = AnnouncementData.getInstance();

        if (command.startsWith("admin_announce")) {
            try {
                final String[] tokens = command.split(" ", 3);
                switch (tokens[1]) {
                    case "list":
                        announcement.listAnnouncements(player);
                        break;

                    case "all":
                    case "all_auto":
                        final boolean isAuto = tokens[1].equalsIgnoreCase("all_auto");
                        World.getInstance().getPlayers().forEach(p -> announcement.showAnnouncements(p, isAuto));

                        announcement.listAnnouncements(player);
                        break;

                    case "add":
                        String[] split = tokens[2].split(" ", 2); // boolean string
                        boolean crit = Boolean.parseBoolean(split[0]);

                        if (!announcement.addAnnouncement(split[1], crit, false, -1, -1, -1))
                            player.sendMessage("Invalid //announce message content ; can't be null or empty.");

                        announcement.listAnnouncements(player);
                        break;

                    case "add_auto":
                        split = tokens[2].split(" ", 6); // boolean boolean int int int string
                        crit = Boolean.parseBoolean(split[0]);
                        final boolean auto = Boolean.parseBoolean(split[1]);
                        final int idelay = Integer.parseInt(split[2]);
                        final int delay = Integer.parseInt(split[3]);
                        final int limit = Integer.parseInt(split[4]);
                        final String msg = split[5];

                        if (!announcement.addAnnouncement(msg, crit, auto, idelay, delay, limit))
                            player.sendMessage("Invalid //announce message content ; can't be null or empty.");

                        announcement.listAnnouncements(player);
                        break;

                    case "del":
                        announcement.delAnnouncement(Integer.parseInt(tokens[2]));
                        announcement.listAnnouncements(player);
                        break;

                    default:
                        player.sendMessage("Possible //announce parameters : <list|all|add|add_auto|del>");
                        break;
                }
            } catch (Exception e) {
                sendFile(player, "announce.htm");
            }
        } else if (command.startsWith("admin_ann") || command.startsWith("admin_say"))
            announcement.handleAnnounce(command, 10, command.startsWith("admin_say"));
        else if (command.startsWith("admin_gmchat")) {
            try {
                AdminData.getInstance().broadcastToGMs(new CreatureSay(player, SayType.ALLIANCE, command.substring(13)));
            } catch (Exception e) {
                player.sendMessage("Invalid //gmchat message content ; can't be null or empty.");
            }
        }
    }

    @Override
    public String[] getAdminCommandList() {
        return ADMIN_COMMANDS;
    }
}