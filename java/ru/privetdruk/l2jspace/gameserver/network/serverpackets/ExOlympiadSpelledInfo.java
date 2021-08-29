package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.holder.EffectHolder;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

import java.util.ArrayList;
import java.util.List;

public class ExOlympiadSpelledInfo extends L2GameServerPacket {
    private final int _objectId;
    private final List<EffectHolder> _effects = new ArrayList<>();

    public ExOlympiadSpelledInfo(Player player) {
        _objectId = player.getObjectId();
    }

    @Override
    protected final void writeImpl() {
        writeC(0xfe);
        writeH(0x2a);

        writeD(_objectId);

        writeD(_effects.size());
        for (EffectHolder holder : _effects) {
            writeD(holder.getId());
            writeH(holder.getValue());
            writeD(holder.getDuration() / 1000);
        }
    }

    public void addEffect(L2Skill skill, int duration) {
        _effects.add(new EffectHolder(skill, duration));
    }
}