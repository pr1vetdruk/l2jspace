package ru.privetdruk.l2jspace.gameserver.handler.itemhandlers;

import ru.privetdruk.l2jspace.gameserver.enums.items.ShotType;
import ru.privetdruk.l2jspace.gameserver.enums.items.WeaponType;
import ru.privetdruk.l2jspace.gameserver.handler.IItemHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.holder.IntIntHolder;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Weapon;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.MagicSkillUse;

public class FishShots implements IItemHandler {
    @Override
    public void useItem(Playable playable, ItemInstance item, boolean forceUse) {
        if (!(playable instanceof Player))
            return;

        final Player player = (Player) playable;
        final ItemInstance weaponInst = player.getActiveWeaponInstance();
        final Weapon weaponItem = player.getActiveWeaponItem();

        if (weaponInst == null || weaponItem.getItemType() != WeaponType.FISHINGROD)
            return;

        // Fishshot is already active
        if (player.isChargedShot(ShotType.FISH_SOULSHOT))
            return;

        // Wrong grade of soulshot for that fishing pole.
        if (weaponItem.getCrystalType() != item.getItem().getCrystalType()) {
            player.sendPacket(SystemMessageId.WRONG_FISHINGSHOT_GRADE);
            return;
        }

        if (!player.destroyItemWithoutTrace(item.getObjectId(), 1)) {
            player.sendPacket(SystemMessageId.NOT_ENOUGH_SOULSHOTS);
            return;
        }

        final IntIntHolder[] skills = item.getItem().getSkills();

        player.setChargedShot(ShotType.FISH_SOULSHOT, true);
        player.broadcastPacket(new MagicSkillUse(player, skills[0].getId(), 1, 0, 0));
    }
}