package ru.privetdruk.l2jspace.gameserver.model.zone.type;

import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.zone.type.subtype.ZoneType;

/**
 * A zone extending {@link ZoneType}, used for fish points.
 */
public class FishingZone extends ZoneType {
    public FishingZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(Creature character) {
    }

    @Override
    protected void onExit(Creature character) {
    }

    public int getWaterZ() {
        return getZone().getHighZ();
    }
}