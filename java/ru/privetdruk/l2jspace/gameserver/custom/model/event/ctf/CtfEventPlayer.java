package ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf;

import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventPlayer;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.TeamSetting;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

public class CtfEventPlayer extends EventPlayer {
    private boolean haveFlag = false;

    public CtfEventPlayer(Player player, TeamSetting teamSettings) {
        super(player, teamSettings);
    }

    public boolean isHaveFlag() {
        return haveFlag;
    }

    public void setHaveFlag(boolean haveFlag) {
        this.haveFlag = haveFlag;
    }

    @Override
    public CtfTeamSetting getTeamSettings() {
        return (CtfTeamSetting) super.getTeamSettings();
    }
}
