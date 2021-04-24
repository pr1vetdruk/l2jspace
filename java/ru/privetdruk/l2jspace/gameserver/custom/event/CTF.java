package ru.privetdruk.l2jspace.gameserver.custom.event;

import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

public class CTF extends EventEngine {
    @Override
    protected boolean customPreLaunchChecks() {
        return false;
    }

    @Override
    protected void restorePlayerData(Player player) {

    }
}
