package ru.privetdruk.l2jspace.gameserver.handler.usercommandhandlers;

import ru.privetdruk.l2jspace.gameserver.handler.IUserCommandHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.group.Party;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;

public class PartyInfo implements IUserCommandHandler {
    private static final int[] COMMAND_IDS =
            {
                    81
            };

    @Override
    public void useUserCommand(int id, Player player) {
        final Party party = player.getParty();
        if (party == null)
            return;

        player.sendPacket(SystemMessageId.PARTY_INFORMATION);
        player.sendPacket(party.getLootRule().getMessageId());
        player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PARTY_LEADER_S1).addString(party.getLeader().getName()));
        player.sendMessage("Members: " + party.getMembersCount() + "/9");
        player.sendPacket(SystemMessageId.FRIEND_LIST_FOOTER);
    }

    @Override
    public int[] getUserCommandList() {
        return COMMAND_IDS;
    }
}