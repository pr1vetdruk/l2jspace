package ru.privetdruk.l2jspace.gameserver.network.gameserverpackets;

import java.util.ArrayList;
import java.util.List;

import ru.privetdruk.l2jspace.common.network.AttributeType;

import ru.privetdruk.l2jspace.gameserver.model.holder.IntIntHolder;

public class ServerStatus extends GameServerBasePacket {
    private final List<IntIntHolder> _attributes;

    private static final int ON = 0x01;
    private static final int OFF = 0x00;

    public ServerStatus() {
        _attributes = new ArrayList<>();
    }

    public void addAttribute(AttributeType type, int value) {
        _attributes.add(new IntIntHolder(type.getId(), value));
    }

    public void addAttribute(AttributeType type, boolean onOrOff) {
        addAttribute(type, (onOrOff) ? ON : OFF);
    }

    @Override
    public byte[] getContent() {
        writeC(0x06);
        writeD(_attributes.size());
        for (IntIntHolder temp : _attributes) {
            writeD(temp.getId());
            writeD(temp.getValue());
        }

        return getBytes();
    }
}