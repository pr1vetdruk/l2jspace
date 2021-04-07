package ru.privetdruk.l2jspace.loginserver.network.gameserverpackets;

import ru.privetdruk.l2jspace.loginserver.network.clientpackets.ClientBasePacket;

public class PlayerLogout extends ClientBasePacket {
    private final String _account;

    public PlayerLogout(byte[] decrypt) {
        super(decrypt);
        _account = readS();
    }

    public String getAccount() {
        return _account;
    }
}