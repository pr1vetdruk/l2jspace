package ru.privetdruk.l2jspace.gameserver.data.xml;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import ru.privetdruk.l2jspace.common.data.StatSet;
import ru.privetdruk.l2jspace.common.data.xml.IXmlReader;
import ru.privetdruk.l2jspace.common.geometry.Polygon;
import ru.privetdruk.l2jspace.gameserver.data.manager.CastleManager;
import ru.privetdruk.l2jspace.gameserver.enums.DoorType;
import ru.privetdruk.l2jspace.gameserver.geoengine.GeoEngine;
import ru.privetdruk.l2jspace.gameserver.geoengine.geodata.ABlock;
import ru.privetdruk.l2jspace.gameserver.geoengine.geodata.GeoStructure;
import ru.privetdruk.l2jspace.gameserver.idfactory.IdFactory;
import ru.privetdruk.l2jspace.gameserver.model.World;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Door;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.DoorTemplate;
import ru.privetdruk.l2jspace.gameserver.model.entity.Castle;
import ru.privetdruk.l2jspace.gameserver.model.location.Point2D;

import java.nio.file.Path;
import java.util.*;

/**
 * This class loads and stores {@link Door}s.<br>
 * <br>
 * The different informations help to generate a {@link DoorTemplate} and a GeoObject, then we create the Door instance itself. The spawn is made just after the initialization of this class to avoid NPEs.
 */
public class DoorData implements IXmlReader {
    private final Map<Integer, Door> _doors = new HashMap<>();

    protected DoorData() {
        load();
    }

    @Override
    public void load() {
        parseFile("./data/xml/doors.xml");
        LOGGER.info("Loaded {} doors templates.", _doors.size());
    }

    @Override
    public void parseDocument(Document doc, Path path) {
        forEach(doc, "list", listNode -> forEach(listNode, "door", doorNode ->
        {
            final StatSet set = parseAttributes(doorNode);
            final int id = set.getInteger("id");
            forEach(doorNode, "castle", castleNode -> set.set("castle", parseString(castleNode.getAttributes(), "id")));
            forEach(doorNode, "clanHall", chNode -> set.set("clanHall", parseString(chNode.getAttributes(), "id")));
            forEach(doorNode, "position", positionNode ->
            {
                final NamedNodeMap attrs = positionNode.getAttributes();
                set.set("posX", parseInteger(attrs, "x"));
                set.set("posY", parseInteger(attrs, "y"));
                set.set("posZ", parseInteger(attrs, "z"));
            });

            final List<Point2D> coords = new ArrayList<>();
            forEach(doorNode, "coordinates", coordinatesNode -> forEach(coordinatesNode, "loc", locNode ->
            {
                final NamedNodeMap attrs = locNode.getAttributes();
                coords.add(new Point2D(parseInteger(attrs, "x"), parseInteger(attrs, "y")));
            }));

            int minX = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxY = Integer.MIN_VALUE;
            for (final Point2D coord : coords) {
                minX = Math.min(minX, coord.getX());
                maxX = Math.max(maxX, coord.getX());
                minY = Math.min(minY, coord.getY());
                maxY = Math.max(maxY, coord.getY());
            }

            if (World.isOutOfWorld(minX, maxX, minY, maxY)) {
                LOGGER.error("Door id {} coords are outside of world.", id);
                return;
            }

            forEach(doorNode, "stats|function", node -> set.putAll(parseAttributes(node)));

            final int posX = set.getInteger("posX");
            final int posY = set.getInteger("posY");
            final int posZ = set.getInteger("posZ");
            final int x = GeoEngine.getGeoX(minX) - 1;
            final int y = GeoEngine.getGeoY(minY) - 1;
            final int sizeX = (GeoEngine.getGeoX(maxX) + 1) - x + 1;
            final int sizeY = (GeoEngine.getGeoY(maxY) + 1) - y + 1;
            final int geoX = GeoEngine.getGeoX(posX);
            final int geoY = GeoEngine.getGeoY(posY);
            final int geoZ = GeoEngine.getInstance().getHeightNearest(geoX, geoY, posZ);
            final ABlock block = GeoEngine.getInstance().getBlock(geoX, geoY);
            final int i = block.getIndexAbove(geoX, geoY, geoZ, null);
            if (i >= 0) {
                final int layerDiff = block.getHeight(i, null) - geoZ;
                if (set.getInteger("height") > layerDiff)
                    set.set("height", layerDiff - GeoStructure.CELL_IGNORE_HEIGHT);
            }
            final int limit = set.getEnum("type", DoorType.class) == DoorType.WALL ? GeoStructure.CELL_IGNORE_HEIGHT * 4 : GeoStructure.CELL_IGNORE_HEIGHT;
            final boolean[][] inside = new boolean[sizeX][sizeY];
            final Polygon polygon = new Polygon(id, coords);
            for (int ix = 0; ix < sizeX; ix++) {
                for (int iy = 0; iy < sizeY; iy++) {
                    final int gx = x + ix;
                    final int gy = y + iy;
                    final int z = GeoEngine.getInstance().getHeightNearest(gx, gy, posZ);
                    if (Math.abs(z - posZ) > limit)
                        continue;

                    final int worldX = GeoEngine.getWorldX(gx);
                    final int worldY = GeoEngine.getWorldY(gy);

                    cell:
                    for (int wix = worldX - 6; wix <= worldX + 6; wix += 2) {
                        for (int wiy = worldY - 6; wiy <= worldY + 6; wiy += 2) {
                            if (polygon.isInside(wix, wiy)) {
                                inside[ix][iy] = true;
                                break cell;
                            }
                        }
                    }
                }
            }

            set.set("geoX", x);
            set.set("geoY", y);
            set.set("geoZ", geoZ);
            set.set("geoData", GeoEngine.calculateGeoObject(inside));
            set.set("coords", coords.toArray(Point2D[]::new));
            set.set("pAtk", 0);
            set.set("mAtk", 0);
            set.set("runSpd", 0);
            set.set("radius", 16);

            final Door door = new Door(IdFactory.getInstance().getNextId(), new DoorTemplate(set));
            door.getStatus().setMaxHpMp();
            door.getPosition().set(posX, posY, posZ);

            _doors.put(door.getDoorId(), door);
        }));
    }

    public final void reload() {
        for (Door door : _doors.values())
            door.openMe();

        _doors.clear();

        for (Castle castle : CastleManager.getInstance().getCastles())
            castle.getDoors().clear();

        load();
        spawn();
    }

    /**
     * Spawns {@link Door}s into the world. If this door is associated to a {@link Castle}, we load door upgrade aswell.<br>
     * <br>
     * Note: keep as side-method, do not join to the load(). On initial load, the DoorTable.getInstance() is not initialized, yet Door is calling it during spawn process...causing NPE.
     */
    public final void spawn() {
        // spawn doors
        for (Door door : _doors.values())
            door.spawnMe();

        // load doors upgrades
        for (Castle castle : CastleManager.getInstance().getCastles())
            castle.loadDoorUpgrade();
    }

    public Door getDoor(int id) {
        return _doors.get(id);
    }

    public Collection<Door> getDoors() {
        return _doors.values();
    }

    public static DoorData getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final DoorData INSTANCE = new DoorData();
    }
}