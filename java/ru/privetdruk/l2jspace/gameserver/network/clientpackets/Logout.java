package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.data.manager.FestivalOfDarknessManager;
import ru.privetdruk.l2jspace.gameserver.enums.ZoneId;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ActionFailed;
import ru.privetdruk.l2jspace.gameserver.taskmanager.AttackStanceTaskManager;

public final class Logout extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        if (player.getActiveEnchantItem() != null || player.isLocked()) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (player.isInsideZone(ZoneId.NO_RESTART)) {
            player.sendPacket(SystemMessageId.NO_LOGOUT_HERE);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (AttackStanceTaskManager.getInstance().isInAttackStance(player) && !player.isGM()) {
            player.sendPacket(SystemMessageId.CANT_LOGOUT_WHILE_FIGHTING);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (player.isFestivalParticipant() && FestivalOfDarknessManager.getInstance().isFestivalInitialized()) {
            player.sendPacket(SystemMessageId.NO_LOGOUT_HERE);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        player.removeFromBossZone();
        player.logout(true);
    }
}