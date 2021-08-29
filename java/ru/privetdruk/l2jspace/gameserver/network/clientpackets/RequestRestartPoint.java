package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.common.pool.ThreadPool;
import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.data.manager.CastleManager;
import ru.privetdruk.l2jspace.gameserver.data.manager.ClanHallManager;
import ru.privetdruk.l2jspace.gameserver.data.xml.MapRegionData;
import ru.privetdruk.l2jspace.gameserver.data.xml.MapRegionData.TeleportType;
import ru.privetdruk.l2jspace.gameserver.enums.SiegeSide;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.clanhall.ClanHall;
import ru.privetdruk.l2jspace.gameserver.model.clanhall.ClanHallFunction;
import ru.privetdruk.l2jspace.gameserver.model.entity.Siege;
import ru.privetdruk.l2jspace.gameserver.model.location.Location;
import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;

public final class RequestRestartPoint extends L2GameClientPacket {
    protected static final Location JAIL_LOCATION = new Location(-114356, -249645, -2984);

    protected int requestType;

    @Override
    protected void readImpl() {
        requestType = readD();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getPlayer();

        if (player == null || !player.isDead()) {
            return;
        }

        if (player.isEventPlayer()) {
            player.sendMessage("Вы не можете перезайти во время участия в ивенте.");
            return;
        }

        // TODO Needed? Possible?
        if (player.isFakeDeath()) {
            player.stopFakeDeath(true);
            return;
        }

        // Schedule a respawn delay if player is part of a clan registered in an active siege.
        if (player.getClan() != null) {
            Siege siege = CastleManager.getInstance().getActiveSiege(player);

            if (siege != null && siege.checkSide(player.getClan(), SiegeSide.ATTACKER)) {
                ThreadPool.schedule(() -> portPlayer(player), Config.ATTACKERS_RESPAWN_DELAY);
                return;
            }
        }

        portPlayer(player);
    }

    /**
     * Teleport the {@link Player} to the associated {@link Location}, based on _requestType.
     *
     * @param player : The player set as parameter.
     */
    private void portPlayer(Player player) {
        Clan clan = player.getClan();

        Location loc;

        // Enforce type.
        if (player.isInJail()) {
            requestType = 27;
        } else if (player.isFestivalParticipant()) {
            requestType = 4;
        }

        // To clanhall.
        if (requestType == 1) {
            if (clan == null || !clan.hasClanHall()) {
                return;
            }

            loc = MapRegionData.getInstance().getLocationToTeleport(player, TeleportType.CLAN_HALL);

            ClanHall ch = ClanHallManager.getInstance().getClanHallByOwner(clan);

            if (ch != null) {
                ClanHallFunction function = ch.getFunction(ClanHall.FUNC_RESTORE_EXP);

                if (function != null) {
                    player.restoreExp(function.getLvl());
                }
            }
        }
        // To castle.
        else if (requestType == 2) {
            Siege siege = CastleManager.getInstance().getActiveSiege(player);

            if (siege != null) {
                SiegeSide side = siege.getSide(clan);

                if (side == SiegeSide.DEFENDER || side == SiegeSide.OWNER) {
                    loc = MapRegionData.getInstance().getLocationToTeleport(player, TeleportType.CASTLE);
                } else if (side == SiegeSide.ATTACKER) {
                    loc = MapRegionData.getInstance().getLocationToTeleport(player, TeleportType.TOWN);
                } else {
                    return;
                }
            } else {
                if (clan == null || !clan.hasCastle()) {
                    return;
                }

                loc = MapRegionData.getInstance().getLocationToTeleport(player, TeleportType.CASTLE);
            }
        }
        // To siege flag.
        else if (requestType == 3) {
            loc = MapRegionData.getInstance().getLocationToTeleport(player, TeleportType.SIEGE_FLAG);
        }
        // Fixed.
        else if (requestType == 4) {
            if (!player.isGM() && !player.isFestivalParticipant()) {
                return;
            }

            loc = player.getPosition();
        }
        // To jail.
        else if (requestType == 27) {
            if (!player.isInJail()) {
                return;
            }

            loc = JAIL_LOCATION;
        }
        // Nothing has been found, use regular "To town" behavior.
        else {
            loc = MapRegionData.getInstance().getLocationToTeleport(player, TeleportType.TOWN);
        }

        player.setIsIn7sDungeon(false);

        if (player.isDead()) {
            player.doRevive();
        }

        player.teleportTo(loc, 20);
    }
}