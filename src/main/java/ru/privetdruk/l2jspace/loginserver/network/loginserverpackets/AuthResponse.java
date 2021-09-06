package ru.privetdruk.l2jspace.loginserver.network.loginserverpackets;

import ru.privetdruk.l2jspace.loginserver.data.manager.GameServerManager;
import ru.privetdruk.l2jspace.loginserver.network.serverpackets.ServerBasePacket;

public class AuthResponse extends ServerBasePacket {
    public AuthResponse(int serverId) {
        writeC(0x02);
        writeC(serverId);
        writeS(GameServerManager.getInstance().getServerNames().get(serverId));
    }

    @Override
    public byte[] getContent() {
        return getBytes();
    }
}