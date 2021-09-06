package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.scripting.QuestState;

import java.util.List;

public class GMViewQuestList extends L2GameServerPacket {
    private final List<QuestState> questStates;
    private final Player player;

    public GMViewQuestList(Player player) {
        questStates = player.getQuestList().getAllQuests(true);
        this.player = player;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x93);

        writeS(player.getName());
        writeH(questStates.size());

        for (QuestState questState : questStates) {
            writeD(questState.getQuest().getQuestId());
            writeD(questState.getFlags());
        }
    }
}