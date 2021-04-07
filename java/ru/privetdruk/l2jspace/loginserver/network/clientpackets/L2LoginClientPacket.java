package ru.privetdruk.l2jspace.loginserver.network.clientpackets;

import ru.privetdruk.l2jspace.common.logging.CLogger;
import ru.privetdruk.l2jspace.common.mmocore.ReceivablePacket;

import ru.privetdruk.l2jspace.loginserver.network.LoginClient;

public abstract class L2LoginClientPacket extends ReceivablePacket<LoginClient> {
    protected static final CLogger LOGGER = new CLogger(L2LoginClientPacket.class.getName());

    @Override
    protected final boolean read() {
        try {
            return readImpl();
        } catch (Exception e) {
            LOGGER.error("Failed reading {}. ", e, getClass().getSimpleName());
            return false;
        }
    }

    protected abstract boolean readImpl();
}
