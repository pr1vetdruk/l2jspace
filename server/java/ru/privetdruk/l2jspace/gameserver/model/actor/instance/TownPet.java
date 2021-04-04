package ru.privetdruk.l2jspace.gameserver.model.actor.instance;

import java.util.concurrent.ScheduledFuture;

import ru.privetdruk.l2jspace.common.pool.ThreadPool;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;

public class TownPet extends Folk {
    private ScheduledFuture<?> _aiTask;

    public TownPet(int objectId, NpcTemplate template) {
        super(objectId, template);

        forceRunStance();

        _aiTask = ThreadPool.scheduleAtFixedRate(() -> moveFromSpawnPointUsingRandomOffset(50), 1000, 10000);
    }

    @Override
    public void onInteract(Player player) {
    }

    @Override
    public void deleteMe() {
        if (_aiTask != null) {
            _aiTask.cancel(true);
            _aiTask = null;
        }
        super.deleteMe();
    }
}