package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Pet;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Servitor;
import ru.privetdruk.l2jspace.gameserver.model.holder.EffectHolder;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

import java.util.ArrayList;
import java.util.List;

public class PartySpelled extends L2GameServerPacket {
    private final int _type;
    private final int _objectId;
    private final List<EffectHolder> _effects = new ArrayList<>();

    public PartySpelled(Creature creature) {
        _type = creature instanceof Servitor ? 2 : creature instanceof Pet ? 1 : 0;
        _objectId = creature.getObjectId();
    }

    @Override
    protected final void writeImpl() {
        writeC(0xee);

        writeD(_type);
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