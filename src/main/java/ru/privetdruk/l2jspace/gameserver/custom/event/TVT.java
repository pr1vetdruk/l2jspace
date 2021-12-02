package ru.privetdruk.l2jspace.gameserver.custom.event;

import ru.privetdruk.l2jspace.common.pool.ConnectionPool;
import ru.privetdruk.l2jspace.config.custom.EventConfig;
import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;
import ru.privetdruk.l2jspace.gameserver.custom.model.NpcInfoShort;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventBorder;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventPlayer;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventType;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.TeamSetting;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.CtfPlayer;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.CtfTeamSetting;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.Flag;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.Throne;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.tvt.TvtPlayer;
import ru.privetdruk.l2jspace.gameserver.custom.service.EventService;
import ru.privetdruk.l2jspace.gameserver.enums.TeamAura;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.location.Location;
import ru.privetdruk.l2jspace.gameserver.model.location.SpawnLocation;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.Ride;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static ru.privetdruk.l2jspace.gameserver.custom.model.SkillEnum.Mount.Wyvern.WYVERN_BREATH;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState.*;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventTeamType.BALANCE;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventTeamType.NO;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.ResultPlayerEvent.*;

public class TVT extends EventEngine {
    private EventBorder eventBorder;
    private final List<TeamSetting> ctfTeamSettings = new ArrayList<>();

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
                ctfTeamSettings.add(
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

            teamSettings.addAll(ctfTeamSettings);

            resultSet.close();
        } catch (Exception e) {
            eventState = ERROR;
            logError("loadData", e);
        }
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

        if (EventConfig.CTF.AURA) {
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

        if (EventConfig.CTF.AURA && teamSettings.size() == 2) {
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
        Map.Entry<Integer, List<TeamSetting>> winningTeamEntry = ctfTeamSettings.stream()
                .filter(team -> team.getPoints() > 0)
                .collect(
                        Collectors.groupingBy(
                                TeamSetting::getPoints,
                                TreeMap::new,
                                Collectors.toList()
                        )
                ).lastEntry();

        if (winningTeamEntry != null) {
            List<TeamSetting> winningTeamList = winningTeamEntry.getValue();

            for (EventPlayer eventPlayer : players.values()) {
                boolean isWinner = winningTeamList.contains(eventPlayer.getTeamSettings());
                boolean isTie = winningTeamEntry.getValue().size() > 1;

                playAnimation(eventPlayer.getPlayer(), isWinner);
                giveReward(eventPlayer.getPlayer(), isTie ? TIE : isWinner ? WON : LOST);
            }


            if (EventConfig.TVT.ANNOUNCE_TEAM_STATS) {
                announceCritical("Статистика:");
                for (TeamSetting team : ctfTeamSettings) {
                    announceCritical("Команда: " + team.getName() + " - сделал убийств: " + team.getPoints());
                }
            }

            TeamSetting winningTeam = winningTeamList.get(0);

            if (winningTeamList.size() == 1) {
                announceCritical("Поздравляем! Победила команда - " + winningTeam.getName() + "!");
            } else {
                announceCritical("Ивент завершился в ничью!");
            }
        } else {
            announceCritical("Ни одной команде не удалось принести флаг на свою базу!");
        }
    }

    @Override
    public void register(Player player) {

    }

    @Override
    public void register(Player player, String teamName) {

    }

    @Override
    public void addDisconnectedPlayer(Player player) {

    }

    @Override
    public String configureMainPageContent(Player player) {
        return null;
    }

    @Override
    public void doDie(Player player, Player playerKiller) {

    }

    @Override
    public boolean isAllowedTeleportAfterDeath() {
        return false;
    }

    @Override
    protected void announceRewardsAfter() {

    }
}
