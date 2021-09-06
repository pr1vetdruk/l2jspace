package ru.privetdruk.l2jspace.loginserver;

import ru.privetdruk.l2jspace.common.mmocore.*;
import ru.privetdruk.l2jspace.common.network.IPv4Filter;
import ru.privetdruk.l2jspace.loginserver.data.manager.IpBanManager;
import ru.privetdruk.l2jspace.loginserver.network.LoginClient;
import ru.privetdruk.l2jspace.loginserver.network.serverpackets.Init;

import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SelectorHelper implements IMMOExecutor<LoginClient>, IClientFactory<LoginClient>, IAcceptFilter {
    private final ThreadPoolExecutor _generalPacketsThreadPool;

    private final IPv4Filter _ipv4filter;

    public SelectorHelper() {
        _generalPacketsThreadPool = new ThreadPoolExecutor(4, 6, 15L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        _ipv4filter = new IPv4Filter();
    }

    @Override
    public void execute(ReceivablePacket<LoginClient> packet) {
        _generalPacketsThreadPool.execute(packet);
    }

    @Override
    public LoginClient create(MMOConnection<LoginClient> con) {
        LoginClient client = new LoginClient(con);
        client.sendPacket(new Init(client));
        return client;
    }

    @Override
    public boolean accept(Socket socket) {
        return _ipv4filter.accept(socket) && !IpBanManager.getInstance().isBannedAddress(socket.getInetAddress());
    }
}