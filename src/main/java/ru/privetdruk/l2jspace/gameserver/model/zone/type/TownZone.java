package ru.privetdruk.l2jspace.gameserver.model.zone.type;

import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.enums.ZoneId;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.zone.type.subtype.SpawnZoneType;

/**
 * A zone extending {@link SpawnZoneType}, used by towns. A town zone is generally associated to a castle for taxes.
 */
public class TownZone extends SpawnZoneType {
    private int _townId;
    private int _castleId;

    private boolean _isPeaceZone = true;

    public TownZone(int id) {
        super(id);
    }

    @Override
    public void setParameter(String name, String value) {
        if (name.equals("townId"))
            _townId = Integer.parseInt(value);
        else if (name.equals("castleId"))
            _castleId = Integer.parseInt(value);
        else if (name.equals("isPeaceZone"))
            _isPeaceZone = Boolean.parseBoolean(value);
        else
            super.setParameter(name, value);
    }

    @Override
    protected void onEnter(Creature character) {
        if (Config.ZONE_TOWN == 1 && character instanceof Player && ((Player) character).getSiegeState() != 0)
            return;

        if (_isPeaceZone && Config.ZONE_TOWN != 2)
            character.setInsideZone(ZoneId.PEACE, true);

        character.setInsideZone(ZoneId.TOWN, true);
    }

    @Override
    protected void onExit(Creature character) {
        if (_isPeaceZone)
            character.setInsideZone(ZoneId.PEACE, false);

        character.setInsideZone(ZoneId.TOWN, false);
    }

    public int getTownId() {
        return _townId;
    }

    public final int getCastleId() {
        return _castleId;
    }

    public final boolean isPeaceZone() {
        return _isPeaceZone;
    }
}