package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.enums.FloodProtector;
import ru.privetdruk.l2jspace.gameserver.enums.IntentionType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SocialAction;

public class RequestSocialAction extends L2GameClientPacket {
    private int _actionId;

    @Override
    protected void readImpl() {
        _actionId = readD();
    }

    @Override
    protected void runImpl() {
        if (!getClient().performAction(FloodProtector.SOCIAL))
            return;

        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        if (player.isFishing()) {
            player.sendPacket(SystemMessageId.CANNOT_DO_WHILE_FISHING_3);
            return;
        }

        if (_actionId < 2 || _actionId > 13)
            return;

        if (player.isOperating() || player.getActiveRequester() != null || player.isAlikeDead() || player.getAI().getCurrentIntention().getType() != IntentionType.IDLE)
            return;

        player.broadcastPacket(new SocialAction(player, _actionId));
    }
}