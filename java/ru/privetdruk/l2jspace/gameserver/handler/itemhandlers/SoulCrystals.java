package ru.privetdruk.l2jspace.gameserver.handler.itemhandlers;

import ru.privetdruk.l2jspace.gameserver.handler.IItemHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.holder.IntIntHolder;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class SoulCrystals implements IItemHandler {
    @Override
    public void useItem(Playable playable, ItemInstance item, boolean forceUse) {
        if (!(playable instanceof Player))
            return;

        final IntIntHolder[] skills = item.getEtcItem().getSkills();
        if (skills == null)
            return;

        final L2Skill skill = skills[0].getSkill();
        if (skill == null || skill.getId() != 2096)
            return;

        final Creature target = playable.getTarget() instanceof Creature ? (Creature) playable.getTarget() : null;
        if (target == null) {
            playable.sendPacket(SystemMessageId.INVALID_TARGET);
            return;
        }

        playable.getAI().tryToCast(target, skill, forceUse, false, 0);
    }
}