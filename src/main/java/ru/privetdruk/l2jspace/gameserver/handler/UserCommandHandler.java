package ru.privetdruk.l2jspace.gameserver.handler;

import ru.privetdruk.l2jspace.gameserver.handler.usercommandhandlers.*;

import java.util.HashMap;
import java.util.Map;

public class UserCommandHandler {
    private final Map<Integer, IUserCommandHandler> _entries = new HashMap<>();

    protected UserCommandHandler() {
        registerHandler(new ChannelDelete());
        registerHandler(new ChannelLeave());
        registerHandler(new ChannelListUpdate());
        registerHandler(new ClanPenalty());
        registerHandler(new ClanWarsList());
        registerHandler(new Dismount());
        registerHandler(new Escape());
        registerHandler(new Loc());
        registerHandler(new Mount());
        registerHandler(new OlympiadStat());
        registerHandler(new PartyInfo());
        registerHandler(new SiegeStatus());
        registerHandler(new Time());
    }

    private void registerHandler(IUserCommandHandler handler) {
        for (int id : handler.getUserCommandList())
            _entries.put(id, handler);
    }

    public IUserCommandHandler getHandler(int userCommand) {
        return _entries.get(userCommand);
    }

    public int size() {
        return _entries.size();
    }

    public static UserCommandHandler getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final UserCommandHandler INSTANCE = new UserCommandHandler();
    }
}