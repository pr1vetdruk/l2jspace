package ru.privetdruk.l2jspace.gameserver.model.zone.type;

import ru.privetdruk.l2jspace.gameserver.enums.ZoneId;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.zone.type.subtype.ZoneType;

/**
 * A zone extending {@link ZoneType}, used for jail behavior. It is impossible to summon friends and use shops inside it.
 */
public class JailZone extends ZoneType {
    public JailZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(Creature character) {
        if (character instanceof Player) {
            character.setInsideZone(ZoneId.JAIL, true);
            character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, true);
            character.setInsideZone(ZoneId.NO_STORE, true);
        }
    }

    @Override
    protected void onExit(Creature character) {
        if (character instanceof Player) {
            character.setInsideZone(ZoneId.JAIL, false);
            character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false);
            character.setInsideZone(ZoneId.NO_STORE, false);
        }
    }
}