package ru.privetdruk.l2jspace.common.mmocore;

import java.net.Socket;

public interface IAcceptFilter {
    public boolean accept(Socket socket);
}