package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.communitybbs.CommunityBoard;

public final class RequestShowBoard extends L2GameClientPacket {
    @Override
    protected void readImpl() {
        readD(); // Not used for security reason.
    }

    @Override
    protected void runImpl() {
        CommunityBoard.getInstance().handleCommands(getClient(), Config.BBS_DEFAULT);
    }
}