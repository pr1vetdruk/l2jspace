package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.common.pool.ConnectionPool;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.FriendAddRequestResult;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.L2Friend;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;

public final class RequestAnswerFriendInvite extends L2GameClientPacket {
    private static final String ADD_FRIEND = "INSERT INTO character_friends (char_id, friend_id) VALUES (?,?), (?,?)";

    private int _response;

    @Override
    protected void readImpl() {
        _response = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        final Player requestor = player.getActiveRequester();
        if (requestor == null)
            return;

        if (_response == 1) {
            requestor.sendPacket(SystemMessageId.YOU_HAVE_SUCCEEDED_INVITING_FRIEND);

            // Player added to your friendlist
            requestor.sendPacket(FriendAddRequestResult.STATIC_ACCEPT);
            requestor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_ADDED_TO_FRIENDS).addCharName(player));
            requestor.getFriendList().add(player.getObjectId());
            requestor.sendPacket(new L2Friend(player, 1));

            // has joined as friend.
            player.sendPacket(FriendAddRequestResult.STATIC_ACCEPT);
            player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_JOINED_AS_FRIEND).addCharName(requestor));
            player.getFriendList().add(requestor.getObjectId());
            player.sendPacket(new L2Friend(requestor, 1));

            try (Connection con = ConnectionPool.getConnection();
                 PreparedStatement ps = con.prepareStatement(ADD_FRIEND)) {
                ps.setInt(1, requestor.getObjectId());
                ps.setInt(2, player.getObjectId());
                ps.setInt(3, player.getObjectId());
                ps.setInt(4, requestor.getObjectId());
                ps.execute();
            } catch (Exception e) {
                LOGGER.error("Couldn't add friendId {} for {}.", e, player.getObjectId(), requestor.toString());
            }
        } else
            requestor.sendPacket(FriendAddRequestResult.STATIC_FAIL);

        player.setActiveRequester(null);
        requestor.onTransactionResponse();
    }
}