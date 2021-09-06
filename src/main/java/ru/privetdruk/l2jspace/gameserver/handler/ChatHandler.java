package ru.privetdruk.l2jspace.gameserver.handler;

import ru.privetdruk.l2jspace.gameserver.enums.SayType;
import ru.privetdruk.l2jspace.gameserver.handler.chathandlers.*;

import java.util.HashMap;
import java.util.Map;

public class ChatHandler {
    private final Map<SayType, IChatHandler> _entries = new HashMap<>();

    protected ChatHandler() {
        registerHandler(new ChatAll());
        registerHandler(new ChatAlliance());
        registerHandler(new ChatClan());
        registerHandler(new ChatHeroVoice());
        registerHandler(new ChatParty());
        registerHandler(new ChatPartyMatchRoom());
        registerHandler(new ChatPartyRoomAll());
        registerHandler(new ChatPartyRoomCommander());
        registerHandler(new ChatPetition());
        registerHandler(new ChatShout());
        registerHandler(new ChatTell());
        registerHandler(new ChatTrade());
    }

    private void registerHandler(IChatHandler handler) {
        for (SayType type : handler.getChatTypeList())
            _entries.put(type, handler);
    }

    public IChatHandler getHandler(SayType chatType) {
        return _entries.get(chatType);
    }

    public int size() {
        return _entries.size();
    }

    public static ChatHandler getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final ChatHandler INSTANCE = new ChatHandler();
    }
}