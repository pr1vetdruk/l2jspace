package ru.privetdruk.l2jspace.gameserver.handler.itemhandlers;

import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.data.SkillTable;
import ru.privetdruk.l2jspace.gameserver.handler.IItemHandler;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Monster;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class Harvesters implements IItemHandler {
    @Override
    public void useItem(Playable playable, ItemInstance item, boolean forceUse) {
        if (!(playable instanceof Player))
            return;

        if (!Config.ALLOW_MANOR)
            return;

        final WorldObject target = playable.getTarget();
        if (!(target instanceof Monster)) {
            playable.sendPacket(SystemMessageId.INVALID_TARGET);
            return;
        }

        final Monster monster = (Monster) target;
        if (!monster.isDead()) {
            playable.sendPacket(SystemMessageId.INVALID_TARGET);
            return;
        }

        final L2Skill skill = SkillTable.getInstance().getInfo(2098, 1);
        if (skill != null)
            playable.getAI().tryToCast(monster, skill);
    }
}