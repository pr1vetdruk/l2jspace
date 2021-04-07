package ru.privetdruk.l2jspace.gameserver.model.zone.type;

import ru.privetdruk.l2jspace.gameserver.data.manager.CastleManager;
import ru.privetdruk.l2jspace.gameserver.enums.SpawnType;
import ru.privetdruk.l2jspace.gameserver.enums.ZoneId;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.entity.Castle;
import ru.privetdruk.l2jspace.gameserver.model.zone.type.subtype.ResidenceZoneType;

/**
 * A zone extending {@link ResidenceZoneType} which handles following spawns type :
 * <ul>
 * <li>Generic spawn locs : owner_restart_point_list (spawns used on siege, to respawn on mass gatekeeper room.</li>
 * <li>Chaotic spawn locs : banish_point_list (spawns used to banish players on regular owner maintenance).</li>
 * </ul>
 */
public class CastleZone extends ResidenceZoneType {
    public CastleZone(int id) {
        super(id);
    }

    @Override
    public void banishForeigners(int clanId) {
        // Retrieve associated castle.
        final Castle castle = CastleManager.getInstance().getCastleById(getResidenceId());
        if (castle == null)
            return;

        for (Player player : getKnownTypeInside(Player.class)) {
            if (player.getClanId() == clanId)
                continue;

            player.teleportTo(castle.getRndSpawn(SpawnType.BANISH), 20);
        }
    }

    @Override
    public void setParameter(String name, String value) {
        if (name.equals("castleId"))
            setResidenceId(Integer.parseInt(value));
        else
            super.setParameter(name, value);
    }

    @Override
    protected void onEnter(Creature character) {
        character.setInsideZone(ZoneId.CASTLE, true);
    }

    @Override
    protected void onExit(Creature character) {
        character.setInsideZone(ZoneId.CASTLE, false);
    }
}