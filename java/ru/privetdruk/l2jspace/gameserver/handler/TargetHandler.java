package ru.privetdruk.l2jspace.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillTargetType;
import ru.privetdruk.l2jspace.gameserver.handler.targethandlers.TargetAlly;
import ru.privetdruk.l2jspace.gameserver.handler.targethandlers.TargetArea;
import ru.privetdruk.l2jspace.gameserver.handler.targethandlers.TargetAreaCorpseMob;
import ru.privetdruk.l2jspace.gameserver.handler.targethandlers.TargetAreaSummon;
import ru.privetdruk.l2jspace.gameserver.handler.targethandlers.TargetAura;
import ru.privetdruk.l2jspace.gameserver.handler.targethandlers.TargetAuraUndead;
import ru.privetdruk.l2jspace.gameserver.handler.targethandlers.TargetBehindAura;
import ru.privetdruk.l2jspace.gameserver.handler.targethandlers.TargetClan;
import ru.privetdruk.l2jspace.gameserver.handler.targethandlers.TargetCorpseAlly;
import ru.privetdruk.l2jspace.gameserver.handler.targethandlers.TargetCorpseMob;
import ru.privetdruk.l2jspace.gameserver.handler.targethandlers.TargetCorpsePet;
import ru.privetdruk.l2jspace.gameserver.handler.targethandlers.TargetCorpsePlayer;
import ru.privetdruk.l2jspace.gameserver.handler.targethandlers.TargetEnemySummon;
import ru.privetdruk.l2jspace.gameserver.handler.targethandlers.TargetFrontArea;
import ru.privetdruk.l2jspace.gameserver.handler.targethandlers.TargetFrontAura;
import ru.privetdruk.l2jspace.gameserver.handler.targethandlers.TargetGround;
import ru.privetdruk.l2jspace.gameserver.handler.targethandlers.TargetHoly;
import ru.privetdruk.l2jspace.gameserver.handler.targethandlers.TargetOne;
import ru.privetdruk.l2jspace.gameserver.handler.targethandlers.TargetOwnerPet;
import ru.privetdruk.l2jspace.gameserver.handler.targethandlers.TargetParty;
import ru.privetdruk.l2jspace.gameserver.handler.targethandlers.TargetPartyMember;
import ru.privetdruk.l2jspace.gameserver.handler.targethandlers.TargetPartyOther;
import ru.privetdruk.l2jspace.gameserver.handler.targethandlers.TargetSelf;
import ru.privetdruk.l2jspace.gameserver.handler.targethandlers.TargetSummon;
import ru.privetdruk.l2jspace.gameserver.handler.targethandlers.TargetUndead;
import ru.privetdruk.l2jspace.gameserver.handler.targethandlers.TargetUnlockable;

public class TargetHandler {
    private final Map<SkillTargetType, ITargetHandler> _entries = new HashMap<>();

    protected TargetHandler() {
        registerHandler(new TargetAlly());
        registerHandler(new TargetArea());
        registerHandler(new TargetAreaCorpseMob());
        registerHandler(new TargetAreaSummon());
        registerHandler(new TargetAura());
        registerHandler(new TargetAuraUndead());
        registerHandler(new TargetBehindAura());
        registerHandler(new TargetClan());
        registerHandler(new TargetCorpseAlly());
        registerHandler(new TargetCorpseMob());
        registerHandler(new TargetCorpsePet());
        registerHandler(new TargetCorpsePlayer());
        registerHandler(new TargetEnemySummon());
        registerHandler(new TargetFrontArea());
        registerHandler(new TargetFrontAura());
        registerHandler(new TargetGround());
        registerHandler(new TargetHoly());
        registerHandler(new TargetOne());
        registerHandler(new TargetOwnerPet());
        registerHandler(new TargetParty());
        registerHandler(new TargetPartyMember());
        registerHandler(new TargetPartyOther());
        registerHandler(new TargetSelf());
        registerHandler(new TargetSummon());
        registerHandler(new TargetUndead());
        registerHandler(new TargetUnlockable());
    }

    private void registerHandler(ITargetHandler handler) {
        _entries.put(handler.getTargetType(), handler);
    }

    public ITargetHandler getHandler(SkillTargetType targetType) {
        return _entries.get(targetType);
    }

    public int size() {
        return _entries.size();
    }

    public static TargetHandler getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final TargetHandler INSTANCE = new TargetHandler();
    }
}