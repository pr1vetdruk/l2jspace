package ru.privetdruk.l2jspace.gameserver.scripting.task;

import ru.privetdruk.l2jspace.gameserver.data.sql.ClanTable;
import ru.privetdruk.l2jspace.gameserver.scripting.ScheduledQuest;

public final class ClanLadderRefresh extends ScheduledQuest {
    public ClanLadderRefresh() {
        super(-1, "task");
    }

    @Override
    public final void onStart() {
        ClanTable.getInstance().refreshClansLadder(true);
    }

    @Override
    public final void onEnd() {
    }
}