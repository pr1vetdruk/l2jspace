package ru.privetdruk.l2jspace.common.mmocore;

import java.nio.channels.SocketChannel;

public interface IAcceptFilter {
    public boolean accept(SocketChannel sc);
}