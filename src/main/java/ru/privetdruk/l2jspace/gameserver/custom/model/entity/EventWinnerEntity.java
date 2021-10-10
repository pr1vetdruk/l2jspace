package ru.privetdruk.l2jspace.gameserver.custom.model.entity;

import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventType;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventWinnerStatus;

import java.time.LocalDate;

public class EventWinnerEntity {
    private long id;
    private int playerId;
    private EventType eventType;
    private LocalDate victoryDate = LocalDate.now();
    private EventWinnerStatus status = EventWinnerStatus.ACTIVE;

    public EventWinnerEntity() {
    }

    public EventWinnerEntity(long id, int playerId, EventType eventType, LocalDate victoryDate, EventWinnerStatus status) {
        this.id = id;
        this.playerId = playerId;
        this.eventType = eventType;
        this.victoryDate = victoryDate;
        this.status = status;
    }

    @Override
    public String toString() {
        return "EventWinnerEntity{" +
                "id=" + id +
                ", playerId=" + playerId +
                ", eventType=" + eventType +
                ", victoryDate=" + victoryDate +
                ", status=" + status +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public LocalDate getVictoryDate() {
        return victoryDate;
    }

    public void setVictoryDate(LocalDate victoryDate) {
        this.victoryDate = victoryDate;
    }

    public EventWinnerStatus getStatus() {
        return status;
    }

    public void setStatus(EventWinnerStatus status) {
        this.status = status;
    }
}
