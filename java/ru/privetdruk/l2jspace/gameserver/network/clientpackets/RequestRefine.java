package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.data.xml.AugmentationData;
import ru.privetdruk.l2jspace.gameserver.enums.ShortcutType;
import ru.privetdruk.l2jspace.gameserver.enums.StatusType;
import ru.privetdruk.l2jspace.gameserver.model.Augmentation;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ExVariationResult;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.InventoryUpdate;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.StatusUpdate;

public final class RequestRefine extends AbstractRefinePacket {
    private int _targetItemObjId;
    private int _refinerItemObjId;
    private int _gemStoneItemObjId;
    private int _gemStoneCount;

    @Override
    protected void readImpl() {
        _targetItemObjId = readD();
        _refinerItemObjId = readD();
        _gemStoneItemObjId = readD();
        _gemStoneCount = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        final ItemInstance targetItem = player.getInventory().getItemByObjectId(_targetItemObjId);
        if (targetItem == null) {
            player.sendPacket(ExVariationResult.RESULT_FAILED);
            player.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
            return;
        }

        final ItemInstance refinerItem = player.getInventory().getItemByObjectId(_refinerItemObjId);
        if (refinerItem == null) {
            player.sendPacket(ExVariationResult.RESULT_FAILED);
            player.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
            return;
        }

        final ItemInstance gemStoneItem = player.getInventory().getItemByObjectId(_gemStoneItemObjId);
        if (gemStoneItem == null) {
            player.sendPacket(ExVariationResult.RESULT_FAILED);
            player.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
            return;
        }

        if (!isValid(player, targetItem, refinerItem, gemStoneItem)) {
            player.sendPacket(ExVariationResult.RESULT_FAILED);
            player.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
            return;
        }

        final LifeStone ls = getLifeStone(refinerItem.getItemId());
        if (ls == null) {
            player.sendPacket(ExVariationResult.RESULT_FAILED);
            player.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
            return;
        }

        if (_gemStoneCount != getGemStoneCount(targetItem.getItem().getCrystalType())) {
            player.sendPacket(ExVariationResult.RESULT_FAILED);
            player.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
            return;
        }

        // unequip item
        if (targetItem.isEquipped()) {
            ItemInstance[] unequipped = player.getInventory().unequipItemInSlotAndRecord(targetItem.getLocationSlot());
            InventoryUpdate iu = new InventoryUpdate();

            for (ItemInstance itm : unequipped)
                iu.addModifiedItem(itm);

            player.sendPacket(iu);
            player.broadcastUserInfo();
        }

        // Consume the life stone
        if (!player.destroyItem("RequestRefine", refinerItem, 1, null, false)) {
            player.sendPacket(ExVariationResult.RESULT_FAILED);
            player.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
            return;
        }

        // Consume gemstones
        if (!player.destroyItem("RequestRefine", gemStoneItem, _gemStoneCount, null, false)) {
            player.sendPacket(ExVariationResult.RESULT_FAILED);
            player.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
            return;
        }

        Augmentation augmentation = AugmentationData.getInstance().generateRandomAugmentation(ls.getLevel(), ls.getGrade());
        targetItem.setAugmentation(augmentation);

        final int stat12 = 0x0000FFFF & augmentation.getId();
        final int stat34 = augmentation.getId() >> 16;
        player.sendPacket(new ExVariationResult(stat12, stat34, 1));

        InventoryUpdate iu = new InventoryUpdate();
        iu.addModifiedItem(targetItem);
        player.sendPacket(iu);

        // Refresh shortcuts.
        player.getShortcutList().refreshShortcuts(s -> targetItem.getObjectId() == s.getId() && s.getType() == ShortcutType.ITEM);

        StatusUpdate su = new StatusUpdate(player);
        su.addAttribute(StatusType.CUR_LOAD, player.getCurrentWeight());
        player.sendPacket(su);
    }
}