package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.scripting.QuestState;

public class RequestTutorialPassCmdToServer extends L2GameClientPacket {
    private String _bypass;

    @Override
    protected void readImpl() {
        _bypass = readS();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        final QuestState qs = player.getQuestList().getQuestState("Tutorial");
        if (qs != null)
            qs.getQuest().notifyEvent(_bypass, null, player);
    }
}