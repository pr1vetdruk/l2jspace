package ru.privetdruk.l2jspace.gameserver.custom.event;

import ru.privetdruk.l2jspace.common.pool.ConnectionPool;
import ru.privetdruk.l2jspace.config.custom.EventConfig;
import ru.privetdruk.l2jspace.gameserver.custom.builder.EventSettingBuilder;
import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;
import ru.privetdruk.l2jspace.gameserver.custom.model.NpcInfoShort;
import ru.privetdruk.l2jspace.gameserver.custom.model.Reward;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventBorder;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventPlayer;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventType;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.CtfEventPlayer;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.CtfTeamSetting;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.Flag;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.Throne;
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

import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState.ERROR;

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

            statement.close();
            resultSet.close();
        } catch (Exception e) {
            eventState = ERROR;
            LOGGER.severe("Exception: CTF.loadData(): " + e.getMessage());
        }
    }

    @Override
    protected boolean preLaunchChecksCustom() {
        return false;
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
