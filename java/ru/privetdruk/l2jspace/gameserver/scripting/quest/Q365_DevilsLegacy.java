package ru.privetdruk.l2jspace.gameserver.scripting.quest;

import ru.privetdruk.l2jspace.common.random.Rnd;

import ru.privetdruk.l2jspace.gameserver.data.SkillTable;
import ru.privetdruk.l2jspace.gameserver.enums.QuestStatus;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.scripting.Quest;
import ru.privetdruk.l2jspace.gameserver.scripting.QuestState;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class Q365_DevilsLegacy extends Quest {
    private static final String qn = "Q365_DevilsLegacy";

    // NPCs
    private static final int RANDOLF = 30095;
    private static final int COLLOB = 30092;

    // Item
    private static final int PIRATE_TREASURE_CHEST = 5873;

    public Q365_DevilsLegacy() {
        super(365, "Devil's Legacy");

        setItemsIds(PIRATE_TREASURE_CHEST);

        addStartNpc(RANDOLF);
        addTalkId(RANDOLF, COLLOB);

        addKillId(20836, 20845, 21629, 21630); // Pirate Zombie && Pirate Zombie Captain.
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player) {
        String htmltext = event;
        QuestState st = player.getQuestList().getQuestState(qn);
        if (st == null)
            return htmltext;

        if (event.equalsIgnoreCase("30095-02.htm")) {
            st.setState(QuestStatus.STARTED);
            st.setCond(1);
            playSound(player, SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("30095-06.htm")) {
            playSound(player, SOUND_GIVEUP);
            st.exitQuest(true);
        } else if (event.equalsIgnoreCase("30092-05.htm")) {
            if (!player.getInventory().hasItems(PIRATE_TREASURE_CHEST))
                htmltext = "30092-02.htm";
            else if (player.getInventory().getItemCount(57) < 600)
                htmltext = "30092-03.htm";
            else {
                takeItems(player, PIRATE_TREASURE_CHEST, 1);
                takeItems(player, 57, 600);

                int i0;
                if (Rnd.get(100) < 80) {
                    i0 = Rnd.get(100);
                    if (i0 < 1)
                        giveItems(player, 955, 1);
                    else if (i0 < 4)
                        giveItems(player, 956, 1);
                    else if (i0 < 36)
                        giveItems(player, 1868, 1);
                    else if (i0 < 68)
                        giveItems(player, 1884, 1);
                    else
                        giveItems(player, 1872, 1);

                    htmltext = "30092-05.htm";
                } else {
                    i0 = Rnd.get(1000);
                    if (i0 < 10)
                        giveItems(player, 951, 1);
                    else if (i0 < 40)
                        giveItems(player, 952, 1);
                    else if (i0 < 60)
                        giveItems(player, 955, 1);
                    else if (i0 < 260)
                        giveItems(player, 956, 1);
                    else if (i0 < 445)
                        giveItems(player, 1879, 1);
                    else if (i0 < 630)
                        giveItems(player, 1880, 1);
                    else if (i0 < 815)
                        giveItems(player, 1882, 1);
                    else
                        giveItems(player, 1881, 1);

                    htmltext = "30092-06.htm";

                    // Curse effect !
                    final L2Skill skill = SkillTable.getInstance().getInfo(4082, 1);
                    if (skill != null && player.getFirstEffect(skill) == null)
                        skill.getEffects(npc, player);
                }
            }
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
                htmltext = (player.getStatus().getLevel() < 39) ? "30095-00.htm" : "30095-01.htm";
                break;

            case STARTED:
                switch (npc.getNpcId()) {
                    case RANDOLF:
                        if (!player.getInventory().hasItems(PIRATE_TREASURE_CHEST))
                            htmltext = "30095-03.htm";
                        else {
                            htmltext = "30095-05.htm";

                            int reward = player.getInventory().getItemCount(PIRATE_TREASURE_CHEST) * 400;

                            takeItems(player, PIRATE_TREASURE_CHEST, -1);
                            rewardItems(player, 57, reward + 19800);
                        }
                        break;

                    case COLLOB:
                        htmltext = "30092-01.htm";
                        break;
                }
        }

        return htmltext;
    }

    @Override
    public String onKill(Npc npc, Creature killer) {
        final Player player = killer.getActingPlayer();

        final QuestState st = getRandomPartyMemberState(player, npc, QuestStatus.STARTED);
        if (st == null)
            return null;

        dropItems(st.getPlayer(), PIRATE_TREASURE_CHEST, 1, 0, (npc.getNpcId() == 20836) ? 360000 : 520000);

        return null;
    }
}