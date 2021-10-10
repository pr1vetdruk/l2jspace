package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.model.World;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SendTradeDone;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;

public final class AnswerTradeRequest extends L2GameClientPacket {
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

        if (!player.getAccessLevel().allowTransaction()) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }

        final Player partner = player.getActiveRequester();
        if (partner == null || World.getInstance().getPlayer(partner.getId()) == null) {
            // Trade partner not found, cancel trade
            player.sendPacket(SendTradeDone.FAIL_STATIC_PACKET);
            player.sendPacket(SystemMessageId.TARGET_IS_NOT_FOUND_IN_THE_GAME);
            player.setActiveRequester(null);
            return;
        }

        if (_response == 1 && !partner.isRequestExpired())
            player.startTrade(partner);
        else
            partner.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DENIED_TRADE_REQUEST).addCharName(player));

        // Clears requesting status
        player.setActiveRequester(null);
        partner.onTransactionResponse();
    }
}