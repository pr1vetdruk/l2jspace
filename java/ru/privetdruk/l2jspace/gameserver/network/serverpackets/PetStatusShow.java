package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Summon;

public class PetStatusShow extends L2GameServerPacket {
    private final int _summonType;

    public PetStatusShow(Summon summon) {
        _summonType = summon.getSummonType();
    }

    @Override
    protected final void writeImpl() {
        writeC(0xB0);
        writeD(_summonType);
    }
}