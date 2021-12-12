package ru.privetdruk.l2jspace.gameserver.custom.event;

import ru.privetdruk.l2jspace.common.pool.ConnectionPool;
import ru.privetdruk.l2jspace.common.pool.ThreadPool;
import ru.privetdruk.l2jspace.config.custom.EventConfig;
import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.*;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.tvt.TvtPlayer;
import ru.privetdruk.l2jspace.gameserver.custom.service.EventService;
import ru.privetdruk.l2jspace.gameserver.data.xml.ItemData;
import ru.privetdruk.l2jspace.gameserver.enums.SayType;
import ru.privetdruk.l2jspace.gameserver.enums.TeamAura;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.location.Location;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ActionFailed;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.CreatureSay;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.Ride;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static ru.privetdruk.l2jspace.common.util.StringUtil.SECOND_WORDS;
import static ru.privetdruk.l2jspace.common.util.StringUtil.declensionWords;
import static ru.privetdruk.l2jspace.gameserver.custom.model.SkillEnum.Mount.Wyvern.WYVERN_BREATH;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventBypass.JOIN_TEAM;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventBypass.LEAVE;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState.*;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventTeamType.*;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventTeamType.SHUFFLE;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.ResultEvent.*;

public class TVT extends EventEngine {
    private EventBorder eventBorder;

    public TVT() {
        super(
                EventType.TVT,
                EventConfig.TVT.TEAM_MODE,
                EventConfig.TVT.UNSUMMON_PET,
                EventConfig.TVT.REMOVE_ALL_EFFECTS,
                EventConfig.TVT.JOIN_CURSED_WEAPON,
                EventConfig.TVT.REMOVE_BUFFS_ON_DIE
        );

        eventTaskList.add(this);
    }

    @Override
    protected void startEventCustom() {
        allowPlayersToMove();
        sitPlayers();
    }

    @Override
    protected void announceStart() {
        announceCritical("Вперед!");
    }

    @Override
    public void loadData(int eventId) {
        try (Connection connection = ConnectionPool.getConnection()) {
            settings = EventService.getInstance().findEventSetting(eventId, eventType);

            if (settings == null) {
                return;
            }

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM event_tvt_team_setting WHERE event_id = ?");
            statement.setInt(1, eventId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                teamSettings.add(
                        new TeamSetting(
                                resultSet.getInt("id"),
                                resultSet.getString("name"),
                                resultSet.getString("name_color"),
                                resultSet.getInt("offset"),
                                new Location(
                                        resultSet.getInt("position_x"),
                                        resultSet.getInt("position_y"),
                                        resultSet.getInt("position_z")
                                )
                        )
                );
            }

            resultSet.close();
        } catch (Exception e) {
            eventState = ERROR;
            logError("loadData", e);
        }
    }

    @Override
    public boolean allowPotions() {
        return EventConfig.TVT.ALLOW_POTIONS;
    }

    @Override
    public boolean isSitForced() {
        return eventState == TELEPORTATION || eventState == FINISH;
    }

    @Override
    protected boolean preLaunchChecksCustom() {
        return true;
    }


    @Override
    protected void restorePlayerDataCustom(EventPlayer eventPlayer) {
        Player player = eventPlayer.getPlayer();

        player.getAppearance().setNameColor(eventPlayer.getOriginalColorName());
        player.setTitle(eventPlayer.getOriginalTitle());

        if (eventPlayer.getOriginalKarma() > 0) {
            player.setKarma(eventPlayer.getOriginalKarma());
        }

        if (EventConfig.TVT.AURA) {
            player.setTeamAura(TeamAura.NONE);
        }

        player.broadcastUserInfo();

        if (teamMode == NO || teamMode == BALANCE) {
            eventPlayer.getTeamSettings().removePlayer();
        }
    }

    @Override
    protected void updatePlayerEventDataCustom(EventPlayer eventPlayer) {
        TvtPlayer tvtPlayer = (TvtPlayer) eventPlayer;
        Player player = tvtPlayer.getPlayer();
        TeamSetting team = tvtPlayer.getTeamSettings();

        player.getAppearance().setNameColor(team.getColor());
        if (player.getKarma() > 0) {
            player.setKarma(0);
        }

        if (EventConfig.TVT.AURA && teamSettings.size() == 2) {
            player.setTeamAura(TeamAura.fromId(teamSettings.indexOf(team) + 1));
        }

        if (player.isMounted()) {
            if (player.isFlying()) {
                player.removeSkill(WYVERN_BREATH.getId(), true);
            }

            player.broadcastPacket(new Ride(player.getId(), Ride.ACTION_DISMOUNT, 0));
            player.dismount();
        }

        player.broadcastUserInfo();
    }

    @Override
    protected void spawnOtherNpc() {
    }

    @Override
    protected void unspawnNpcCustom() {

    }

    @Override
    protected void cancelEventCustom() {

    }

    @Override
    protected void determineWinner() {
        Map.Entry<Integer, List<TeamSetting>> winningTeamEntry = teamSettings.stream()
                .filter(team -> team.getPoints() > 0)
                .collect(
                        Collectors.groupingBy(
                                TeamSetting::getPoints,
                                TreeMap::new,
                                Collectors.toList()
                        )
                ).lastEntry();

        if (winningTeamEntry == null) {
            announceCritical("Не удалось определить победителя :<");
            return;
        }

        List<TeamSetting> winningTeamList = winningTeamEntry.getValue();

        boolean isTie = winningTeamEntry.getValue().size() > 1;

        TeamSetting winnerTeam = winningTeamList.get(0);

        List<EventPlayer> top3Players = players.values().stream()
                .sorted(Comparator.comparingInt(EventPlayer::getKills).reversed())
                .limit(3)
                .toList();

        if (winningTeamList.size() == 1) {
            announceCritical("Поздравляем! Победила команда - " + winnerTeam.getName() + "!");
        } else {
            announceCritical("Ивент завершился в ничью!");
        }

        if (EventConfig.TVT.ANNOUNCE_TEAM_STATS) {
            announceCritical("Статистика:");
            for (TeamSetting team : teamSettings) {
                announceCritical(" - Команда: " + team.getName() + " - сделала убийств: " + team.getPoints());
            }

            announceCritical("Топ 3 по убийствам:");
            for (int place = 1; place <= top3Players.size(); place++) {
                EventPlayer eventPlayer = top3Players.get(place - 1);
                announceCritical(" - " + place + " место: " + eventPlayer.getPlayer().getName() + " сделал " + eventPlayer.getKills() + " убийств");
            }
        }

        for (EventPlayer eventPlayer : players.values()) {
            boolean isWinner = winningTeamList.contains(eventPlayer.getTeamSettings());

            playAnimation(eventPlayer.getPlayer(), isWinner);
            giveReward(eventPlayer.getPlayer(), isTie ? TIE : isWinner ? WON : LOST);
        }
    }

    private void giveReward(Player player, ResultEvent result) {
        if (player == null || !player.isOnline() || !player.isEventPlayer()) {
            return;
        }

        settings.getRewards().forEach(reward ->
                player.addItem(
                        settings.getEventName(),
                        reward.getId(),
                        result == WON ? reward.getAmount() : reward.getAmount() / 2,
                        player,
                        true
                ));

        player.sendPacket(new CreatureSay(player.getId(), SayType.PARTYROOM_COMMANDER, settings.getEventName(), result.name()));
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }

    @Override
    public void register(Player player) {

    }

    @Override
    public void register(Player player, String teamName) {
        if (!checkPlayerBeforeRegistration(player) && !checkTeamBeforeRegistration(player, teamName)) {
            return;
        }

        TeamSetting team = null;

        if (teamMode == NO || teamMode == BALANCE) {
            team = findTeam(teamName);
            team.addPlayer();
        }

        players.put(player.getId(), new TvtPlayer(player, team));

        sendPlayerMessage(player, "Вы успешно зарегистрировались на ивент.");
    }

    @Override
    public void addDisconnectedPlayer(Player player) {
        switch (eventState) {
            case TELEPORTATION, PREPARE_FOR_START, IN_PROGRESS -> {
                EventPlayer eventPlayer = allPlayers.get(player.getId());

                if (eventPlayer != null) {
                    eventPlayer.setPlayer(player);
                    player.setEventPlayer(eventPlayer);
                    players.put(player.getId(), eventPlayer); // adding new objectId to vector

                    updatePlayerEventDataCustom(eventPlayer);
                    teleport(eventPlayer);
                }
            }
        }
    }

    @Override
    public String configureMainPageContent(Player player) {
        int playerLevel = player.getStatus().getLevel();

        StringBuilder content = new StringBuilder();

        if (eventState != IN_PROGRESS && teamMode == SHUFFLE && players.size() >= settings.getMaxPlayers()) {
            content.append("Участников: <font color=\"00FF00\">").append(players.size()).append(".</font><br>");
            content.append("Максимум игроков: <font color=\"00FF00\">").append(settings.getMaxPlayers()).append("</font><br><br>");
            content.append("<font color=\"FFFF00\">Вы не можете участвовать в этом ивенте.</font><br>");
        } else if (player.isCursedWeaponEquipped() && !JOIN_CURSED_WEAPON) {
            content.append("<font color=\"FFFF00\">Вы не можете участвовать в этом ивенте с проклятым оружием.</font><br>");
        } else {
            if (eventState == REGISTRATION
                    && playerLevel >= settings.getMinLevel() && playerLevel <= settings.getMaxLevel()) {
                EventPlayer eventPlayer = players.get(player.getId());

                if (eventPlayer != null) {
                    if (teamMode == NO || teamMode == BALANCE) {
                        content.append("Вы уже участвуете в команде <font color=\"LEVEL\">").append(eventPlayer.getTeamSettings().getName()).append("</font><br><br>");
                    } else if (teamMode == SHUFFLE) {
                        content.append("<center><font color=\"3366CC\">Вы уже принимаете участие!</font></center><br><br>");
                    }

                    content.append("<center>Участников: <font color=\"00FF00\">").append(players.size()).append("</font></center><br>");
                    content.append("<center><font color=\"3366CC\">Дождитесь начала ивента или откажитесь от участия!</font><center>");
                    content.append("<center><button value=\"Покинуть\" action=\"bypass -h npc_%objectId%_")
                            .append(LEAVE.getBypass())
                            .append("\" width=\"90\" height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></center>");
                } else {
                    content.append("<center><font color=\"3366CC\">Вы хотите принять участие в ивенте?</font></center><br>");
                    content.append("<center><td width=\"200\">Минимальный уровень: <font color=\"00FF00\">").append(settings.getMinLevel()).append("</font></center></td><br>");
                    content.append("<center><td width=\"200\">Максимальный уровень: <font color=\"00FF00\">").append(settings.getMaxLevel()).append("</font></center></td><br><br>");
                    content.append("<center><font color=\"3366CC\">Команды:</font></center><br>");

                    if (teamMode == NO || teamMode == BALANCE) {
                        content.append("<center><table border=\"0\">");
                        for (TeamSetting team : teamSettings) {
                            content.append("<tr><td width=\"100\"><font color=\"LEVEL\">")
                                    .append(team.getName()).append("</font>&nbsp;(").append(team.getPlayers()).append(" участников)</td>");
                            content.append("<center><td width=\"90\"><button value=\"Участвовать\" action=\"bypass -h npc_%objectId%_")
                                    .append(JOIN_TEAM.getBypass())
                                    .append(" ")
                                    .append(team.getName())
                                    .append("\" width=\"60\" height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></center></td></tr>");
                        }
                        content.append("</table></center>");
                    } else if (teamMode == SHUFFLE) {
                        content.append("<center>");

                        for (TeamSetting team : teamSettings) {
                            content.append("<tr><td width=\"100\"><font color=\"LEVEL\">").append(team.getName()).append("</font> &nbsp;</td>");
                        }

                        content.append("</center><br>");

                        content.append("<center><button value=\"Участвовать\" action=\"bypass -h npc_%objectId%_")
                                .append(JOIN_TEAM.getBypass())
                                .append(" eventShuffle\" \"90\" height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></center>");
                        content.append("<center><font color=\"3366CC\">Команды будут выбраны случайным образом!</font></center><br>");
                        content.append("<center>Участников:</font> <font color=\"LEVEL\">").append(players.size()).append("</center></font><br>");
                        content.append("<center>Награда:</center>");

                        settings.getRewards().forEach(reward -> {
                            content.append("<center><font color=\"LEVEL\">").append(reward.getAmount())
                                    .append(" ").append(ItemData.getInstance().getTemplate(reward.getId()).getName()).append("</center></font>");
                        });
                    }
                }
            } else if (eventState == IN_PROGRESS) {
                content.append("<center>К сожалению ивент ").append(settings.getEventName()).append(" уже начался.</center>");
            } else if (playerLevel < settings.getMinLevel() || playerLevel > settings.getMaxLevel()) {
                content.append("Ваш уровень: <font color=\"00FF00\">").append(playerLevel).append("</font><br>");
                content.append("Минимальный уровень: <font color=\"00FF00\">").append(settings.getMinLevel()).append("</font><br>");
                content.append("Максимальный уровень: <font color=\"00FF00\">").append(settings.getMaxLevel()).append("</font><br><br>");
                content.append("<font color=\"FFFF00\">Вы не можете участвовать в этом ивенте.</font><br>");
            }
        }

        return content.toString();
    }

    @Override
    public void doDie(Player player, Player playerKiller) {
        sendPlayerMessage(player, String.format(
                "Вы будете воскрешены и перемещены на респавн через %d %s!",
                EventConfig.TVT.DELAY_BEFORE_REVIVE,
                declensionWords(EventConfig.TVT.DELAY_BEFORE_REVIVE, SECOND_WORDS)
        ));

        EventPlayer eventPlayerKiller = playerKiller.getEventPlayer();
        if (eventPlayerKiller == null) {
            LOGGER.warning("WTF? TVT bug...");
        } else {
            TeamSetting teamSettings = eventPlayerKiller.getTeamSettings();
            teamSettings.addPoint();
            eventPlayerKiller.addMurder();
        }

        ThreadPool.schedule(() -> {
            player.doRevive();
            player.teleToLocation(player.getEventPlayer().getTeamSettings().getSpawnLocation());
        }, TimeUnit.SECONDS.toMillis(EventConfig.TVT.DELAY_BEFORE_REVIVE));
    }

    @Override
    public boolean isAllowedTeleportAfterDeath() {
        return false;
    }

    @Override
    protected void announceRewardsAfter() {

    }
}
