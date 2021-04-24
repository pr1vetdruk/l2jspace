package ru.privetdruk.l2jspace.gameserver.handler.itemhandlers;

import ru.privetdruk.l2jspace.common.random.Rnd;

import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.enums.items.ShotType;
import ru.privetdruk.l2jspace.gameserver.handler.IItemHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.holder.IntIntHolder;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Weapon;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.MagicSkillUse;

public class SoulShots implements IItemHandler {
    @Override
    public void useItem(Playable playable, ItemInstance item, boolean forceUse) {
        if (!(playable instanceof Player))
            return;

        final Player player = (Player) playable;
        final ItemInstance weaponInst = player.getActiveWeaponInstance();
        final Weapon weaponItem = player.getActiveWeaponItem();

        // Check if soulshot can be used
        if (weaponInst == null || weaponItem.getSoulShotCount() == 0) {
            if (!player.getAutoSoulShot().contains(item.getItemId()))
                player.sendPacket(SystemMessageId.CANNOT_USE_SOULSHOTS);

            return;
        }

        if (weaponItem.getCrystalType() != item.getItem().getCrystalType()) {
            if (!player.getAutoSoulShot().contains(item.getItemId()))
                player.sendPacket(SystemMessageId.SOULSHOTS_GRADE_MISMATCH);

            return;
        }

        // Check if Soulshot are already active.
        if (player.isChargedShot(ShotType.SOULSHOT))
            return;

        // Consume Soulshots if player has enough of them.
        int ssCount = weaponItem.getSoulShotCount();
        if (weaponItem.getReducedSoulShot() > 0 && Rnd.get(100) < weaponItem.getReducedSoulShotChance())
            ssCount = weaponItem.getReducedSoulShot();

        if (!Config.INFINITY_SS && !player.destroyItemWithoutTrace(item.getObjectId(), ssCount)) {
            if (!player.disableAutoShot(item.getItemId()))
                player.sendPacket(SystemMessageId.NOT_ENOUGH_SOULSHOTS);

            return;
        }

        final IntIntHolder[] skills = item.getItem().getSkills();

        weaponInst.setChargedShot(ShotType.SOULSHOT, true);
        player.sendPacket(SystemMessageId.ENABLED_SOULSHOT);
        player.broadcastPacketInRadius(new MagicSkillUse(player, player, skills[0].getId(), 1, 0, 0), 600);
    }
}