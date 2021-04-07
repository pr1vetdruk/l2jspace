package ru.privetdruk.l2jspace.common.mmocore;

import java.nio.ByteBuffer;

public interface IPacketHandler<T extends MMOClient<?>> {
    public ReceivablePacket<T> handlePacket(ByteBuffer buf, T client);
}