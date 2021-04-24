package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;

public final class DlgAnswer extends L2GameClientPacket {
    private int _messageId;
    private int _answer;
    private int _requesterId;

    @Override
    protected void readImpl() {
        _messageId = readD();
        _answer = readD();
        _requesterId = readD();
    }

    @Override
    public void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        if (_messageId == SystemMessageId.RESSURECTION_REQUEST_BY_S1.getId() || _messageId == SystemMessageId.DO_YOU_WANT_TO_BE_RESTORED.getId())
            player.reviveAnswer(_answer);
        else if (_messageId == SystemMessageId.S1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT.getId())
            player.teleportAnswer(_answer, _requesterId);
        else if (_messageId == 1983 && Config.ALLOW_WEDDING)
            player.engageAnswer(_answer);
        else if (_messageId == SystemMessageId.WOULD_YOU_LIKE_TO_OPEN_THE_GATE.getId())
            player.activateGate(_answer, 1);
        else if (_messageId == SystemMessageId.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE.getId())
            player.activateGate(_answer, 0);
    }
}