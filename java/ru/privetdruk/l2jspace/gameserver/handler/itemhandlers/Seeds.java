package ru.privetdruk.l2jspace.gameserver.handler.itemhandlers;

import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.data.manager.CastleManorManager;
import ru.privetdruk.l2jspace.gameserver.data.xml.MapRegionData;
import ru.privetdruk.l2jspace.gameserver.handler.IItemHandler;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Monster;
import ru.privetdruk.l2jspace.gameserver.model.holder.IntIntHolder;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.model.manor.Seed;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;

public class Seeds implements IItemHandler {
    @Override
    public void useItem(Playable playable, ItemInstance item, boolean forceUse) {
        if (!Config.ALLOW_MANOR || !(playable instanceof Player))
            return;

        final WorldObject target = playable.getTarget();
        if (!(target instanceof Monster)) {
            playable.sendPacket(SystemMessageId.THE_TARGET_IS_UNAVAILABLE_FOR_SEEDING);
            return;
        }

        final Monster monster = (Monster) target;
        if (!monster.getTemplate().isSeedable()) {
            playable.sendPacket(SystemMessageId.THE_TARGET_IS_UNAVAILABLE_FOR_SEEDING);
            return;
        }

        if (monster.isDead() || monster.getSeedState().isSeeded()) {
            playable.sendPacket(SystemMessageId.INVALID_TARGET);
            return;
        }

        final Seed seed = CastleManorManager.getInstance().getSeed(item.getItemId());
        if (seed == null)
            return;

        if (seed.getCastleId() != MapRegionData.getInstance().getAreaCastle(playable.getX(), playable.getY())) {
            playable.sendPacket(SystemMessageId.THIS_SEED_MAY_NOT_BE_SOWN_HERE);
            return;
        }

        monster.getSeedState().setSeeded(seed, playable.getObjectId());

        final IntIntHolder[] skills = item.getEtcItem().getSkills();
        if (skills != null) {
            if (skills[0] == null)
                return;

            playable.getAI().tryToCast(monster, skills[0].getSkill());
        }
    }
}