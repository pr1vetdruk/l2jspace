package ru.privetdruk.l2jspace.gameserver.handler.skillhandlers;

import ru.privetdruk.l2jspace.common.random.Rnd;
import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillType;
import ru.privetdruk.l2jspace.gameserver.handler.ISkillHandler;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.holder.IntIntHolder;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;
import ru.privetdruk.l2jspace.gameserver.skill.extractable.ExtractableProductItem;
import ru.privetdruk.l2jspace.gameserver.skill.extractable.ExtractableSkill;

public class Extractable implements ISkillHandler {
    private static final SkillType[] SKILL_IDS =
            {
                    SkillType.EXTRACTABLE,
                    SkillType.EXTRACTABLE_FISH
            };

    @Override
    public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets) {
        if (!(activeChar instanceof Player))
            return;

        final ExtractableSkill exItem = skill.getExtractableSkill();
        if (exItem == null || exItem.getProductItems().isEmpty()) {
            LOGGER.warn("Missing informations for extractable skill id: {}.", skill.getId());
            return;
        }

        final Player player = activeChar.getActingPlayer();

        int chance = Rnd.get(100000);
        boolean created = false;
        for (ExtractableProductItem expi : exItem.getProductItems()) {
            chance -= (int) (expi.getChance() * 1000);
            if (chance >= 0)
                continue;

            // The inventory is full, terminate.
            if (!player.getInventory().validateCapacityByItemIds(expi.getItems())) {
                player.sendPacket(SystemMessageId.SLOTS_FULL);
                return;
            }

            // Inventory has space, create all items.
            for (IntIntHolder item : expi.getItems()) {
                player.addItem("Extract", item.getId(), item.getValue(), player, true);
                created = true;
            }

            break;
        }

        if (!created)
            player.sendPacket(SystemMessageId.NOTHING_INSIDE_THAT);
    }

    @Override
    public SkillType[] getSkillIds() {
        return SKILL_IDS;
    }
}