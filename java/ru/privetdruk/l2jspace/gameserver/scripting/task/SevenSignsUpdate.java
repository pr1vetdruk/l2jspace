package ru.privetdruk.l2jspace.gameserver.scripting.task;

import ru.privetdruk.l2jspace.gameserver.data.manager.FestivalOfDarknessManager;
import ru.privetdruk.l2jspace.gameserver.data.manager.SevenSignsManager;
import ru.privetdruk.l2jspace.gameserver.scripting.ScheduledQuest;

public final class SevenSignsUpdate extends ScheduledQuest {
    public SevenSignsUpdate() {
        super(-1, "task");
    }

    @Override
    public final void onStart() {
        if (!SevenSignsManager.getInstance().isSealValidationPeriod())
            FestivalOfDarknessManager.getInstance().saveFestivalData(false);

        SevenSignsManager.getInstance().saveSevenSignsData();
        SevenSignsManager.getInstance().saveSevenSignsStatus();
    }

    @Override
    public final void onEnd() {
    }
}