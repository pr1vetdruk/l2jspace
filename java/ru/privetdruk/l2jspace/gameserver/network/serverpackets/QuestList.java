package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import java.util.List;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.scripting.Quest;
import ru.privetdruk.l2jspace.gameserver.scripting.QuestState;

public class QuestList extends L2GameServerPacket {
    private final List<Quest> _quests;
    private final Player _player;

    public QuestList(Player player) {
        _quests = player.getQuestList().getAllQuests(true);
        _player = player;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x80);

        writeH(_quests.size());

        for (Quest quest : _quests) {
            // Write quest id.
            writeD(quest.getQuestId());

            // Write quest's flags or cond value, if active.
            final QuestState qs = _player.getQuestList().getQuestState(quest.getQuestId());
            writeD((qs == null) ? 0 : qs.getFlags());
        }
    }
}