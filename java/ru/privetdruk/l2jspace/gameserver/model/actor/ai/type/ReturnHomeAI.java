package ru.privetdruk.l2jspace.gameserver.model.actor.ai.type;

import ru.privetdruk.l2jspace.common.pool.ThreadPool;
import ru.privetdruk.l2jspace.gameserver.enums.IntentionType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.location.Location;

public class ReturnHomeAI {
    private static final int WALKING_TIME = 4000;
    private static final int RESTING_TIME = 4000;

    private final Npc npc;
    private final Location lastLocation;

    private boolean returningHome = false;

    public ReturnHomeAI(Npc npc) {
        this.npc = npc;
        lastLocation = new Location(npc.getPosition());
    }

    /**
     * Returns true if NPC is returning home. Also returns true if it rests.
     */
    public boolean isReturningHome() {
        return returningHome;
    }

    /**
     * Starts returning home. Takes effect only first call.
     */
    public void startReturningHome() {
        // Don't do anything if already returning home.
        if (returningHome) {
            return;
        }

        returningHome = true;
        startWalk();
    }

    /**
     * Completely stops returning to home process.
     */
    public void stopReturningHome() {
        if (returningHome) {
            returningHome = false;
            npc.getMove().stop();
            npc.getAI().tryToActive();
        }
    }

    /**
     * Starts walking to the spawn location.
     */
    public void startWalk() {
        // Interrupt returning to home if intention is not active.
        if (npc.getAI().getCurrentIntention().getType() != IntentionType.ACTIVE) {
            returningHome = false;
            return;
        }

        // Check if we already returned before walking.
        if (getSqDistTo(npc.getSpawn().getLoc()) < 10000) {
            stopReturningHome();
            return;
        }

        lastLocation.set(npc.getPosition());
        npc.forceWalkStance();
        npc.getAI().tryToMoveTo(npc.getSpawn().getLoc(), null);

        if (returningHome) {
            ThreadPool.schedule(this::doRest, WALKING_TIME);
        }
    }

    /**
     * Stops and waits a little bit.
     */
    public void doRest() {
        // Interrupt returning to home if intention is not active.
        if (npc.getAI().getCurrentIntention().getType() != IntentionType.MOVE_TO) {
            returningHome = false;
            return;
        }

        if (isStuck()) {
            teleportToSpawn();
            return;
        }

        npc.getMove().stop();
        npc.getAI().tryToActive();

        if (returningHome) {
            ThreadPool.schedule(this::startWalk, RESTING_TIME);
        }
    }

    /**
     * If monster don't move at least 200 distance - it stuck.
     */
    private boolean isStuck() {
        return getSqDistTo(lastLocation) < 5000;
    }

    /**
     * Returns square distance to the given location.
     */
    private int getSqDistTo(Location loc) {
        int dx = npc.getX() - loc.getX();
        int dy = npc.getY() - loc.getY();
        int dz = npc.getZ() - loc.getZ();

        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Teleports monster to the spawn location. Call this method when movement is blocked or can't find path.
     */
    private void teleportToSpawn() {
        returningHome = false;
        npc.getMove().stop();
        npc.teleportTo(npc.getSpawn().getLoc(), 0);
    }
}
