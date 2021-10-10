package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;

public class FinishRotation extends L2GameServerPacket {
    private final int _heading;
    private final int _charObjId;

    public FinishRotation(Creature cha) {
        _charObjId = cha.getId();
        _heading = cha.getHeading();
    }

    @Override
    protected final void writeImpl() {
        writeC(0x63);
        writeD(_charObjId);
        writeD(_heading);
    }
}