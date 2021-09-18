package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.model.World;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.Summon;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ActionFailed;

public final class AttackRequest extends L2GameClientPacket {
    private int objectId;
    private boolean isShiftAction;

    @Override
    protected void readImpl() {
        objectId = readD();
        readD(); // originX
        readD(); // originY
        readD(); // originZ
        isShiftAction = readC() != 0;
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getPlayer();
        if (player == null) {
            return;
        }

        if (player.isOutOfControl()) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (player.isInObserverMode()) {
            player.sendPacket(SystemMessageId.OBSERVERS_CANNOT_PARTICIPATE);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // avoid using expensive operations if not needed
        WorldObject target = player.getTargetId() == objectId ? player.getTarget() : World.getInstance().getObject(objectId);

        if (target == null) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // No attacks to same team in Event
        if (isTargetTeammate(player, target)) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // (player.getTarget() == target) -> This happens when you control + click a target without having had it selected beforehand. Behaves as the Action packet and will NOT trigger an attack.
        target.onAction(player, (player.getTarget() == target), isShiftAction);
    }

    private boolean isTargetTeammate(Player player, WorldObject target) {
        if (player.isEventPlayer()) {
            Player targetPlayer = target.getActingPlayer();

            if (targetPlayer.isEventPlayer()) {
                return player.getEventPlayer().getTeamSettings().equals(targetPlayer.getEventPlayer().getTeamSettings());
            }
        }

        return false;
    }
}