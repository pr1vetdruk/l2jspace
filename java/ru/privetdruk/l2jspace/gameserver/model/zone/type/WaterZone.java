package ru.privetdruk.l2jspace.gameserver.model.zone.type;

import ru.privetdruk.l2jspace.gameserver.enums.ZoneId;
import ru.privetdruk.l2jspace.gameserver.enums.actors.MoveType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.zone.type.subtype.ZoneType;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.AbstractNpcInfo.NpcInfo;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ServerObjectInfo;

/**
 * A zone extending {@link ZoneType}, used for the water behavior. {@link Player}s can drown if they stay too long below water line.
 */
public class WaterZone extends ZoneType {
    public WaterZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(Creature character) {
        boolean water = character.isInsideZone(ZoneId.WATER);
        character.setInsideZone(ZoneId.WATER, true);

        // Check if character was already in water, skip.
        if (water)
            return;

        character.getMove().addMoveType(MoveType.SWIM);

        if (character instanceof Player)
            ((Player) character).broadcastUserInfo();
        else if (character instanceof Npc) {
            for (Player player : character.getKnownType(Player.class)) {
                if (character.getStatus().getMoveSpeed() == 0)
                    player.sendPacket(new ServerObjectInfo((Npc) character, player));
                else
                    player.sendPacket(new NpcInfo((Npc) character, player));
            }
        }
    }

    @Override
    protected void onExit(Creature character) {
        character.setInsideZone(ZoneId.WATER, false);

        // Still in water, skip.
        if (character.isInsideZone(ZoneId.WATER))
            return;

        character.getMove().removeMoveType(MoveType.SWIM);

        if (character instanceof Player)
            ((Player) character).broadcastUserInfo();
        else if (character instanceof Npc) {
            for (Player player : character.getKnownType(Player.class)) {
                if (character.getStatus().getMoveSpeed() == 0)
                    player.sendPacket(new ServerObjectInfo((Npc) character, player));
                else
                    player.sendPacket(new NpcInfo((Npc) character, player));
            }
        }
    }

    public int getWaterZ() {
        return getZone().getHighZ();
    }
}