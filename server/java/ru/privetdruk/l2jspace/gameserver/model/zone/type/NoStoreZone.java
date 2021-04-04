package ru.privetdruk.l2jspace.gameserver.model.zone.type;

import ru.privetdruk.l2jspace.gameserver.enums.ZoneId;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.zone.type.subtype.ZoneType;

/**
 * A zone extending {@link ZoneType} where store isn't allowed.
 */
public class NoStoreZone extends ZoneType {
    public NoStoreZone(final int id) {
        super(id);
    }

    @Override
    protected void onEnter(final Creature character) {
        if (character instanceof Player)
            character.setInsideZone(ZoneId.NO_STORE, true);
    }

    @Override
    protected void onExit(final Creature character) {
        if (character instanceof Player)
            character.setInsideZone(ZoneId.NO_STORE, false);
    }
}