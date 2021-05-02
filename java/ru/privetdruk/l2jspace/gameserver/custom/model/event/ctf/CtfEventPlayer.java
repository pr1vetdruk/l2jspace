package ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf;

import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventPlayer;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.TeamSetting;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

public class CtfEventPlayer extends EventPlayer {
    private Npc enemyFlag = null;
    private int countFlags = 0;

    public CtfEventPlayer(Player player, TeamSetting teamSettings) {
        super(player, teamSettings);
    }

    public boolean isHasFlag() {
        return enemyFlag != null;
    }

    public void setEnemyFlag(Npc enemyFlag) {
        this.enemyFlag = enemyFlag;
    }

    @Override
    public CtfTeamSetting getTeamSettings() {
        return (CtfTeamSetting) super.getTeamSettings();
    }

    public int getCountFlags() {
        return countFlags;
    }

    public void setCountFlags(int countFlags) {
        this.countFlags = countFlags;
    }
}
