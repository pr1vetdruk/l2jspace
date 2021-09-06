package ru.privetdruk.l2jspace.gameserver.custom.model.event.lastemperor;

import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventPlayer;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.TeamSetting;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

public class LastEmperorPlayer extends EventPlayer {
    public LastEmperorPlayer(Player player, TeamSetting teamSettings) {
        super(player, teamSettings);
    }
}
