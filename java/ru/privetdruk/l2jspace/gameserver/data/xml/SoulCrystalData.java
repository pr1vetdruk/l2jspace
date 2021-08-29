package ru.privetdruk.l2jspace.gameserver.data.xml;

import org.w3c.dom.Document;
import ru.privetdruk.l2jspace.common.data.StatSet;
import ru.privetdruk.l2jspace.common.data.xml.IXmlReader;
import ru.privetdruk.l2jspace.gameserver.model.soulcrystal.LevelingInfo;
import ru.privetdruk.l2jspace.gameserver.model.soulcrystal.SoulCrystal;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * This class loads and stores following Soul Crystal infos :
 * <ul>
 * <li>{@link SoulCrystal} infos related to items (such as level, initial / broken / succeeded itemId) ;</li>
 * <li>{@link LevelingInfo} infos related to NPCs (such as absorb type, chances of fail/success, if the item cast needs to be done and the list of allowed crystal levels).</li>
 * </ul>
 */
public class SoulCrystalData implements IXmlReader {
    private final Map<Integer, SoulCrystal> _soulCrystals = new HashMap<>();
    private final Map<Integer, LevelingInfo> _levelingInfos = new HashMap<>();

    protected SoulCrystalData() {
        load();
    }

    @Override
    public void load() {
        parseFile("./data/xml/soulCrystals.xml");
        LOGGER.info("Loaded {} Soul Crystals data and {} NPCs data.", _soulCrystals.size(), _levelingInfos.size());
    }

    @Override
    public void parseDocument(Document doc, Path path) {
        forEach(doc, "list", listNode ->
        {
            forEach(listNode, "crystals", crystalsNode -> forEach(crystalsNode, "crystal", crystalNode ->
            {
                final StatSet set = parseAttributes(crystalNode);
                _soulCrystals.put(set.getInteger("initial"), new SoulCrystal(set));
            }));
            forEach(listNode, "npcs", npcsNode -> forEach(npcsNode, "npc", npcNode ->
            {
                final StatSet set = parseAttributes(npcNode);
                _levelingInfos.put(set.getInteger("id"), new LevelingInfo(set));
            }));
        });
    }

    public final Map<Integer, SoulCrystal> getSoulCrystals() {
        return _soulCrystals;
    }

    public final Map<Integer, LevelingInfo> getLevelingInfos() {
        return _levelingInfos;
    }

    public static SoulCrystalData getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final SoulCrystalData INSTANCE = new SoulCrystalData();
    }
}