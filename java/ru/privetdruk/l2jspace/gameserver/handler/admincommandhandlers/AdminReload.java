package ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;

import ru.privetdruk.l2jspace.Config;
import ru.privetdruk.l2jspace.gameserver.data.SkillTable;
import ru.privetdruk.l2jspace.gameserver.data.cache.CrestCache;
import ru.privetdruk.l2jspace.gameserver.data.cache.HtmCache;
import ru.privetdruk.l2jspace.gameserver.data.manager.BuyListManager;
import ru.privetdruk.l2jspace.gameserver.data.manager.CursedWeaponManager;
import ru.privetdruk.l2jspace.gameserver.data.manager.ZoneManager;
import ru.privetdruk.l2jspace.gameserver.data.xml.AdminData;
import ru.privetdruk.l2jspace.gameserver.data.xml.AnnouncementData;
import ru.privetdruk.l2jspace.gameserver.data.xml.DoorData;
import ru.privetdruk.l2jspace.gameserver.data.xml.InstantTeleportData;
import ru.privetdruk.l2jspace.gameserver.data.xml.ItemData;
import ru.privetdruk.l2jspace.gameserver.data.xml.MultisellData;
import ru.privetdruk.l2jspace.gameserver.data.xml.NpcData;
import ru.privetdruk.l2jspace.gameserver.data.xml.ScriptData;
import ru.privetdruk.l2jspace.gameserver.data.xml.TeleportData;
import ru.privetdruk.l2jspace.gameserver.data.xml.WalkerRouteData;
import ru.privetdruk.l2jspace.gameserver.handler.IAdminCommandHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

public class AdminReload implements IAdminCommandHandler {
    private static final String[] ADMIN_COMMANDS =
            {
                    "admin_reload"
            };

    @Override
    public void useAdminCommand(String command, Player player) {
        final StringTokenizer st = new StringTokenizer(command);
        st.nextToken();

        try {
            do {
                String type = st.nextToken();
                if (type.startsWith("admin")) {
                    AdminData.getInstance().reload();
                    player.sendMessage("Admin data has been reloaded.");
                } else if (type.startsWith("announcement")) {
                    AnnouncementData.getInstance().reload();
                    player.sendMessage("The content of announcements.xml has been reloaded.");
                } else if (type.startsWith("buylist")) {
                    BuyListManager.getInstance().reload();
                    player.sendMessage("Buylists have been reloaded.");
                } else if (type.startsWith("config")) {
                    Config.loadGameServer();
                    player.sendMessage("Configs files have been reloaded.");
                } else if (type.startsWith("crest")) {
                    CrestCache.getInstance().reload();
                    player.sendMessage("Crests have been reloaded.");
                } else if (type.startsWith("cw")) {
                    CursedWeaponManager.getInstance().reload();
                    player.sendMessage("Cursed weapons have been reloaded.");
                } else if (type.startsWith("door")) {
                    DoorData.getInstance().reload();
                    player.sendMessage("Doors instance has been reloaded.");
                } else if (type.startsWith("htm")) {
                    HtmCache.getInstance().reload();
                    player.sendMessage("The HTM cache has been reloaded.");
                } else if (type.startsWith("item")) {
                    ItemData.getInstance().reload();
                    player.sendMessage("Items' templates have been reloaded.");
                } else if (type.equals("multisell")) {
                    MultisellData.getInstance().reload();
                    player.sendMessage("The multisell instance has been reloaded.");
                } else if (type.equals("npc")) {
                    NpcData.getInstance().reload();
                    ScriptData.getInstance().reload();
                    player.sendMessage("NPCs templates and Scripts have been reloaded.");
                } else if (type.startsWith("npcwalker")) {
                    WalkerRouteData.getInstance().reload();
                    player.sendMessage("Walker routes have been reloaded.");
                } else if (type.equals("script")) {
                    ScriptData.getInstance().reload();
                    player.sendMessage("Scripts have been reloaded.");
                } else if (type.startsWith("skill")) {
                    SkillTable.getInstance().reload();
                    player.sendMessage("Skills' XMLs have been reloaded.");
                } else if (type.startsWith("teleport")) {
                    InstantTeleportData.getInstance().reload();
                    TeleportData.getInstance().reload();
                    player.sendMessage("Teleport locations have been reloaded.");
                } else if (type.startsWith("zone")) {
                    ZoneManager.getInstance().reload();
                    player.sendMessage("Zones have been reloaded.");
                } else
                    sendUsage(player);
            }
            while (st.hasMoreTokens());
        } catch (Exception e) {
            sendUsage(player);
        }
    }

    public void sendUsage(Player player) {
        player.sendMessage("Usage : //reload <admin|announcement|buylist|config>");
        player.sendMessage("Usage : //reload <crest|cw|door|htm|item|multisell|npc>");
        player.sendMessage("Usage : //reload <npcwalker|script|skill|teleport|zone>");
    }

    @Override
    public String[] getAdminCommandList() {
        return ADMIN_COMMANDS;
    }
}