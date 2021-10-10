package ru.privetdruk.l2jspace.gameserver.scripting.script.teleport;

import ru.privetdruk.l2jspace.common.util.ArraysUtil;
import ru.privetdruk.l2jspace.gameserver.data.manager.SevenSignsManager;
import ru.privetdruk.l2jspace.gameserver.data.xml.TeleportData;
import ru.privetdruk.l2jspace.gameserver.enums.CabalType;
import ru.privetdruk.l2jspace.gameserver.enums.TeleportType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.scripting.Quest;

public class HuntingGroundsTeleporter extends Quest {
    private static final int[] PRIESTS =
            {
                    31078,
                    31079,
                    31080,
                    31081,
                    31082,
                    31083,
                    31084,
                    31085,
                    31086,
                    31087,
                    31088,
                    31089,
                    31090,
                    31091,
                    31168,
                    31169,
                    31692,
                    31693,
                    31694,
                    31695,
                    31997,
                    31998
            };

    private static final int[] DAWN_NPCS =
            {
                    31078,
                    31079,
                    31080,
                    31081,
                    31082,
                    31083,
                    31084,
                    31168,
                    31692,
                    31694,
                    31997
            };

    public HuntingGroundsTeleporter() {
        super(-1, "teleport");

        addTalkId(PRIESTS);
    }

    @Override
    public String onTalk(Npc npc, Player player) {
        final CabalType playerCabal = SevenSignsManager.getInstance().getPlayerCabal(player.getId());
        if (playerCabal == CabalType.NORMAL)
            return ArraysUtil.contains(DAWN_NPCS, npc.getNpcId()) ? "dawn_tele-no.htm" : "dusk_tele-no.htm";

        TeleportData.getInstance().showTeleportList(player, npc, TeleportType.STANDARD);
        return null;
    }
}