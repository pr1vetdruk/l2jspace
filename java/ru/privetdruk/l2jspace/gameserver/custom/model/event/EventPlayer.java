package ru.privetdruk.l2jspace.gameserver.custom.model.event;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

public class EventPlayer {
    private Player player;
    private TeamSetting teamSettings;

    public EventPlayer(Player player, TeamSetting teamSettings) {
        this.player = player;
        this.teamSettings = teamSettings;
    }

    public Player getPlayer() {
        return player;
    }

    public TeamSetting getTeamSettings() {
        return teamSettings;
    }

    public void setTeamSettings(TeamSetting teamSettings) {
        this.teamSettings = teamSettings;
    }
}
