package ru.privetdruk.l2jspace.loginserver.network.loginserverpackets;

import ru.privetdruk.l2jspace.loginserver.LoginServer;
import ru.privetdruk.l2jspace.loginserver.network.serverpackets.ServerBasePacket;

public class InitLS extends ServerBasePacket {
    public InitLS(byte[] publickey) {
        writeC(0x00);
        writeD(LoginServer.PROTOCOL_REV);
        writeD(publickey.length);
        writeB(publickey);
    }

    @Override
    public byte[] getContent() {
        return getBytes();
    }
}