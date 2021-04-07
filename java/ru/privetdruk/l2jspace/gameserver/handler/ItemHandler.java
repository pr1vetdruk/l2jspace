package ru.privetdruk.l2jspace.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.BeastSoulShots;
import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.BeastSpices;
import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.BeastSpiritShots;
import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.BlessedSpiritShots;
import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.Books;
import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.BreakingArrow;
import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.Calculators;
import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.Elixirs;
import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.EnchantScrolls;
import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.FishShots;
import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.Harvesters;
import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.ItemSkills;
import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.Keys;
import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.Maps;
import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.MercenaryTickets;
import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.PaganKeys;
import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.PetFoods;
import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.Recipes;
import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.RollingDices;
import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.ScrollsOfResurrection;
import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.Seeds;
import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.SevenSignsRecords;
import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.SoulCrystals;
import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.SoulShots;
import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.SpecialXMas;
import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.SpiritShots;
import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.SummonItems;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.EtcItem;

public class ItemHandler {
    private final Map<Integer, IItemHandler> _entries = new HashMap<>();

    protected ItemHandler() {
        registerHandler(new BeastSoulShots());
        registerHandler(new BeastSpices());
        registerHandler(new BeastSpiritShots());
        registerHandler(new BlessedSpiritShots());
        registerHandler(new Books());
        registerHandler(new BreakingArrow());
        registerHandler(new Calculators());
        registerHandler(new Elixirs());
        registerHandler(new EnchantScrolls());
        registerHandler(new FishShots());
        registerHandler(new Harvesters());
        registerHandler(new ItemSkills());
        registerHandler(new Keys());
        registerHandler(new Maps());
        registerHandler(new MercenaryTickets());
        registerHandler(new PaganKeys());
        registerHandler(new PetFoods());
        registerHandler(new Recipes());
        registerHandler(new RollingDices());
        registerHandler(new ScrollsOfResurrection());
        registerHandler(new Seeds());
        registerHandler(new SevenSignsRecords());
        registerHandler(new SoulShots());
        registerHandler(new SpecialXMas());
        registerHandler(new SoulCrystals());
        registerHandler(new SpiritShots());
        registerHandler(new SummonItems());
    }

    private void registerHandler(IItemHandler handler) {
        _entries.put(handler.getClass().getSimpleName().intern().hashCode(), handler);
    }

    public IItemHandler getHandler(EtcItem item) {
        if (item == null || item.getHandlerName() == null)
            return null;

        return _entries.get(item.getHandlerName().hashCode());
    }

    public int size() {
        return _entries.size();
    }

    public static ItemHandler getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final ItemHandler INSTANCE = new ItemHandler();
    }
}