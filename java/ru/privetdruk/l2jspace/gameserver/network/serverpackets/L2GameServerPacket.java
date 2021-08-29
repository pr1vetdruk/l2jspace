package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.common.logging.CLogger;
import ru.privetdruk.l2jspace.common.mmocore.SendablePacket;
import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.network.GameClient;

public abstract class L2GameServerPacket extends SendablePacket<GameClient> {
    protected static final CLogger LOGGER = new CLogger(L2GameServerPacket.class.getName());

    protected abstract void writeImpl();

    @Override
    protected void write() {
        if (Config.PACKET_HANDLER_DEBUG)
            LOGGER.info(getType());

        try {
            writeImpl();
        } catch (Exception e) {
            LOGGER.error("Failed writing {} for {}. ", e, getType(), getClient().toString());
        }
    }

    public void runImpl() {
    }

    public String getType() {
        return "[S] " + getClass().getSimpleName();
    }
}