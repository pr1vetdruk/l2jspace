package ru.privetdruk.l2jspace.gameserver.custom.model.event;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

public class EventPlayer {
    private final Player player;
    private TeamSetting teamSettings;

    private final int originalColorName;
    private final int originalKarma;
    private final String originalTitle;

    public EventPlayer(Player player, TeamSetting teamSettings) {
        this.player = player;
        this.teamSettings = teamSettings;
        this.originalColorName = player.getAppearance().getNameColor();
        this.originalKarma = player.getKarma();
        this.originalTitle = player.getTitle();
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

    public int getOriginalColorName() {
        return originalColorName;
    }

    public int getOriginalKarma() {
        return originalKarma;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }
}
