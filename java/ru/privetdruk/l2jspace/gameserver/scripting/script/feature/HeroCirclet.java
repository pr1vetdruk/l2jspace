package ru.privetdruk.l2jspace.gameserver.scripting.script.feature;

import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.scripting.Quest;

public class HeroCirclet extends Quest {
    private static final int CIRCLET = 6842;

    public HeroCirclet() {
        super(-1, "feature");

        addTalkId(31690, 31769, 31770, 31771, 31772);
    }

    @Override
    public String onTalk(Npc npc, Player player) {
        if (!player.isHero())
            return "no_hero.htm";

        if (player.getInventory().hasItems(CIRCLET))
            return "already_have_circlet.htm";

        giveItems(player, 6842, 1);
        return null;
    }
}