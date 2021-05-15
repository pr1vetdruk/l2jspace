package ru.privetdruk.l2jspace.gameserver.model.actor.attack;

import ru.privetdruk.l2jspace.gameserver.enums.ZoneId;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;

/**
 * This class groups all attack data related to a {@link Creature}.
 *
 * @param <T> : The {@link Playable} used as actor.
 */
public class PlayableAttack<T extends Playable> extends CreatureAttack<T> {
    public PlayableAttack(T actor) {
        super(actor);
    }

    @Override
    public boolean canDoAttack(Creature target) {
        if (!super.canDoAttack(target)) {
            return false;
        }

        if (target instanceof Playable) {
            Player player = Player.definePlayer(_actor);
            Player targetPlayer = Player.definePlayer((Playable) target);

            if (!player.isEventPlayer() && _actor.isInsideZone(ZoneId.PEACE)) {
                _actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANT_ATK_PEACEZONE));
                return false;
            }

            if (!targetPlayer.isEventPlayer() && target.isInsideZone(ZoneId.PEACE)) {
                _actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.TARGET_IN_PEACEZONE));
                return false;
            }
        }

        return true;
    }
}