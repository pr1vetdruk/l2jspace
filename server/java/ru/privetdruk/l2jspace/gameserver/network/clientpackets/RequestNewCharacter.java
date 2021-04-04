package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.network.serverpackets.NewCharacterSuccess;

public final class RequestNewCharacter extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        sendPacket(NewCharacterSuccess.STATIC_PACKET);
    }
}