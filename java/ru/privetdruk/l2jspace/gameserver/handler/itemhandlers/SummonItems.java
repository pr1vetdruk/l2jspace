package ru.privetdruk.l2jspace.gameserver.handler.itemhandlers;

import java.util.List;

import ru.privetdruk.l2jspace.config.custom.EventConfig;
import ru.privetdruk.l2jspace.gameserver.data.SkillTable;
import ru.privetdruk.l2jspace.gameserver.data.xml.NpcData;
import ru.privetdruk.l2jspace.gameserver.data.xml.SummonItemData;
import ru.privetdruk.l2jspace.gameserver.handler.IItemHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.ChristmasTree;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.model.holder.IntIntHolder;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.model.spawn.Spawn;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;

public class SummonItems implements IItemHandler {
    @Override
    public void useItem(Playable playable, ItemInstance item, boolean forceUse) {
        if (!(playable instanceof Player))
            return;

        final Player player = (Player) playable;

        if (player.isSitting()) {
            player.sendPacket(SystemMessageId.CANT_MOVE_SITTING);
            return;
        }

        if (player.isInObserverMode()
                || player.isAllSkillsDisabled()
                || player.getCast().isCastingNow()
                || (player.isEventPlayer() && !EventConfig.Engine.ALLOW_SUMMON)) {
            return;
        }

        final IntIntHolder sitem = SummonItemData.getInstance().getSummonItem(item.getItemId());

        if ((player.getSummon() != null || player.isMounted()) && sitem.getValue() > 0) {
            player.sendPacket(SystemMessageId.SUMMON_ONLY_ONE);
            return;
        }

        if (player.getAttack().isAttackingNow() || player.isInCombat()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_SUMMON_IN_COMBAT);
            return;
        }

        final NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(sitem.getId());
        if (npcTemplate == null)
            return;

        switch (sitem.getValue()) {
            case 0: // static summons (like Christmas tree)
                final List<ChristmasTree> trees = player.getKnownTypeInRadius(ChristmasTree.class, 1200);
                if (!trees.isEmpty()) {
                    player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANNOT_SUMMON_S1_AGAIN).addCharName(trees.get(0)));
                    return;
                }

                if (!player.destroyItem("Summon", item, 1, null, false))
                    return;

                player.getMove().stop();

                try {
                    final Spawn spawn = new Spawn(npcTemplate);
                    spawn.setLoc(player.getPosition());
                    spawn.setRespawnState(false);

                    final Npc npc = spawn.doSpawn(true);
                    npc.setTitle(player.getName());
                    npc.setWalkOrRun(false);
                } catch (Exception e) {
                    player.sendPacket(SystemMessageId.TARGET_CANT_FOUND);
                }
                break;

            case 1: // summon pet through an item
                player.getAI().tryToCast(player, SkillTable.getInstance().getInfo(2046, 1), false, false, item.getObjectId());
                player.sendPacket(SystemMessageId.SUMMON_A_PET);
                break;

            case 2: // wyvern
                player.getMove().stop();
                player.mount(sitem.getId(), item.getObjectId());
                break;
        }
    }
}