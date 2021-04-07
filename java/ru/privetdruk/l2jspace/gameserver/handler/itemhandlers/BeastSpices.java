package ru.privetdruk.l2jspace.gameserver.handler.itemhandlers;

import ru.privetdruk.l2jspace.gameserver.handler.IItemHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.FeedableBeast;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class BeastSpices implements IItemHandler {
    @Override
    public void useItem(Playable playable, ItemInstance item, boolean forceUse) {
        if (!(playable instanceof Player))
            return;

        final Player player = (Player) playable;
        final Creature target = playable.getTarget() instanceof Creature ? (Creature) playable.getTarget() : null;

        if (!(target instanceof FeedableBeast)) {
            player.sendPacket(SystemMessageId.INVALID_TARGET);
            return;
        }

        final L2Skill skill = item.getEtcItem().getSkills()[0].getSkill();
        if (skill != null)
            player.getAI().tryToCast(target, skill);
    }
}