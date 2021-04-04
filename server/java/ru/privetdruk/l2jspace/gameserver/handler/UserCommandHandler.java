package ru.privetdruk.l2jspace.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import ru.privetdruk.l2jspace.gameserver.handler.usercommandhandlers.ChannelDelete;
import ru.privetdruk.l2jspace.gameserver.handler.usercommandhandlers.ChannelLeave;
import ru.privetdruk.l2jspace.gameserver.handler.usercommandhandlers.ChannelListUpdate;
import ru.privetdruk.l2jspace.gameserver.handler.usercommandhandlers.ClanPenalty;
import ru.privetdruk.l2jspace.gameserver.handler.usercommandhandlers.ClanWarsList;
import ru.privetdruk.l2jspace.gameserver.handler.usercommandhandlers.Dismount;
import ru.privetdruk.l2jspace.gameserver.handler.usercommandhandlers.Escape;
import ru.privetdruk.l2jspace.gameserver.handler.usercommandhandlers.Loc;
import ru.privetdruk.l2jspace.gameserver.handler.usercommandhandlers.Mount;
import ru.privetdruk.l2jspace.gameserver.handler.usercommandhandlers.OlympiadStat;
import ru.privetdruk.l2jspace.gameserver.handler.usercommandhandlers.PartyInfo;
import ru.privetdruk.l2jspace.gameserver.handler.usercommandhandlers.SiegeStatus;
import ru.privetdruk.l2jspace.gameserver.handler.usercommandhandlers.Time;

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