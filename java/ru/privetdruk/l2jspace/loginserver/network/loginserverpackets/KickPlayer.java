package ru.privetdruk.l2jspace.loginserver.network.loginserverpackets;

import ru.privetdruk.l2jspace.loginserver.network.serverpackets.ServerBasePacket;

public class KickPlayer extends ServerBasePacket {
    public KickPlayer(String account) {
        writeC(0x04);
        writeS(account);
    }

    @Override
    public byte[] getContent() {
        return getBytes();
    }
}