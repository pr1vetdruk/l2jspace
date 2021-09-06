package ru.privetdruk.l2jspace.common.mmocore;

import java.nio.ByteBuffer;

public abstract class AbstractPacket<T extends MMOClient<?>> {
    protected ByteBuffer _buf;

    T _client;

    public final T getClient() {
        return _client;
    }
}