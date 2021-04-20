package ru.privetdruk.l2jspace.gameserver.custom.model.event;

import ru.privetdruk.l2jspace.gameserver.custom.model.NpcInfoShort;
import ru.privetdruk.l2jspace.gameserver.custom.model.Reward;
import ru.privetdruk.l2jspace.gameserver.model.spawn.Spawn;

public class EventSetting {
    private String eventName;
    private String eventDescription;
    private String registrationLocationName;
    private int minLevel;
    private int maxLevel;
    private int minPlayers;
    private int maxPlayers;
    private NpcInfoShort mainNpc;
    private Spawn spawnMainNpc;
    private Reward reward;
    private int timeRegistration;
    private int durationEvent;
    private long intervalBetweenMatches;

    public EventSetting() {
    }

    public EventSetting(String eventName,
                        String eventDescription,
                        String registrationLocationName,
                        int minLevel,
                        int maxLevel,
                        NpcInfoShort mainNpc,
                        Reward reward,
                        int timeRegistration,
                        int durationEvent,
                        int minPlayers,
                        int maxPlayers,
                        long intervalBetweenMatches) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.registrationLocationName = registrationLocationName;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.mainNpc = mainNpc;
        this.reward = reward;
        this.timeRegistration = timeRegistration;
        this.durationEvent = durationEvent;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.intervalBetweenMatches = intervalBetweenMatches;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getRegistrationLocationName() {
        return registrationLocationName;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public NpcInfoShort getMainNpc() {
        return mainNpc;
    }

    public Reward getReward() {
        return reward;
    }

    public int getTimeRegistration() {
        return timeRegistration;
    }

    public int getDurationEvent() {
        return durationEvent;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public long getIntervalBetweenMatches() {
        return intervalBetweenMatches;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public void setRegistrationLocationName(String registrationLocationName) {
        this.registrationLocationName = registrationLocationName;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public void setMainNpc(NpcInfoShort mainNpc) {
        this.mainNpc = mainNpc;
    }

    public void setReward(Reward reward) {
        this.reward = reward;
    }

    public void setTimeRegistration(int timeRegistration) {
        this.timeRegistration = timeRegistration;
    }

    public void setDurationEvent(int durationEvent) {
        this.durationEvent = durationEvent;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setIntervalBetweenMatches(long intervalBetweenMatches) {
        this.intervalBetweenMatches = intervalBetweenMatches;
    }

    public Spawn getSpawnMainNpc() {
        return spawnMainNpc;
    }

    public void setSpawnMainNpc(Spawn spawnMainNpc) {
        this.spawnMainNpc = spawnMainNpc;
    }
}
