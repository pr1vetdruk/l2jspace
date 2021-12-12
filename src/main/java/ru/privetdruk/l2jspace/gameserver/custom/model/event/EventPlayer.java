package ru.privetdruk.l2jspace.gameserver.custom.model.event;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

public class EventPlayer {
    /**
     * Игрок
     */
    private Player player;

    /**
     * Настройки команды игрока
     */
    private TeamSetting teamSettings;

    /**
     * Разрешено ли ходить
     */
    private boolean allowedToWalk = true;

    /**
     * Может атаковать
     */
    private boolean canAttack = true;

    /**
     * Нанесенный урон
     */
    private long damageDone = 0L;

    /**
     * Соперник
     */
    private EventPlayer rival;

    /**
     * Цвет имени до старта ивента
     */
    private final int originalColorName;

    /**
     * Кол-во кармы до старта ивента
     */
    private final int originalKarma;

    /**
     * Значение титула до старта ивента
     */
    private final String originalTitle;

    private int kills = 0;

    public EventPlayer(Player player, TeamSetting teamSettings) {
        this.player = player;
        this.teamSettings = teamSettings;
        this.originalColorName = player.getAppearance().getNameColor();
        this.originalKarma = player.getKarma();
        this.originalTitle = player.getTitle();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public TeamSetting getTeamSettings() {
        return teamSettings;
    }

    public void setTeamSettings(TeamSetting teamSettings) {
        this.teamSettings = teamSettings;
    }

    public int getOriginalColorName() {
        return originalColorName;
    }

    public int getOriginalKarma() {
        return originalKarma;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public boolean isAllowedToWalk() {
        return allowedToWalk;
    }

    public void setAllowedToWalk(boolean allowedToWalk) {
        this.allowedToWalk = allowedToWalk;
    }

    public EventPlayer getRival() {
        return rival;
    }

    public void setRival(EventPlayer rival) {
        this.rival = rival;
    }

    public void addDamageDone(long damageDone) {
        this.damageDone += damageDone;
    }

    public long getDamageDone() {
        return damageDone;
    }

    public void resetDamage() {
        damageDone = 0L;
    }

    public boolean isCanAttack() {
        return canAttack;
    }

    public void setCanAttack(boolean canAttack) {
        this.canAttack = canAttack;
    }

    public void addMurder() {
        kills++;
    }

    public int getKills() {
        return kills;
    }
}
