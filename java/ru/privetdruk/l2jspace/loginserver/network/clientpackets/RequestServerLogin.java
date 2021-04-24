package ru.privetdruk.l2jspace.loginserver.network.clientpackets;

import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.loginserver.model.Account;
import ru.privetdruk.l2jspace.loginserver.network.SessionKey;
import ru.privetdruk.l2jspace.loginserver.network.serverpackets.LoginFail;
import ru.privetdruk.l2jspace.loginserver.network.serverpackets.PlayFail;
import ru.privetdruk.l2jspace.loginserver.network.serverpackets.PlayOk;

public class RequestServerLogin extends L2LoginClientPacket {
    private int _skey1;
    private int _skey2;
    private int _serverId;

    public int getSessionKey1() {
        return _skey1;
    }

    public int getSessionKey2() {
        return _skey2;
    }

    public int getServerID() {
        return _serverId;
    }

    @Override
    public boolean readImpl() {
        if (super._buf.remaining() >= 9) {
            _skey1 = readD();
            _skey2 = readD();
            _serverId = readC();
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        final SessionKey sk = getClient().getSessionKey();

        // Check Show Licence window.
        if (Config.SHOW_LICENCE && !sk.checkLoginPair(_skey1, _skey2)) {
            getClient().close(LoginFail.REASON_ACCESS_FAILED);
            return;
        }

        // Check Account integrity.
        final Account account = getClient().getAccount();
        if (account == null) {
            getClient().close(LoginFail.REASON_ACCESS_FAILED);
            return;
        }

        // Check possibility of login.
        if (!account.isLoginPossible(_serverId)) {
            getClient().close(PlayFail.REASON_TOO_MANY_PLAYERS);
            return;
        }

        getClient().setJoinedGS(true);
        getClient().sendPacket(new PlayOk(sk));
    }
}
