package ru.privetdruk.l2jspace.gameserver.custom.builder;

import ru.privetdruk.l2jspace.gameserver.custom.model.NpcInfoShort;
import ru.privetdruk.l2jspace.gameserver.custom.model.Reward;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventSetting;

public class EventSettingBuilder {
    private String name;
    private String description;
    private String registrationLocationName;
    private int minLevel;
    private int maxLevel;
    private NpcInfoShort npc;
    private Reward reward;
    private int timeRegistration;
    private int durationTime;
    private int minPlayers;
    private int maxPlayers;
    private long intervalBetweenMatches;

    public EventSettingBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public EventSettingBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public EventSettingBuilder setRegistrationLocationName(String registrationLocationName) {
        this.registrationLocationName = registrationLocationName;
        return this;
    }

    public EventSettingBuilder setMinLevel(int minLevel) {
        this.minLevel = minLevel;
        return this;
    }

    public EventSettingBuilder setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
        return this;
    }

    public EventSettingBuilder setNpc(NpcInfoShort npc) {
        this.npc = npc;
        return this;
    }

    public EventSettingBuilder setTimeRegistration(int timeRegistration) {
        this.timeRegistration = timeRegistration;
        return this;
    }

    public EventSettingBuilder setDurationTime(int durationTime) {
        this.durationTime = durationTime;
        return this;
    }

    public EventSettingBuilder setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
        return this;
    }

    public EventSettingBuilder setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        return this;
    }

    public EventSettingBuilder setIntervalBetweenMatches(long intervalBetweenMatches) {
        this.intervalBetweenMatches = intervalBetweenMatches;
        return this;
    }

    public EventSetting build() {
        return new EventSetting(name, description, registrationLocationName, minLevel, maxLevel, npc, timeRegistration, durationTime, minPlayers, maxPlayers, intervalBetweenMatches);
    }
}