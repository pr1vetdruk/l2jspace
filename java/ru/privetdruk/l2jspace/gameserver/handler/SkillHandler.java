package ru.privetdruk.l2jspace.gameserver.handler;

import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillType;
import ru.privetdruk.l2jspace.gameserver.handler.skillhandlers.*;

import java.util.HashMap;
import java.util.Map;

public class SkillHandler {
    private final Map<Integer, ISkillHandler> _entries = new HashMap<>();

    protected SkillHandler() {
        registerHandler(new BalanceLife());
        registerHandler(new Blow());
        registerHandler(new Cancel());
        registerHandler(new CombatPointHeal());
        registerHandler(new Continuous());
        registerHandler(new CpDamPercent());
        registerHandler(new Craft());
        registerHandler(new Disablers());
        registerHandler(new DrainSoul());
        registerHandler(new Dummy());
        registerHandler(new Extractable());
        registerHandler(new Fishing());
        registerHandler(new FishingSkill());
        registerHandler(new GetPlayer());
        registerHandler(new GiveSp());
        registerHandler(new Harvest());
        registerHandler(new Heal());
        registerHandler(new HealPercent());
        registerHandler(new InstantJump());
        registerHandler(new Manadam());
        registerHandler(new ManaHeal());
        registerHandler(new Mdam());
        registerHandler(new Pdam());
        registerHandler(new Resurrect());
        registerHandler(new Sow());
        registerHandler(new Spoil());
        registerHandler(new StriderSiegeAssault());
        registerHandler(new SummonFriend());
        registerHandler(new SummonCreature());
        registerHandler(new Sweep());
        registerHandler(new TakeCastle());
        registerHandler(new Unlock());
    }

    private void registerHandler(ISkillHandler handler) {
        for (SkillType t : handler.getSkillIds())
            _entries.put(t.ordinal(), handler);
    }

    public ISkillHandler getHandler(SkillType skillType) {
        return _entries.get(skillType.ordinal());
    }

    public int size() {
        return _entries.size();
    }

    public static SkillHandler getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final SkillHandler INSTANCE = new SkillHandler();
    }
}