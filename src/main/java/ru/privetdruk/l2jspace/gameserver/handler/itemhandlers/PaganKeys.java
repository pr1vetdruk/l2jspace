package ru.privetdruk.l2jspace.gameserver.handler.itemhandlers;

import ru.privetdruk.l2jspace.gameserver.data.xml.DoorData;
import ru.privetdruk.l2jspace.gameserver.handler.IItemHandler;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Door;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ActionFailed;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;

public class PaganKeys implements IItemHandler {
    @Override
    public void useItem(Playable playable, ItemInstance item, boolean forceUse) {
        if (!(playable instanceof Player))
            return;

        final Player player = (Player) playable;
        final WorldObject target = player.getTarget();

        if (!(target instanceof Door)) {
            player.sendPacket(SystemMessageId.INVALID_TARGET);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final Door door = (Door) target;

        if (!(player.isIn3DRadius(door, Npc.INTERACTION_DISTANCE))) {
            player.sendPacket(SystemMessageId.DIST_TOO_FAR_CASTING_STOPPED);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (!playable.destroyItem("Consume", item.getId(), 1, null, true))
            return;

        final int doorId = door.getDoorId();

        switch (item.getItemId()) {
            case 8056:
                if (doorId == 23150004 || doorId == 23150003) {
                    DoorData.getInstance().getDoor(23150003).openMe();
                    DoorData.getInstance().getDoor(23150004).openMe();
                } else
                    player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addItemName(8056));
                break;

            case 8273:
                switch (doorId) {
                    case 19160002:
                    case 19160003:
                    case 19160004:
                    case 19160005:
                    case 19160006:
                    case 19160007:
                    case 19160008:
                    case 19160009:
                        DoorData.getInstance().getDoor(doorId).openMe();
                        break;

                    default:
                        player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addItemName(8273));
                        break;
                }
                break;

            case 8275:
                switch (doorId) {
                    case 19160012:
                    case 19160013:
                        DoorData.getInstance().getDoor(doorId).openMe();
                        break;

                    default:
                        player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addItemName(8275));
                        break;
                }
                break;
        }
    }
}