package ru.privetdruk.l2jspace.gameserver.scripting.quest;

import ru.privetdruk.l2jspace.common.random.Rnd;

import ru.privetdruk.l2jspace.gameserver.enums.QuestStatus;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.scripting.Quest;
import ru.privetdruk.l2jspace.gameserver.scripting.QuestState;

public class Q638_SeekersOfTheHolyGrail extends Quest {
    private static final String QUEST_NAME = "Q638_SeekersOfTheHolyGrail";

    // NPC
    private static final int INNOCENTIN = 31328;

    // Item
    private static final int PAGAN_TOTEM = 8068;

    public Q638_SeekersOfTheHolyGrail() {
        super(638, "Seekers of the Holy Grail");

        setItemsIds(PAGAN_TOTEM);

        addStartNpc(INNOCENTIN);
        addTalkId(INNOCENTIN);

        for (int i = 22138; i < 22175; i++)
            addKillId(i);
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player) {
        String htmltext = event;
        QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
        if (st == null)
            return htmltext;

        if (event.equalsIgnoreCase("31328-02.htm")) {
            st.setState(QuestStatus.STARTED);
            st.setCond(1);
            playSound(player, SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("31328-06.htm")) {
            playSound(player, SOUND_FINISH);
            st.exitQuest(true);
        }

        return htmltext;
    }

    @Override
    public String onTalk(Npc npc, Player player) {
        QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
        String htmltext = getNoQuestMsg();
        if (st == null)
            return htmltext;

        switch (st.getState()) {
            case CREATED:
                htmltext = (player.getStatus().getLevel() < 73) ? "31328-00.htm" : "31328-01.htm";
                break;

            case STARTED:
                if (player.getInventory().getItemCount(PAGAN_TOTEM) >= 2000) {
                    htmltext = "31328-03.htm";
                    playSound(player, SOUND_MIDDLE);
                    takeItems(player, PAGAN_TOTEM, 2000);

                    int chance = Rnd.get(3);
                    if (chance == 0)
                        rewardItems(player, 959, 1);
                    else if (chance == 1)
                        rewardItems(player, 960, 1);
                    else
                        rewardItems(player, 57, 3576000);
                } else
                    htmltext = "31328-04.htm";
                break;
        }

        return htmltext;
    }

    @Override
    public String onKill(Npc npc, Creature killer) {
        final Player player = killer.getActingPlayer();

        final QuestState st = getRandomPartyMemberState(player, npc, QuestStatus.STARTED);
        if (st == null)
            return null;

        dropItemsAlways(st.getPlayer(), PAGAN_TOTEM, 1, 0);

        return null;
    }
}