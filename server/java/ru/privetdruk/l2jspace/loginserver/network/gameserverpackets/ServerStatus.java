package ru.privetdruk.l2jspace.loginserver.network.gameserverpackets;

import ru.privetdruk.l2jspace.common.network.AttributeType;
import ru.privetdruk.l2jspace.common.network.ServerType;

import ru.privetdruk.l2jspace.loginserver.data.manager.GameServerManager;
import ru.privetdruk.l2jspace.loginserver.model.GameServerInfo;
import ru.privetdruk.l2jspace.loginserver.network.clientpackets.ClientBasePacket;

public class ServerStatus extends ClientBasePacket {
    private static final int ON = 0x01;

    public ServerStatus(byte[] decrypt, int serverId) {
        super(decrypt);

        GameServerInfo gsi = GameServerManager.getInstance().getRegisteredGameServers().get(serverId);
        if (gsi != null) {
            int size = readD();
            for (int i = 0; i < size; i++) {
                int type = readD();
                int value = readD();

                switch (AttributeType.VALUES[type]) {
                    case STATUS:
                        gsi.setType(ServerType.VALUES[value]);
                        break;

                    case CLOCK:
                        gsi.setShowingClock(value == ON);
                        break;

                    case BRACKETS:
                        gsi.setShowingBrackets(value == ON);
                        break;

                    case AGE_LIMIT:
                        gsi.setAgeLimit(value);
                        break;

                    case TEST_SERVER:
                        gsi.setTestServer(value == ON);
                        break;

                    case PVP_SERVER:
                        gsi.setPvp(value == ON);
                        break;

                    case MAX_PLAYERS:
                        gsi.setMaxPlayers(value);
                        break;
                }
            }
        }
    }
}