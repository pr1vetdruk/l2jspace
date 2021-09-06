package ru.privetdruk.l2jspace.gameserver.handler.usercommandhandlers;

import ru.privetdruk.l2jspace.gameserver.enums.ZoneId;
import ru.privetdruk.l2jspace.gameserver.handler.IUserCommandHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.PlaySound;

public class Escape implements IUserCommandHandler {
    private static final int[] COMMAND_IDS =
            {
                    52
            };

    @Override
    public void useUserCommand(int id, Player player) {
        if (player.isInOlympiadMode() || player.isInObserverMode() || player.isFestivalParticipant() || player.isInJail() || player.isInsideZone(ZoneId.BOSS)) {
            player.sendPacket(SystemMessageId.NO_UNSTUCK_PLEASE_SEND_PETITION);
            return;
        }

        // Official timer 5 minutes, for GM 1 second
        if (player.isGM())
            player.getAI().tryToCast(player, 2100, 1);
        else {
            if (player.isEventPlayer()) {
                return;
            }

            player.sendPacket(new PlaySound("systemmsg_e.809"));
            player.sendPacket(SystemMessageId.STUCK_TRANSPORT_IN_FIVE_MINUTES);

            player.getAI().tryToCast(player, 2099, 1);
        }
    }

    @Override
    public int[] getUserCommandList() {
        return COMMAND_IDS;
    }
}