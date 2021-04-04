package ru.privetdruk.l2jspace.gameserver.handler.itemhandlers;

import ru.privetdruk.l2jspace.common.random.Rnd;

import ru.privetdruk.l2jspace.gameserver.enums.FloodProtector;
import ru.privetdruk.l2jspace.gameserver.handler.IItemHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.Dice;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;

public class RollingDices implements IItemHandler {
    @Override
    public void useItem(Playable playable, ItemInstance item, boolean forceUse) {
        if (!(playable instanceof Player))
            return;

        final Player player = (Player) playable;

        if (!player.getClient().performAction(FloodProtector.ROLL_DICE)) {
            player.sendPacket(SystemMessageId.YOU_MAY_NOT_THROW_THE_DICE_AT_THIS_TIME_TRY_AGAIN_LATER);
            return;
        }

        final int number = Rnd.get(1, 6);

        player.broadcastPacket(new Dice(player, item.getItemId(), number));
        player.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_ROLLED_S2).addCharName(player).addNumber(number));
    }
}