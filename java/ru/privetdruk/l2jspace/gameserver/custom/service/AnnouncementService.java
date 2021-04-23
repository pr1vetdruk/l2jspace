package ru.privetdruk.l2jspace.gameserver.custom.service;

import ru.privetdruk.l2jspace.gameserver.enums.SayType;
import ru.privetdruk.l2jspace.gameserver.model.World;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.CreatureSay;

public class AnnouncementService {
    public static AnnouncementService getInstance() {
        return AnnouncementService.SingletonHolder.INSTANCE;
    }

    public void criticalToAll(String text) {
        final CreatureSay cs = new CreatureSay(0, SayType.CRITICAL_ANNOUNCE, null, text);
        for (Player player : World.getInstance().getPlayers()) {
            if ((player != null) && player.isOnline()) {
                player.sendPacket(cs);
            }
        }
    }

    private AnnouncementService() {
    }

    private static class SingletonHolder {
        protected static final AnnouncementService INSTANCE = new AnnouncementService();
    }
}
