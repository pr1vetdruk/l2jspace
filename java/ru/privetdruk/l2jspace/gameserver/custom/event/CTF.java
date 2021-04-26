package ru.privetdruk.l2jspace.gameserver.custom.event;

import ru.privetdruk.l2jspace.config.custom.event.EventConfig;
import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.*;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.CtfEventPlayer;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.CtfTeamSetting;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.model.itemcontainer.Inventory;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.InventoryUpdate;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ItemList;

import java.util.ArrayList;
import java.util.List;

public class CTF extends EventEngine {
    private EventBorder eventBorder;
    private List<CtfTeamSetting> ctfTeamSettings;

    public CTF(EventType eventType, EventTeamType teamMode, boolean onStartUnsummonPet, boolean onStartRemoveAllEffects) {
        super(EventType.CTF, EventConfig.CTF.TEAM_MODE, EventConfig.CTF.ON_START_UNSUMMON_PET, EventConfig.CTF.ON_START_REMOVE_ALL_EFFECTS);
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
