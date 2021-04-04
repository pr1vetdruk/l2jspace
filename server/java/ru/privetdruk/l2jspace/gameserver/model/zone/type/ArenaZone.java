package ru.privetdruk.l2jspace.gameserver.model.zone.type;

import ru.privetdruk.l2jspace.gameserver.enums.ZoneId;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.zone.type.subtype.ZoneType;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;

/**
 * A zone extending {@link ZoneType}, where summoning is forbidden. The place is considered a pvp zone (no flag, no karma). It is used for arenas.
 */
public class ArenaZone extends ZoneType {
    public ArenaZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(Creature character) {
        if (character instanceof Player)
            ((Player) character).sendPacket(SystemMessageId.ENTERED_COMBAT_ZONE);

        character.setInsideZone(ZoneId.PVP, true);
        character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, true);
    }

    @Override
    protected void onExit(Creature character) {
        character.setInsideZone(ZoneId.PVP, false);
        character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false);

        if (character instanceof Player)
            ((Player) character).sendPacket(SystemMessageId.LEFT_COMBAT_ZONE);
    }
}