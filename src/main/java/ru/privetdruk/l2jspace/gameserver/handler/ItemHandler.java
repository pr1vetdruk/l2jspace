package ru.privetdruk.l2jspace.gameserver.handler;

import ru.privetdruk.l2jspace.gameserver.handler.itemhandlers.*;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.EtcItem;

import java.util.HashMap;
import java.util.Map;

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