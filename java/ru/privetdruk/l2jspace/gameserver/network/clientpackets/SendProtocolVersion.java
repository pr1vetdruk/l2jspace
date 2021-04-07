package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.network.serverpackets.L2GameServerPacket;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.VersionCheck;

public final class SendProtocolVersion extends L2GameClientPacket {
    private int _version;

    @Override
    protected void readImpl() {
        _version = readD();
    }

    @Override
    protected void runImpl() {
        switch (_version) {
            case 737:
            case 740:
            case 744:
            case 746:
                getClient().sendPacket(new VersionCheck(getClient().enableCrypt()));
                break;

            default:
                getClient().close((L2GameServerPacket) null);
                break;
        }
    }
}