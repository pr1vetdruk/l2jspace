package ru.privetdruk.l2jspace.gameserver.handler;

import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.*;

import java.util.HashMap;
import java.util.Map;

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