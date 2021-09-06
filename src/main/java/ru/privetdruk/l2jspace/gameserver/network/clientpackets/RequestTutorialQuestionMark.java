package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.scripting.QuestState;

public class RequestTutorialQuestionMark extends L2GameClientPacket {
    private int _number;

    @Override
    protected void readImpl() {
        _number = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        final QuestState qs = player.getQuestList().getQuestState("Tutorial");
        if (qs != null)
            qs.getQuest().notifyEvent("QM" + _number + "", null, player);
    }
}