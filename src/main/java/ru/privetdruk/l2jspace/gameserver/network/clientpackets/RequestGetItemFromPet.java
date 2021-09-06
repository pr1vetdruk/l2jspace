package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Pet;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.EnchantResult;

public final class RequestGetItemFromPet extends L2GameClientPacket {
    private int _objectId;
    private int _amount;
    @SuppressWarnings("unused")
    private int _unknown;

    @Override
    protected void readImpl() {
        _objectId = readD();
        _amount = readD();
        _unknown = readD();// = 0 for most trades
    }

    @Override
    protected void runImpl() {
        if (_amount <= 0)
            return;

        final Player player = getClient().getPlayer();
        if (player == null || !player.hasPet())
            return;

        if (player.isProcessingTransaction()) {
            player.sendPacket(SystemMessageId.ALREADY_TRADING);
            return;
        }

        if (player.getActiveEnchantItem() != null) {
            player.setActiveEnchantItem(null);
            player.sendPacket(EnchantResult.CANCELLED);
            player.sendPacket(SystemMessageId.ENCHANT_SCROLL_CANCELLED);
        }

        final Pet pet = (Pet) player.getSummon();

        pet.transferItem("Transfer", _objectId, _amount, player.getInventory(), player, pet);
    }
}