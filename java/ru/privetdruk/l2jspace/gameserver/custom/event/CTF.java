package ru.privetdruk.l2jspace.gameserver.custom.event;

import ru.privetdruk.l2jspace.common.pool.ConnectionPool;
import ru.privetdruk.l2jspace.config.custom.EventConfig;
import ru.privetdruk.l2jspace.gameserver.custom.builder.EventSettingBuilder;
import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;
import ru.privetdruk.l2jspace.gameserver.custom.model.NpcInfoShort;
import ru.privetdruk.l2jspace.gameserver.custom.model.Reward;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.*;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.CtfEventPlayer;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.CtfTeamSetting;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.Flag;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.Throne;
import ru.privetdruk.l2jspace.gameserver.data.xml.ItemData;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.model.location.Location;
import ru.privetdruk.l2jspace.gameserver.model.location.SpawnLocation;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.InventoryUpdate;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ItemList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventBypass.JOIN_TEAM;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventBypass.LEAVE;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState.ERROR;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState.IN_PROGRESS;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState.REGISTRATION;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventTeamType.BALANCE;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventTeamType.NO;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventTeamType.SHUFFLE;

public class CTF extends EventEngine {
    private EventBorder eventBorder;
    private final List<CtfTeamSetting> ctfTeamSettings = new ArrayList<>();

    public CTF() {
        super(EventType.CTF, EventConfig.CTF.TEAM_MODE, EventConfig.CTF.UNSUMMON_PET, EventConfig.CTF.REMOVE_ALL_EFFECTS);

        eventTaskList.add(this);
    }

    @Override
    public void loadData(int eventId) {
        try (Connection connection = ConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM event WHERE id = ? AND type = ?");
            statement.setInt(1, eventId);
            statement.setString(2, eventType.name());

            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                LOGGER.warning("Setting ctf not found!");
                return;
            }

            settings = new EventSettingBuilder()
                    .setName(resultSet.getString("name"))
                    .setDescription(resultSet.getString("description"))
                    .setRegistrationLocationName(resultSet.getString("registration_location"))
                    .setMinLevel(resultSet.getInt("min_level"))
                    .setMaxLevel(resultSet.getInt("max_level"))
                    .setNpc(new NpcInfoShort(
                            resultSet.getInt("npc_id"),
                            new SpawnLocation(
                                    resultSet.getInt("npc_x"),
                                    resultSet.getInt("npc_y"),
                                    resultSet.getInt("npc_z"),
                                    resultSet.getInt("npc_heading")
                            )
                    ))
                    .setReward(new Reward(
                            resultSet.getInt("reward_id"),
                            resultSet.getInt("reward_amount")
                    ))
                    .setTimeRegistration(resultSet.getInt("time_registration"))
                    .setDurationTime(resultSet.getInt("duration_event"))
                    .setMinPlayers(resultSet.getInt("min_players"))
                    .setMaxPlayers(resultSet.getInt("max_players"))
                    .setIntervalBetweenMatches(resultSet.getLong("delay_next_event"))
                    .build();

            statement.close();
            resultSet.close();

            statement = connection.prepareStatement("SELECT * FROM event_ctf_team_setting WHERE event_id = ?");
            statement.setInt(1, eventId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                ctfTeamSettings.add(
                        new CtfTeamSetting(
                                resultSet.getInt("id"),
                                resultSet.getString("name"),
                                resultSet.getInt("name_color"),
                                resultSet.getInt("offset"),
                                new Location(
                                        resultSet.getInt("position_x"),
                                        resultSet.getInt("position_y"),
                                        resultSet.getInt("position_z")
                                ),
                                new Flag(
                                        new NpcInfoShort(
                                                resultSet.getInt("flag_npc_id"),
                                                new SpawnLocation(
                                                        resultSet.getInt("flag_position_x"),
                                                        resultSet.getInt("flag_position_y"),
                                                        resultSet.getInt("flag_position_z")
                                                )
                                        ),
                                        resultSet.getInt("flag_item_id"),
                                        false
                                ),
                                new Throne(
                                        new NpcInfoShort(
                                                resultSet.getInt("throne_npc_id"),
                                                new SpawnLocation(
                                                        resultSet.getInt("flag_position_x"),
                                                        resultSet.getInt("flag_position_y"),
                                                        resultSet.getInt("flag_position_z") - resultSet.getInt("offset_throne_position_z")
                                                )
                                        ),
                                        resultSet.getInt("offset_throne_position_z")
                                )
                        )
                );
            }

            teamSettings.addAll(ctfTeamSettings);

            statement.close();
            resultSet.close();
        } catch (Exception e) {
            eventState = ERROR;
            LOGGER.severe("Exception: CTF.loadData(): " + e.getMessage());
        }
    }

    @Override
    protected boolean preLaunchChecksCustom() {
        return true;
    }

    @Override
    protected void restorePlayerDataCustom(EventPlayer player) {

    }

    @Override
    protected void updatePlayerEventData() {

    }

    @Override
    protected void spawnOtherNpc() {

    }

    @Override
    protected void unspawnNpcCustom() {

    }

    @Override
    protected void abortCustom() {

    }

    @Override
    protected void determineWinner() {

    }

    @Override
    public String configurePageContent(Player player) {
        int playerLevel = player.getStatus().getLevel();
        int npcId = settings.getMainNpc().getId();

        StringBuilder content = new StringBuilder();

        if (eventState != IN_PROGRESS && eventState != REGISTRATION) {
            content.append("<center>Wait till the admin/gm start the participation.</center>");
        } else if (eventState != IN_PROGRESS && teamMode == SHUFFLE && players.size() >= settings.getMaxPlayers()) {
            content.append("Currently participated: <font color=\"00FF00\">").append(players.size()).append(".</font><br>");
            content.append("Max players: <font color=\"00FF00\">").append(settings.getMaxPlayers()).append("</font><br><br>");
            content.append("<font color=\"FFFF00\">You can't participate to this event.</font><br>");
        } else if (player.isCursedWeaponEquipped() && !EventConfig.CTF.JOIN_CURSED_WEAPON) {
            content.append("<font color=\"FFFF00\">You can't participate to this event with a cursed Weapon.</font><br>");
        } else {
            if (eventState == REGISTRATION
                    && playerLevel >= settings.getMinLevel() && playerLevel <= settings.getMaxLevel()) {
                EventPlayer eventPlayer = players.get(player.getObjectId());

                if (eventPlayer != null) {
                    if (teamMode == NO || teamMode == BALANCE) {
                        content.append("You participated already in team <font color=\"LEVEL\">").append(eventPlayer.getTeamSettings().getName()).append("</font><br><br>");
                    } else if (teamMode == SHUFFLE) {
                        content.append("<center><font color=\"3366CC\">You participated already!</font></center><br><br>");
                    }

                    content.append("<center>Joined Players: <font color=\"00FF00\">").append(players.size()).append("</font></center><br>");
                    content.append("<center><font color=\"3366CC\">Wait till event start or remove your participation!</font><center>");
                    content.append("<center><button value=\"Remove\" action=\"bypass -h npc_")
                            .append(npcId)
                            .append(LEAVE.getBypass())
                            .append("\" width=85 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></center>");
                } else {
                    content.append("<center><font color=\"3366CC\">You want to participate in the event?</font></center><br>");
                    content.append("<center><td width=\"200\">Min lvl: <font color=\"00FF00\">").append(settings.getMinLevel()).append("</font></center></td><br>");
                    content.append("<center><td width=\"200\">Max lvl: <font color=\"00FF00\">").append(settings.getMaxLevel()).append("</font></center></td><br><br>");
                    content.append("<center><font color=\"3366CC\">Teams:</font></center><br>");

                    if (teamMode == NO || teamMode == BALANCE) {
                        content.append("<center><table border=\"0\">");
                        for (TeamSetting team : teamSettings) {
                            content.append("<tr><td width=\"100\"><font color=\"LEVEL\">")
                                    .append(team.getName()).append("</font>&nbsp;(").append(team.getPlayers()).append(" joined)</td>");
                            content.append("<center><td width=\"60\"><button value=\"Join\" action=\"bypass -h npc_")
                                    .append(npcId).append(JOIN_TEAM.getBypass()).append(team.getName())
                                    .append("\" width=85 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></center></td></tr>");
                        }
                        content.append("</table></center>");
                    } else if (teamMode == SHUFFLE) {
                        content.append("<center>");

                        for (TeamSetting team : teamSettings) {
                            content.append("<tr><td width=\"100\"><font color=\"LEVEL\">").append(team.getName()).append("</font> &nbsp;</td>");
                        }

                        content.append("</center><br>");

                        content.append("<center><button value=\"Join Event\" action=\"bypass -h npc_")
                                .append(npcId)
                                .append(JOIN_TEAM.getBypass())
                                .append("eventShuffle\" width=85 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></center>");
                        content.append("<center><font color=\"3366CC\">Teams will be randomly generated!</font></center><br>");
                        content.append("<center>Joined Players:</font> <font color=\"LEVEL\">").append(players.size()).append("</center></font><br>");
                        content.append("<center>Reward: <font color=\"LEVEL\">").append(settings.getReward().getAmount())
                                .append(" ").append(ItemData.getInstance().getTemplate(settings.getReward().getId()).getName()).append("</center></font>");
                    }
                }
            } else if (eventState == IN_PROGRESS) {
                content.append("<center>").append(settings.getEventName()).append(" match is in progress.</center>");
            } else if (playerLevel < settings.getMinLevel() || playerLevel > settings.getMaxLevel()) {
                content.append("Your lvl: <font color=\"00FF00\">").append(playerLevel).append("</font><br>");
                content.append("Min lvl: <font color=\"00FF00\">").append(settings.getMinLevel()).append("</font><br>");
                content.append("Max lvl: <font color=\"00FF00\">").append(settings.getMaxLevel()).append("</font><br><br>");
                content.append("<font color=\"FFFF00\">You can't participate to this event.</font><br>");
            }
        }

        return content.toString();
    }

    @Override
    public void registerPlayer(Player player, String teamName) {
        if (!checkPlayerBeforeRegistration(player) && !checkTeamBeforeRegistration(player, teamName)) {
            return;
        }

        TeamSetting team = null;

        if (teamMode == NO || teamMode == BALANCE) {
            team = findTeam(teamName);
            team.addPlayer();
        }

        players.put(player.getObjectId(), new EventPlayer(player, team));

        sendPlayerMessage(player, "You successfully registered for the event.");
    }

    private void removeFlagFromPlayer(CtfEventPlayer eventPlayer) {
        int flagItemId = eventPlayer.getTeamSettings().getFlag().getItemId();

        Player player = eventPlayer.getPlayer();

        if (!eventPlayer.isHaveFlag()) {
            player.getInventory().destroyItemByItemId("", flagItemId, 1, player, null);
            return;
        }

        eventPlayer.setHaveFlag(false);

        ItemInstance weaponEquipped = player.getInventory().getPaperdollItems().stream()
                .filter(item -> item.getItemId() == flagItemId)
                .findFirst()
                .orElse(null);

        // Get your weapon back now ...
        if (weaponEquipped != null) {
            ItemInstance[] unequipped = player.getInventory().unequipItemInBodySlotAndRecord(weaponEquipped);

            player.getInventory().destroyItemByItemId("", flagItemId, 1, player, null);

            InventoryUpdate inventoryUpdate = new InventoryUpdate();

            for (ItemInstance element : unequipped) {
                inventoryUpdate.addModifiedItem(element);
            }

            player.sendPacket(inventoryUpdate);
        } else {
            player.getInventory().destroyItemByItemId("", flagItemId, 1, player, null);
        }

        player.sendPacket(new ItemList(player, true)); // Get your weapon back now ...
        player.getAttack().stop();
        player.broadcastUserInfo();
    }
}
