package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.handler.IItemHandler;
import ru.privetdruk.l2jspace.gameserver.handler.ItemHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Pet;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.PetItemList;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;

public final class RequestPetUseItem extends L2GameClientPacket {
    private int _objectId;

    @Override
    protected void readImpl() {
        _objectId = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null || !player.hasPet())
            return;

        final Pet pet = (Pet) player.getSummon();

        final ItemInstance item = pet.getInventory().getItemByObjectId(_objectId);
        if (item == null)
            return;

        if (player.isAlikeDead() || pet.isDead()) {
            player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addItemName(item));
            return;
        }

        if (!item.isEquipped() && !item.getItem().checkCondition(pet, pet, true))
            return;

        // Check if item is pet armor or pet weapon
        if (item.isPetItem()) {
            // Verify if the pet can wear that item
            if (pet.canWear(item.getItem())) {
                player.sendPacket(SystemMessageId.PET_CANNOT_USE_ITEM);
                return;
            }

            if (item.isEquipped()) {
                pet.getInventory().unequipItemInSlot(item.getLocationSlot());
                player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_TOOK_OFF_S1).addItemName(item));
            } else {
                pet.getInventory().equipPetItem(item);
                player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_PUT_ON_S1).addItemName(item));
            }

            player.sendPacket(new PetItemList(pet));
            pet.updateAndBroadcastStatus(1);
            return;
        }

        IItemHandler handler = ItemHandler.getInstance().getHandler(item.getEtcItem());
        if (handler == null || (!pet.getTemplate().canEatFood(item.getItemId()) && !item.isPotion())) {
            player.sendPacket(SystemMessageId.PET_CANNOT_USE_ITEM);
            return;
        }

        handler.useItem(pet, item, false);
        pet.updateAndBroadcastStatus(1);
    }
}