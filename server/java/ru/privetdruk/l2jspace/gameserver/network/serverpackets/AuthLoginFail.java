package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.enums.FailReason;

public class AuthLoginFail extends L2GameServerPacket {
    private final FailReason _reason;

    public AuthLoginFail(FailReason reason) {
        _reason = reason;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x14);
        writeD(_reason.ordinal());
    }
}