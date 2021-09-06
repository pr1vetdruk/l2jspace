package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.enums.StatusType;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.holder.IntIntHolder;

import java.util.ArrayList;
import java.util.List;

public class StatusUpdate extends L2GameServerPacket {
    private final int _objectId;
    private final List<IntIntHolder> _attributes;

    public StatusUpdate(WorldObject object) {
        _attributes = new ArrayList<>();
        _objectId = object.getObjectId();
    }

    public void addAttribute(StatusType type, int level) {
        _attributes.add(new IntIntHolder(type.getId(), level));
    }

    @Override
    protected final void writeImpl() {
        writeC(0x0e);
        writeD(_objectId);
        writeD(_attributes.size());

        for (IntIntHolder temp : _attributes) {
            writeD(temp.getId());
            writeD(temp.getValue());
        }
    }
}