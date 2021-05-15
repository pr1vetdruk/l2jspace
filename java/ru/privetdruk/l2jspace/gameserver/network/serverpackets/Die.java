package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.data.manager.CastleManager;
import ru.privetdruk.l2jspace.gameserver.enums.SiegeSide;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Monster;
import ru.privetdruk.l2jspace.gameserver.model.entity.Siege;
import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;

public class Die extends L2GameServerPacket {
    private final Creature creature;
    private final int objectId;
    private final boolean fake;

    private boolean sweepable;
    private boolean allowFixedRes;
    private Clan clan;
    private boolean isTeleportToVillage;

    public Die(Creature creature) {
        this.creature = creature;
        objectId = creature.getObjectId();
        fake = !creature.isDead();

        if (creature instanceof Player) {
            Player player = (Player) creature;
            allowFixedRes = player.getAccessLevel().allowFixedRes();
            clan = player.getClan();
            isTeleportToVillage = !player.isEventPlayer();
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
        writeD(isTeleportToVillage ? 0x01 : 0); // to nearest village

        if (isTeleportToVillage && clan != null) {
            SiegeSide side = null;

            Siege siege = CastleManager.getInstance().getActiveSiege(creature);

            if (siege != null) {
                side = siege.getSide(clan);
            }

            writeD((clan.hasClanHall()) ? 0x01 : 0x00); // to clanhall
            writeD((clan.hasCastle() || side == SiegeSide.OWNER || side == SiegeSide.DEFENDER) ? 0x01 : 0x00); // to castle
            writeD((side == SiegeSide.ATTACKER && clan.getFlag() != null) ? 0x01 : 0x00); // to siege HQ
        } else {
            writeD(0x00); // to clanhall
            writeD(0x00); // to castle
            writeD(0x00); // to siege HQ
        }

        writeD((sweepable) ? 0x01 : 0x00); // sweepable (blue glow)
        writeD((allowFixedRes) ? 0x01 : 0x00); // FIXED
    }
}