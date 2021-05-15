package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.data.manager.FestivalOfDarknessManager;
import ru.privetdruk.l2jspace.gameserver.enums.ZoneId;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.GameClient;
import ru.privetdruk.l2jspace.gameserver.network.GameClient.GameClientState;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.CharSelectInfo;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.RestartResponse;
import ru.privetdruk.l2jspace.gameserver.taskmanager.AttackStanceTaskManager;

public final class RequestRestart extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getPlayer();

        if (player == null) {
            return;
        }

        if (player.getActiveEnchantItem() != null || player.isLocked()) {
            sendPacket(RestartResponse.valueOf(false));
            return;
        }

        if (player.isInsideZone(ZoneId.NO_RESTART)) {
            player.sendPacket(SystemMessageId.NO_RESTART_HERE);
            sendPacket(RestartResponse.valueOf(false));
            return;
        }

        if (AttackStanceTaskManager.getInstance().isInAttackStance(player) && !player.isGM()) {
            player.sendPacket(SystemMessageId.CANT_RESTART_WHILE_FIGHTING);
            sendPacket(RestartResponse.valueOf(false));
            return;
        }

        if (player.isFestivalParticipant() && FestivalOfDarknessManager.getInstance().isFestivalInitialized()) {
            player.sendPacket(SystemMessageId.NO_RESTART_HERE);
            sendPacket(RestartResponse.valueOf(false));
            return;
        }

        if (player.isEventPlayer()) {
            player.sendMessage("Вы не можете перезайти во время участия в ивенте.");
            sendPacket(RestartResponse.valueOf(false));
            return;
        }

        player.removeFromBossZone();

        GameClient client = getClient();

        // detach the client from the char so that the connection isnt closed in the deleteMe
        player.setClient(null);

        // removing player from the world
        player.deleteMe();

        client.setPlayer(null);
        client.setState(GameClientState.AUTHED);

        sendPacket(RestartResponse.valueOf(true));

        // send char list
        CharSelectInfo cl = new CharSelectInfo(client.getAccountName(), client.getSessionId().playOkID1);
        sendPacket(cl);
        client.setCharSelectSlot(cl.getCharacterSlots());
    }
}