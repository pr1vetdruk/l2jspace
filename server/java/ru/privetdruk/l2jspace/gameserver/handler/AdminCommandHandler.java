package ru.privetdruk.l2jspace.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminAdmin;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminAnnouncements;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminBookmark;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminCached;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminClanHall;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminCursedWeapon;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminDoor;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminEditChar;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminEffects;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminEnchant;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminFind;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminGeoEngine;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminInfo;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminItem;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminKnownlist;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminMaintenance;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminManage;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminManor;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminMovieMaker;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminOlympiad;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminPetition;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminPledge;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminPolymorph;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminPremium;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminPunish;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminReload;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminSiege;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminSkill;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminSpawn;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminSummon;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminTarget;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminTeleport;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminTest;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminZone;

public class AdminCommandHandler {
    private final Map<Integer, IAdminCommandHandler> _entries = new HashMap<>();

    protected AdminCommandHandler() {
        registerHandler(new AdminAdmin());
        registerHandler(new AdminAnnouncements());
        registerHandler(new AdminBookmark());
        registerHandler(new AdminCached());
        registerHandler(new AdminClanHall());
        registerHandler(new AdminCursedWeapon());
        registerHandler(new AdminDoor());
        registerHandler(new AdminEditChar());
        registerHandler(new AdminEffects());
        registerHandler(new AdminEnchant());
        registerHandler(new AdminFind());
        registerHandler(new AdminGeoEngine());
        registerHandler(new AdminInfo());
        registerHandler(new AdminItem());
        registerHandler(new AdminKnownlist());
        registerHandler(new AdminMaintenance());
        registerHandler(new AdminManage());
        registerHandler(new AdminManor());
        registerHandler(new AdminMovieMaker());
        registerHandler(new AdminOlympiad());
        registerHandler(new AdminPetition());
        registerHandler(new AdminPledge());
        registerHandler(new AdminPolymorph());
        registerHandler(new AdminPremium());
        registerHandler(new AdminPunish());
        registerHandler(new AdminReload());
        registerHandler(new AdminSiege());
        registerHandler(new AdminSkill());
        registerHandler(new AdminSpawn());
        registerHandler(new AdminSummon());
        registerHandler(new AdminTarget());
        registerHandler(new AdminTeleport());
        registerHandler(new AdminTest());
        registerHandler(new AdminZone());
    }

    private void registerHandler(IAdminCommandHandler handler) {
        for (String id : handler.getAdminCommandList())
            _entries.put(id.hashCode(), handler);
    }

    public IAdminCommandHandler getHandler(String adminCommand) {
        String command = adminCommand;

        if (adminCommand.indexOf(" ") != -1)
            command = adminCommand.substring(0, adminCommand.indexOf(" "));

        return _entries.get(command.hashCode());
    }

    public int size() {
        return _entries.size();
    }

    public static AdminCommandHandler getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final AdminCommandHandler INSTANCE = new AdminCommandHandler();
    }
}