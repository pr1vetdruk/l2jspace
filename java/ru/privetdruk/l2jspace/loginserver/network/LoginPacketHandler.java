package ru.privetdruk.l2jspace.loginserver.network;

import java.nio.ByteBuffer;

import ru.privetdruk.l2jspace.common.logging.CLogger;
import ru.privetdruk.l2jspace.common.mmocore.IPacketHandler;
import ru.privetdruk.l2jspace.common.mmocore.ReceivablePacket;

import ru.privetdruk.l2jspace.loginserver.enums.LoginClientState;
import ru.privetdruk.l2jspace.loginserver.network.clientpackets.AuthGameGuard;
import ru.privetdruk.l2jspace.loginserver.network.clientpackets.RequestAuthLogin;
import ru.privetdruk.l2jspace.loginserver.network.clientpackets.RequestServerList;
import ru.privetdruk.l2jspace.loginserver.network.clientpackets.RequestServerLogin;

/**
 * Handler for packets received by Login Server
 */
public final class LoginPacketHandler implements IPacketHandler<LoginClient> {
    private static final CLogger LOGGER = new CLogger(LoginPacketHandler.class.getName());

    @Override
    public ReceivablePacket<LoginClient> handlePacket(ByteBuffer buf, LoginClient client) {
        int opcode = buf.get() & 0xFF;

        ReceivablePacket<LoginClient> packet = null;
        LoginClientState state = client.getState();

        switch (state) {
            case CONNECTED:
                if (opcode == 0x07)
                    packet = new AuthGameGuard();
                else
                    debugOpcode(opcode, state);
                break;

            case AUTHED_GG:
                if (opcode == 0x00)
                    packet = new RequestAuthLogin();
                else
                    debugOpcode(opcode, state);
                break;

            case AUTHED_LOGIN:
                if (opcode == 0x05)
                    packet = new RequestServerList();
                else if (opcode == 0x02)
                    packet = new RequestServerLogin();
                else
                    debugOpcode(opcode, state);
                break;
        }
        return packet;
    }

    private static void debugOpcode(int opcode, LoginClientState state) {
        LOGGER.warn("Unknown Opcode: " + opcode + " for state: " + state.name());
    }
}