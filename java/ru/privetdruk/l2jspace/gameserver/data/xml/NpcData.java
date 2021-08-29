package ru.privetdruk.l2jspace.gameserver.data.xml;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import ru.privetdruk.l2jspace.common.data.StatSet;
import ru.privetdruk.l2jspace.common.data.xml.IXmlReader;
import ru.privetdruk.l2jspace.common.util.ArraysUtil;
import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.data.SkillTable;
import ru.privetdruk.l2jspace.gameserver.model.MinionData;
import ru.privetdruk.l2jspace.gameserver.model.PetDataEntry;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.PetTemplate;
import ru.privetdruk.l2jspace.gameserver.model.item.DropCategory;
import ru.privetdruk.l2jspace.gameserver.model.item.DropData;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Loads and stores {@link NpcTemplate}s.
 */
public class NpcData implements IXmlReader {
    private static final String DEFAULT_NPC_INSTANCE_PACKAGE = "ru.privetdruk.l2jspace.gameserver.model.actor.instance.";
    private static final String CUSTOM_NPC_INSTANCE_PACKAGE = "ru.privetdruk.l2jspace.gameserver.custom.instance.";
    private static final int CUSTOM_NPC_ID_STARTER = 50000;

    private final Map<Integer, NpcTemplate> npcs = new HashMap<>();

    protected NpcData() {
        load();
    }

    @Override
    public void load() {
        parseFile("./data/xml/npc");
        LOGGER.info("Loaded {} NPC templates.", npcs.size());
    }

    @Override
    public void parseDocument(Document doc, Path path) {
        forEach(doc, "list", listNode -> forEach(listNode, "npc", npcNode -> {
            final NamedNodeMap attrs = npcNode.getAttributes();
            final int npcId = parseInteger(attrs, "id");
            final int templateId = attrs.getNamedItem("idTemplate") == null ? npcId : parseInteger(attrs, "idTemplate");
            final StatSet statSet = new StatSet();
            statSet.set("id", npcId);
            statSet.set("idTemplate", templateId);
            statSet.set("name", parseString(attrs, "name"));
            statSet.set("title", parseString(attrs, "title"));

            forEach(npcNode, "set", setNode -> {
                final NamedNodeMap setAttrs = setNode.getAttributes();
                statSet.set(parseString(setAttrs, "name"), parseString(setAttrs, "val"));
            });

            forEach(npcNode, "ai", aiNode -> {
                final NamedNodeMap aiAttrs = aiNode.getAttributes();
                statSet.set("aiType", parseString(aiAttrs, "type"));
                statSet.set("ssCount", parseInteger(aiAttrs, "ssCount"));
                statSet.set("ssRate", parseInteger(aiAttrs, "ssRate"));
                statSet.set("spsCount", parseInteger(aiAttrs, "spsCount"));
                statSet.set("spsRate", parseInteger(aiAttrs, "spsRate"));
                statSet.set("aggro", parseInteger(aiAttrs, "aggro"));

                if (aiAttrs.getNamedItem("clan") != null) {
                    statSet.set("clan", parseString(aiAttrs, "clan").split(";"));
                    statSet.set("clanRange", parseInteger(aiAttrs, "clanRange"));

                    if (aiAttrs.getNamedItem("ignoredIds") != null) {
                        statSet.set("ignoredIds", parseString(aiAttrs, "ignoredIds"));
                    }
                }

                statSet.set("canMove", parseBoolean(aiAttrs, "canMove"));
                statSet.set("seedable", parseBoolean(aiAttrs, "seedable"));
            });

            forEach(npcNode, "drops", dropsNode -> {
                final String type = statSet.getString("type");
                final boolean isRaid = type.equalsIgnoreCase("RaidBoss") || type.equalsIgnoreCase("GrandBoss");
                final List<DropCategory> drops = new ArrayList<>();

                forEach(dropsNode, "category", categoryNode -> {
                    final NamedNodeMap categoryAttrs = categoryNode.getAttributes();
                    final DropCategory category = new DropCategory(parseInteger(categoryAttrs, "id"));

                    forEach(categoryNode, "drop", dropNode -> {
                        final NamedNodeMap dropAttrs = dropNode.getAttributes();
                        final DropData data = new DropData(parseInteger(dropAttrs, "itemid"), parseInteger(dropAttrs, "min"), parseInteger(dropAttrs, "max"), parseInteger(dropAttrs, "chance"));

                        if (ArraysUtil.contains(Config.NO_DROP_ITEMS, data.getItemId())) {
                            return;
                        }

                        if (ItemData.getInstance().getTemplate(data.getItemId()) == null) {
                            LOGGER.warn("Droplist data for undefined itemId: {}.", data.getItemId());
                            return;
                        }

                        category.addDropData(data, isRaid);
                    });

                    drops.add(category);
                });

                statSet.set("drops", drops);
            });

            forEach(npcNode, "minions", minionsNode -> {
                final List<MinionData> minions = new ArrayList<>();

                forEach(minionsNode, "minion", minionNode -> {
                    final NamedNodeMap minionAttrs = minionNode.getAttributes();
                    final MinionData data = new MinionData();
                    data.setMinionId(parseInteger(minionAttrs, "id"));
                    data.setAmountMin(parseInteger(minionAttrs, "min"));
                    data.setAmountMax(parseInteger(minionAttrs, "max"));
                    minions.add(data);
                });

                statSet.set("minions", minions);
            });

            forEach(npcNode, "petdata", petdataNode -> {
                final NamedNodeMap petdataAttrs = petdataNode.getAttributes();
                statSet.set("mustUsePetTemplate", true);
                statSet.set("food1", parseInteger(petdataAttrs, "food1"));
                statSet.set("food2", parseInteger(petdataAttrs, "food2"));
                statSet.set("autoFeedLimit", parseDouble(petdataAttrs, "autoFeedLimit"));
                statSet.set("hungryLimit", parseDouble(petdataAttrs, "hungryLimit"));
                statSet.set("unsummonLimit", parseDouble(petdataAttrs, "unsummonLimit"));

                final Map<Integer, PetDataEntry> entries = new HashMap<>();
                forEach(petdataNode, "stat", statNode -> {
                    final StatSet petSet = parseAttributes(statNode);
                    entries.put(petSet.getInteger("level"), new PetDataEntry(petSet));
                });

                statSet.set("petData", entries);
            });

            forEach(npcNode, "skills", skillsNode -> {
                final List<L2Skill> skills = new ArrayList<>();
                forEach(skillsNode, "skill", skillNode -> {
                    final NamedNodeMap skillAttrs = skillNode.getAttributes();
                    final int skillId = parseInteger(skillAttrs, "id");
                    final int level = parseInteger(skillAttrs, "level");

                    if (skillId == L2Skill.SKILL_NPC_RACE) {
                        statSet.set("raceId", level);
                        return;
                    }

                    final L2Skill skill = SkillTable.getInstance().getInfo(skillId, level);
                    if (skill == null)
                        return;

                    skills.add(skill);
                });

                statSet.set("skills", skills);
            });

            forEach(npcNode, "teachTo", teachToNode -> statSet.set("teachTo", parseString(teachToNode.getAttributes(), "classes")));

            npcs.put(npcId, statSet.getBool("mustUsePetTemplate", false) ? new PetTemplate(statSet) : new NpcTemplate(statSet));
        }));
    }

    public void reload() {
        npcs.clear();

        load();
    }

    public NpcTemplate getTemplate(int id) {
        return npcs.get(id);
    }

    /**
     * @param name : The name of the NPC to search.
     * @return the {@link NpcTemplate} for a given name.
     */
    public NpcTemplate getTemplateByName(String name) {
        return npcs.values().stream().filter(t -> t.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Gets all {@link NpcTemplate}s matching the filter.
     *
     * @param filter : The Predicate filter used as a filter.
     * @return a NpcTemplate list matching the given filter.
     */
    public List<NpcTemplate> getTemplates(Predicate<NpcTemplate> filter) {
        return npcs.values().stream().filter(filter).collect(Collectors.toList());
    }

    public Collection<NpcTemplate> getAllNpcs() {
        return npcs.values();
    }

    public static String getNpcInstancePackage(int npcId) {
        return npcId < CUSTOM_NPC_ID_STARTER ? DEFAULT_NPC_INSTANCE_PACKAGE : CUSTOM_NPC_INSTANCE_PACKAGE;
    }

    public static NpcData getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final NpcData INSTANCE = new NpcData();
    }
}