package ru.privetdruk.l2jspace.gameserver.data.xml;

import org.w3c.dom.Document;
import ru.privetdruk.l2jspace.common.data.StatSet;
import ru.privetdruk.l2jspace.common.data.xml.IXmlReader;
import ru.privetdruk.l2jspace.gameserver.idfactory.IdFactory;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.StaticObject;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class loads, stores and spawns {@link StaticObject}s.
 */
public class StaticObjectData implements IXmlReader {
    private final Map<Integer, StaticObject> _objects = new HashMap<>();

    protected StaticObjectData() {
        load();
    }

    @Override
    public void load() {
        parseFile("./data/xml/staticObjects.xml");
        LOGGER.info("Loaded {} static objects.", _objects.size());
    }

    @Override
    public void parseDocument(Document doc, Path path) {
        forEach(doc, "list", listNode -> forEach(listNode, "object", objectNode ->
        {
            final StatSet set = parseAttributes(objectNode);
            final StaticObject obj = new StaticObject(IdFactory.getInstance().getNextId());
            obj.setStaticObjectId(set.getInteger("id"));
            obj.setType(set.getInteger("type"));
            obj.setMap(set.getString("texture"), set.getInteger("mapX"), set.getInteger("mapY"));
            obj.spawnMe(set.getInteger("x"), set.getInteger("y"), set.getInteger("z"));
            _objects.put(obj.getId(), obj);
        }));
    }

    public Collection<StaticObject> getStaticObjects() {
        return _objects.values();
    }

    public static StaticObjectData getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final StaticObjectData INSTANCE = new StaticObjectData();
    }
}