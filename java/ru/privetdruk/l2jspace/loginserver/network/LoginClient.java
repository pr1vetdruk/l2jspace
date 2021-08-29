package ru.privetdruk.l2jspace.loginserver.network;

import ru.privetdruk.l2jspace.common.logging.CLogger;
import ru.privetdruk.l2jspace.common.mmocore.MMOClient;
import ru.privetdruk.l2jspace.common.mmocore.MMOConnection;
import ru.privetdruk.l2jspace.common.mmocore.SendablePacket;
import ru.privetdruk.l2jspace.common.random.Rnd;
import ru.privetdruk.l2jspace.loginserver.LoginController;
import ru.privetdruk.l2jspace.loginserver.crypt.LoginCrypt;
import ru.privetdruk.l2jspace.loginserver.crypt.ScrambledKeyPair;
import ru.privetdruk.l2jspace.loginserver.enums.LoginClientState;
import ru.privetdruk.l2jspace.loginserver.model.Account;
import ru.privetdruk.l2jspace.loginserver.network.serverpackets.L2LoginServerPacket;
import ru.privetdruk.l2jspace.loginserver.network.serverpackets.LoginFail;
import ru.privetdruk.l2jspace.loginserver.network.serverpackets.PlayFail;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.security.interfaces.RSAPrivateKey;

/**
 * Represents a client connected into the LoginServer
 */
public final class LoginClient extends MMOClient<MMOConnection<LoginClient>> {
    private static final CLogger LOGGER = new CLogger(LoginClient.class.getName());

    private final LoginCrypt _loginCrypt;
    private final ScrambledKeyPair _scrambledPair;
    private final byte[] _blowfishKey;
    private final int _sessionId;
    private final long _connectionStartTime;

    private LoginClientState _state;
    private Account _account;
    private SessionKey _sessionKey;
    private boolean _joinedGS;

    public LoginClient(MMOConnection<LoginClient> con) {
        super(con);

        _state = LoginClientState.CONNECTED;
        _scrambledPair = LoginController.getInstance().getScrambledRSAKeyPair();
        _blowfishKey = LoginController.getInstance().getRandomBlowfishKey();
        _sessionId = Rnd.nextInt();
        _connectionStartTime = System.currentTimeMillis();
        _loginCrypt = new LoginCrypt();
        _loginCrypt.setKey(_blowfishKey);
    }

    @Override
    public String toString() {
        final InetAddress address = getConnection().getInetAddress();
        if (getState() == LoginClientState.AUTHED_LOGIN)
            return "[" + getAccount() + " (" + (address == null ? "disconnected" : address.getHostAddress()) + ")]";

        return "[" + (address == null ? "disconnected" : address.getHostAddress()) + "]";
    }

    @Override
    public boolean decrypt(ByteBuffer buf, int size) {
        try {
            if (!_loginCrypt.decrypt(buf.array(), buf.position(), size)) {
                super.getConnection().close((SendablePacket<LoginClient>) null);
                return false;
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("Couldn't decrypt LoginClient packet.", e);
            super.getConnection().close((SendablePacket<LoginClient>) null);
            return false;
        }
    }

    @Override
    public boolean encrypt(ByteBuffer buf, int size) {
        final int offset = buf.position();
        try {
            size = _loginCrypt.encrypt(buf.array(), offset, size);
        } catch (Exception e) {
            LOGGER.error("Couldn't encrypt LoginClient packet.", e);
            return false;
        }

        buf.position(offset + size);
        return true;
    }

    @Override
    public void onDisconnection() {
        if (_account == null)
            return;

        if (!hasJoinedGS() || (getConnectionStartTime() + LoginController.LOGIN_TIMEOUT) < System.currentTimeMillis())
            LoginController.getInstance().removeAuthedLoginClient(_account.getLogin());
    }

    @Override
    protected void onForcedDisconnection() {
    }

    public LoginClientState getState() {
        return _state;
    }

    public void setState(LoginClientState state) {
        _state = state;
    }

    public byte[] getBlowfishKey() {
        return _blowfishKey;
    }

    public byte[] getScrambledModulus() {
        return _scrambledPair.getScrambledModulus();
    }

    public RSAPrivateKey getRSAPrivateKey() {
        return (RSAPrivateKey) _scrambledPair.getKeyPair().getPrivate();
    }

    public Account getAccount() {
        return _account;
    }

    public void setAccount(Account account) {
        _account = account;
    }

    public int getSessionId() {
        return _sessionId;
    }

    public boolean hasJoinedGS() {
        return _joinedGS;
    }

    public void setJoinedGS(boolean val) {
        _joinedGS = val;
    }

    public void setSessionKey(SessionKey sessionKey) {
        _sessionKey = sessionKey;
    }

    public SessionKey getSessionKey() {
        return _sessionKey;
    }

    public long getConnectionStartTime() {
        return _connectionStartTime;
    }

    public void sendPacket(L2LoginServerPacket lsp) {
        getConnection().sendPacket(lsp);
    }

    public void close(LoginFail reason) {
        getConnection().close(reason);
    }

    public void close(PlayFail reason) {
        getConnection().close(reason);
    }

    public void close(L2LoginServerPacket lsp) {
        getConnection().close(lsp);
    }
}