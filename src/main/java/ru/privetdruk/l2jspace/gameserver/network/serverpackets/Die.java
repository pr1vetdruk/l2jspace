package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;
import ru.privetdruk.l2jspace.gameserver.data.manager.CastleManager;
import ru.privetdruk.l2jspace.gameserver.data.manager.ClanHallManager;
import ru.privetdruk.l2jspace.gameserver.enums.SiegeSide;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Monster;
import ru.privetdruk.l2jspace.gameserver.model.entity.ClanHallSiege;
import ru.privetdruk.l2jspace.gameserver.model.entity.Siege;
import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;

public class Die extends L2GameServerPacket {
    private final Creature creature;
    private final int objectId;
    private final boolean fake;

    private boolean sweepable;
    private boolean allowFixedRes;
    private Clan clan;
    private boolean teleportToVillage = true;

    public Die(Creature creature) {
        this.creature = creature;
        objectId = creature.getId();
        fake = !creature.isDead();

        if (creature instanceof Player) {
            Player player = (Player) creature;
            allowFixedRes = player.getAccessLevel().allowFixedRes();
            clan = player.getClan();

            if (player.isEventPlayer()) {
                teleportToVillage = EventEngine.findActive().isAllowedTeleportAfterDeath();
            }
        } else if (creature instanceof Monster) {
            sweepable = ((Monster) creature).getSpoilState().isSweepable();
        }
    }

    @Override
    protected final void writeImpl() {
        if (fake) {
            return;
        }

        writeC(0x06);
        writeD(objectId);
        writeD(teleportToVillage ? 0x01 : 0); // to nearest village

        if (teleportToVillage && clan != null) {
            final Siege siege = CastleManager.getInstance().getActiveSiege(creature);
            final ClanHallSiege chs = ClanHallManager.getInstance().getActiveSiege(creature);

            // Check first if an active Siege is under process.
            if (siege != null) {
                final SiegeSide side = siege.getSide(clan);

                writeD((clan.hasClanHall()) ? 0x01 : 0x00); // to clanhall
                writeD((clan.hasCastle() || side == SiegeSide.OWNER || side == SiegeSide.DEFENDER) ? 0x01 : 0x00); // to castle
                writeD((side == SiegeSide.ATTACKER && clan.getFlag() != null) ? 0x01 : 0x00); // to siege HQ
            }
            // If no Siege, check ClanHallSiege.
            else if (chs != null) {
                writeD((clan.hasClanHall()) ? 0x01 : 0x00); // to clanhall
                writeD((clan.hasCastle()) ? 0x01 : 0x00); // to castle
                writeD((chs.checkSide(clan, SiegeSide.ATTACKER) && clan.getFlag() != null) ? 0x01 : 0x00); // to siege HQ
            }
            // We're in peace mode, activate generic teleports.
            else {
                writeD((clan.hasClanHall()) ? 0x01 : 0x00); // to clanhall
                writeD((clan.hasCastle()) ? 0x01 : 0x00); // to castle
                writeD(0x00); // to siege HQ
            }
        } else {
            writeD(0x00); // to clanhall
            writeD(0x00); // to castle
            writeD(0x00); // to siege HQ
        }

        writeD((sweepable) ? 0x01 : 0x00); // sweepable (blue glow)
        writeD((allowFixedRes) ? 0x01 : 0x00); // FIXED
    }
}