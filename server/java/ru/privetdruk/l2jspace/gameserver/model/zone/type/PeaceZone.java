package ru.privetdruk.l2jspace.gameserver.model.zone.type;

import ru.privetdruk.l2jspace.gameserver.enums.ZoneId;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.zone.type.subtype.ZoneType;

/**
 * A zone extending {@link ZoneType}, notably used for peace behavior (pvp related).
 */
public class PeaceZone extends ZoneType {
    public PeaceZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(Creature character) {
        character.setInsideZone(ZoneId.PEACE, true);
    }

    @Override
    protected void onExit(Creature character) {
        character.setInsideZone(ZoneId.PEACE, false);
    }
}