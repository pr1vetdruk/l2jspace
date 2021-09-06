package ru.privetdruk.l2jspace.gameserver.handler.itemhandlers;

import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.enums.items.ShotType;
import ru.privetdruk.l2jspace.gameserver.handler.IItemHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.Summon;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.MagicSkillUse;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;

public class BeastSoulShots implements IItemHandler {
    @Override
    public void useItem(Playable playable, ItemInstance item, boolean forceUse) {
        if (playable == null)
            return;

        final Player player = playable.getActingPlayer();
        if (player == null)
            return;

        if (playable instanceof Summon) {
            player.sendPacket(SystemMessageId.PET_CANNOT_USE_ITEM);
            return;
        }

        final Summon summon = player.getSummon();
        if (summon == null) {
            player.sendPacket(SystemMessageId.PETS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
            return;
        }

        if (summon.isDead()) {
            player.sendPacket(SystemMessageId.SOULSHOTS_AND_SPIRITSHOTS_ARE_NOT_AVAILABLE_FOR_A_DEAD_PET);
            return;
        }

        // SoulShots are already active.
        if (summon.isChargedShot(ShotType.SOULSHOT))
            return;

        // If the player doesn't have enough beast soulshot remaining, remove any auto soulshot task.
        if (!Config.INFINITY_SS && !player.destroyItemWithoutTrace(item.getObjectId(), summon.getSoulShotsPerHit())) {
            if (!player.disableAutoShot(item.getItemId()))
                player.sendPacket(SystemMessageId.NOT_ENOUGH_SOULSHOTS_FOR_PET);

            return;
        }

        player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_USES_S1).addItemName(item.getItemId()));
        summon.setChargedShot(ShotType.SOULSHOT, true);
        player.broadcastPacketInRadius(new MagicSkillUse(summon, summon, 2033, 1, 0, 0), 600);
    }
}