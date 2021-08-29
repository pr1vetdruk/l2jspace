package ru.privetdruk.l2jspace.gameserver.data.manager;

import ru.privetdruk.l2jspace.common.logging.CLogger;
import ru.privetdruk.l2jspace.gameserver.enums.CabalType;
import ru.privetdruk.l2jspace.gameserver.enums.PeriodType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.spawn.Spawn;

import java.util.ArrayList;
import java.util.List;

public class SevenSignsSpawnManager {
    private static final CLogger LOGGER = new CLogger(SevenSignsSpawnManager.class.getName());

    private final List<Spawn> _weekly = new ArrayList<>();
    private final List<Spawn> _firstWeek = new ArrayList<>();
    private final List<Spawn> _secondWeek = new ArrayList<>();
    private final List<Spawn> _thirdWeek = new ArrayList<>();

    protected SevenSignsSpawnManager() {
        notifyChangeMode();
    }

    public void addCreature(Spawn spawnDat, int week) {
        switch (week) {
            case 0 -> _weekly.add(spawnDat);
            case 1 -> _firstWeek.add(spawnDat);
            case 2 -> _secondWeek.add(spawnDat);
            case 3 -> _thirdWeek.add(spawnDat);
        }
    }

    public void spawnFirstWeekCreatures() {
        spawnCreatures(_weekly, _firstWeek, "week 0", "week 1");
    }

    public void spawnSecondWeekCreatures() {
        spawnCreatures(_firstWeek, _secondWeek, "week 1", "week 2");
    }

    public void spawnThirdWeekCreatures() {
        spawnCreatures(_secondWeek, _thirdWeek, "week 2", "week 3");
    }

    private static void spawnCreatures(List<Spawn> unSpawnCreatures, List<Spawn> spawnCreatures, String unspawnLogInfo, String spawnLogInfo) {
        try {
            if (!unSpawnCreatures.isEmpty()) {
                int i = 0;
                for (Spawn spawn : unSpawnCreatures) {
                    spawn.setRespawnState(false);

                    final Npc last = spawn.getNpc();
                    if (last != null) {
                        last.deleteMe();
                        i++;
                    }
                }
                LOGGER.info("Removed " + i + " " + unspawnLogInfo + " creatures");
            }

            int i = 0;
            for (Spawn spawnDat : spawnCreatures) {
                if (spawnDat == null)
                    continue;
                spawnDat.setRespawnState(true);
                spawnDat.doSpawn(false);
                i++;
            }

            LOGGER.info("Spawned " + i + " " + spawnLogInfo + " creatures");
        } catch (Exception e) {
            LOGGER.warn("Error while spawning creatures: " + e.getMessage(), e);
        }
    }

    public void notifyChangeMode() {
        if (_firstWeek.isEmpty() && _secondWeek.isEmpty() && _thirdWeek.isEmpty()) {
            return;
        }

        PeriodType currentPeriod = SevenSignsManager.getInstance().getCurrentPeriod();

        if (currentPeriod == PeriodType.COMPETITION) {
            spawnFirstWeekCreatures();
        } else if (currentPeriod == PeriodType.RESULTS) {
            return;
        } else if (currentPeriod == PeriodType.SEAL_VALIDATION) {
            CabalType winningCabal = SevenSignsManager.getInstance().getWinningCabal();

            if (winningCabal == CabalType.DAWN) {
                spawnThirdWeekCreatures();
            }

            if (winningCabal == CabalType.DUSK) {
                spawnSecondWeekCreatures();
            }
        }
    }

    public void cleanUp() {
        _weekly.clear();
        _firstWeek.clear();
        _secondWeek.clear();
        _thirdWeek.clear();
    }

    public static SevenSignsSpawnManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final SevenSignsSpawnManager INSTANCE = new SevenSignsSpawnManager();
    }
}