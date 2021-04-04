package ru.privetdruk.l2jspace.gameserver.model.zone.type;

import ru.privetdruk.l2jspace.gameserver.enums.ZoneId;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.zone.type.subtype.ZoneType;

/**
 * A zone extending {@link ZoneType}, used for quests and custom scripts.
 */
public class ScriptZone extends ZoneType {
    public ScriptZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(Creature character) {
        character.setInsideZone(ZoneId.SCRIPT, true);
    }

    @Override
    protected void onExit(Creature character) {
        character.setInsideZone(ZoneId.SCRIPT, false);
    }
}