package ru.privetdruk.l2jspace.gameserver.scripting.quest;

import ru.privetdruk.l2jspace.gameserver.enums.QuestStatus;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SocialAction;
import ru.privetdruk.l2jspace.gameserver.scripting.Quest;
import ru.privetdruk.l2jspace.gameserver.scripting.QuestState;

public class Q247_PossessorOfAPreciousSoul extends Quest {
    private static final String qn = "Q247_PossessorOfAPreciousSoul";

    // NPCs
    private static final int CARADINE = 31740;
    private static final int LADY_OF_THE_LAKE = 31745;

    // Items
    private static final int CARADINE_LETTER = 7679;
    private static final int NOBLESS_TIARA = 7694;

    public Q247_PossessorOfAPreciousSoul() {
        super(247, "Possessor of a Precious Soul - 4");

        addStartNpc(CARADINE);
        addTalkId(CARADINE, LADY_OF_THE_LAKE);
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player) {
        String htmltext = event;
        QuestState st = player.getQuestList().getQuestState(qn);
        if (st == null)
            return htmltext;

        // Caradine
        if (event.equalsIgnoreCase("31740-03.htm")) {
            st.setState(QuestStatus.STARTED);
            st.setCond(1);
            playSound(player, SOUND_ACCEPT);
            takeItems(player, CARADINE_LETTER, 1);
        } else if (event.equalsIgnoreCase("31740-05.htm")) {
            st.setCond(2);
            player.teleportTo(143209, 43968, -3038, 0);
        }
        // Lady of the lake
        else if (event.equalsIgnoreCase("31745-05.htm")) {
            player.setNoble(true, true);
            giveItems(player, NOBLESS_TIARA, 1);
            rewardExpAndSp(player, 93836, 0);
            player.broadcastPacket(new SocialAction(player, 3));
            playSound(player, SOUND_FINISH);
            st.exitQuest(false);
        }

        return htmltext;
    }

    @Override
    public String onTalk(Npc npc, Player player) {
        String htmltext = getNoQuestMsg();
        QuestState st = player.getQuestList().getQuestState(qn);
        if (st == null)
            return htmltext;

        switch (st.getState()) {
            case CREATED:
                if (player.getInventory().hasItems(CARADINE_LETTER))
                    htmltext = (!player.isSubClassActive() || player.getStatus().getLevel() < 75) ? "31740-02.htm" : "31740-01.htm";
                break;

            case STARTED:
                if (!player.isSubClassActive())
                    break;

                int cond = st.getCond();
                switch (npc.getNpcId()) {
                    case CARADINE:
                        if (cond == 1)
                            htmltext = "31740-04.htm";
                        else if (cond == 2)
                            htmltext = "31740-06.htm";
                        break;

                    case LADY_OF_THE_LAKE:
                        if (cond == 2)
                            htmltext = (player.getStatus().getLevel() < 75) ? "31745-06.htm" : "31745-01.htm";
                        break;
                }
                break;

            case COMPLETED:
                htmltext = getAlreadyCompletedMsg();
                break;
        }

        return htmltext;
    }
}