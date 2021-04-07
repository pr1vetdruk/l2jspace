package ru.privetdruk.l2jspace.gameserver.handler.usercommandhandlers;

import ru.privetdruk.l2jspace.gameserver.handler.IUserCommandHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.taskmanager.GameTimeTaskManager;

public class Time implements IUserCommandHandler {
    private static final int[] COMMAND_IDS =
            {
                    77
            };

    @Override
    public void useUserCommand(int id, Player player) {
        final int hour = GameTimeTaskManager.getInstance().getGameHour();
        final int minute = GameTimeTaskManager.getInstance().getGameMinute();

        final String min = ((minute < 10) ? "0" : "") + minute;

        player.sendPacket(SystemMessage.getSystemMessage((GameTimeTaskManager.getInstance().isNight()) ? SystemMessageId.TIME_S1_S2_IN_THE_NIGHT : SystemMessageId.TIME_S1_S2_IN_THE_DAY).addNumber(hour).addString(min));
    }

    @Override
    public int[] getUserCommandList() {
        return COMMAND_IDS;
    }
}