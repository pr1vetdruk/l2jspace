package ru.privetdruk.l2jspace.gameserver.custom.event;

import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventPlayer;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.TeamSetting;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.CtfEventPlayer;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.CtfTeamSetting;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.model.itemcontainer.Inventory;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.InventoryUpdate;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ItemList;

public class CTF extends EventEngine {
    @Override
    protected boolean preLaunchChecksCustom() {
        return false;
    }

    @Override
    protected void restorePlayerDataCustom(EventPlayer eventPlayer) {

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

    private void removeFlagFromPlayer(CtfEventPlayer eventPlayer) {
        int flagItemId = eventPlayer.getTeamSettings().getFlag().getItemId();

        Player player = eventPlayer.getPlayer();

        if (!eventPlayer.isHaveFlag()) {
            player.getInventory().destroyItemByItemId("", flagItemId, 1, player, null);
            return;
        }

        eventPlayer.setHaveFlag(false);
        player.teamNameHaveFlagCtf = null;

        ItemInstance weaponEquipped = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LRHAND);

        // Get your weapon back now ...
        if (weaponEquipped != null) {
            ItemInstance[] unequipped = player.getInventory().unEquipItemInBodySlotAndRecord(weaponEquipped.getItem().getBodyPart());

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
        player.abortAttack();
        player.broadcastUserInfo();
    }
}
