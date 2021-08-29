package ru.privetdruk.l2jspace.gameserver.data.manager;

import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.spawn.Spawn;
import ru.privetdruk.l2jspace.gameserver.taskmanager.GameTimeTaskManager;

import java.util.ArrayList;
import java.util.List;

public class DayNightManager {
    private final List<Spawn> _dayCreatures = new ArrayList<>();
    private final List<Spawn> _nightCreatures = new ArrayList<>();

    protected DayNightManager() {
    }

    public void addDayCreature(Spawn spawnDat) {
        _dayCreatures.add(spawnDat);
    }

    public void addNightCreature(Spawn spawnDat) {
        _nightCreatures.add(spawnDat);
    }

    public void spawnCreatures(boolean isNight) {
        final List<Spawn> creaturesToUnspawn = (isNight) ? _dayCreatures : _nightCreatures;
        final List<Spawn> creaturesToSpawn = (isNight) ? _nightCreatures : _dayCreatures;

        for (Spawn spawn : creaturesToUnspawn) {
            spawn.setRespawnState(false);

            final Npc last = spawn.getNpc();
            if (last != null)
                last.deleteMe();
        }

        for (Spawn spawn : creaturesToSpawn) {
            spawn.setRespawnState(true);
            spawn.doSpawn(false);
        }
    }

    public void notifyChangeMode() {
        if (_nightCreatures.isEmpty() && _dayCreatures.isEmpty())
            return;

        spawnCreatures(GameTimeTaskManager.getInstance().isNight());
    }

    public void cleanUp() {
        _nightCreatures.clear();
        _dayCreatures.clear();
    }

    public static DayNightManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final DayNightManager INSTANCE = new DayNightManager();
    }
}