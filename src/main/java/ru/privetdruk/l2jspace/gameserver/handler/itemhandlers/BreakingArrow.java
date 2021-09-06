package ru.privetdruk.l2jspace.gameserver.handler.itemhandlers;

import ru.privetdruk.l2jspace.gameserver.handler.IItemHandler;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.GrandBoss;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ActionFailed;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SocialAction;

public class BreakingArrow implements IItemHandler {
    @Override
    public void useItem(Playable playable, ItemInstance item, boolean forceUse) {
        final int itemId = item.getItemId();
        if (!(playable instanceof Player))
            return;

        final Player player = (Player) playable;
        final WorldObject target = player.getTarget();
        if (!(target instanceof GrandBoss)) {
            player.sendPacket(SystemMessageId.INVALID_TARGET);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final GrandBoss frintezza = (GrandBoss) target;
        if (!player.isIn3DRadius(frintezza, 500)) {
            player.sendMessage("The purpose is inaccessible");
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if ((itemId == 8192) && (frintezza.getNpcId() == 29045)) {
            frintezza.broadcastPacket(new SocialAction(frintezza, 2));
            playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
        }
    }
}