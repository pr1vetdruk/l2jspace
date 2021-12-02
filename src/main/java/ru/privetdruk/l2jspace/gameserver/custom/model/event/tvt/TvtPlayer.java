package ru.privetdruk.l2jspace.gameserver.custom.model.event.tvt;

import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventPlayer;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.TeamSetting;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

public class TvtPlayer extends EventPlayer {
    private TeamSetting teamSetting;
    private int countKills;

    public TvtPlayer(Player player, TeamSetting teamSettings) {
        super(player, teamSettings);
    }
}
