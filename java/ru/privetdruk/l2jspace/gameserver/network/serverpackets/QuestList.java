package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.scripting.QuestState;

import java.util.List;

public class QuestList extends L2GameServerPacket {
    private final List<QuestState> questStates;

    public QuestList(Player player) {
        questStates = player.getQuestList().getAllQuests(true);
    }

    @Override
    protected final void writeImpl() {
        writeC(0x80);
        writeH(questStates.size());

        for (QuestState questState : questStates) {
            writeD(questState.getQuest().getQuestId());
            writeD(questState.getFlags());
        }
    }
}