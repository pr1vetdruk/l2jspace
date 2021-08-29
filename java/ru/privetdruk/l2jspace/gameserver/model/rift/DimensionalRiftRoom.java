package ru.privetdruk.l2jspace.gameserver.model.rift;

import ru.privetdruk.l2jspace.common.data.StatSet;
import ru.privetdruk.l2jspace.common.random.Rnd;
import ru.privetdruk.l2jspace.gameserver.model.location.Location;
import ru.privetdruk.l2jspace.gameserver.model.spawn.Spawn;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * One cell of Dimensional Rift system.<br>
 * <br>
 * Each DimensionalRiftRoom holds specific {@link Spawn}s, a {@link Shape}, and a teleport {@link Location}.
 */
public class DimensionalRiftRoom {
    public static final int Z_VALUE = -6752;

    private final List<Spawn> _spawns = new ArrayList<>();

    private final byte _type;
    private final byte _id;

    private final int _xMin;
    private final int _xMax;
    private final int _yMin;
    private final int _yMax;

    private final Location _teleportLoc;

    private final Shape _shape;

    private final boolean _isBossRoom;

    private boolean _partyInside;

    public DimensionalRiftRoom(byte type, StatSet set) {
        final int xMin = set.getInteger("xMin");
        final int xMax = set.getInteger("xMax");
        final int yMin = set.getInteger("yMin");
        final int yMax = set.getInteger("yMax");

        _type = type;
        _id = set.getByte("id");
        _xMin = (xMin + 128);
        _xMax = (xMax - 128);
        _yMin = (yMin + 128);
        _yMax = (yMax - 128);

        _teleportLoc = new Location(set.getInteger("xT"), set.getInteger("yT"), Z_VALUE);

        _isBossRoom = (_id == 9);

        _shape = new Polygon(new int[]
                {
                        xMin,
                        xMax,
                        xMax,
                        xMin
                }, new int[]
                {
                        yMin,
                        yMin,
                        yMax,
                        yMax
                }, 4);
    }

    @Override
    public String toString() {
        return "RiftRoom #" + _type + "_" + _id + ", full: " + _partyInside + ", tel: " + _teleportLoc.toString() + ", spawns: " + _spawns.size();

    }

    public byte getType() {
        return _type;
    }

    public byte getId() {
        return _id;
    }

    public int getRandomX() {
        return Rnd.get(_xMin, _xMax);
    }

    public int getRandomY() {
        return Rnd.get(_yMin, _yMax);
    }

    public Location getTeleportLoc() {
        return _teleportLoc;
    }

    public boolean checkIfInZone(int x, int y, int z) {
        return _shape.contains(x, y) && z >= -6816 && z <= -6240;
    }

    public boolean isBossRoom() {
        return _isBossRoom;
    }

    public List<Spawn> getSpawns() {
        return _spawns;
    }

    public boolean isPartyInside() {
        return _partyInside;
    }

    public void setPartyInside(boolean partyInside) {
        _partyInside = partyInside;
    }

    public void spawn() {
        for (Spawn spawn : _spawns) {
            spawn.doSpawn(false);
            spawn.setRespawnState(!isBossRoom());
        }
    }

    public void unspawn() {
        for (Spawn spawn : _spawns) {
            spawn.setRespawnState(false);
            if (spawn.getNpc() != null)
                spawn.getNpc().deleteMe();
        }
        _partyInside = false;
    }
}