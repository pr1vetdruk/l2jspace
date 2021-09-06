package ru.privetdruk.l2jspace.gameserver.handler.itemhandlers;

import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.data.SkillTable;
import ru.privetdruk.l2jspace.gameserver.handler.IItemHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Pet;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.MagicSkillUse;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class PetFoods implements IItemHandler {
    @Override
    public void useItem(Playable playable, ItemInstance item, boolean forceUse) {
        final int itemId = item.getItemId();

        switch (itemId) {
            case 2515: // Wolf's food
                useFood(playable, 2048, item);
                break;

            case 4038: // Hatchling's food
                useFood(playable, 2063, item);
                break;

            case 5168: // Strider's food
                useFood(playable, 2101, item);
                break;

            case 5169: // ClanHall / Castle Strider's food
                useFood(playable, 2102, item);
                break;

            case 6316: // Wyvern's food
                useFood(playable, 2180, item);
                break;

            case 7582: // Baby Pet's food
                useFood(playable, 2048, item);
                break;
        }
    }

    private static boolean useFood(Playable playable, int magicId, ItemInstance item) {
        final L2Skill skill = SkillTable.getInstance().getInfo(magicId, 1);
        if (skill != null) {
            if (playable instanceof Pet) {
                final Pet pet = (Pet) playable;

                if (pet.destroyItem("Consume", item.getObjectId(), 1, null, false)) {
                    // Send visual effect.
                    playable.broadcastPacket(new MagicSkillUse(playable, playable, magicId, 1, 0, 0));

                    // Put current value.
                    pet.setCurrentFed(pet.getCurrentFed() + (skill.getFeed() * Config.PET_FOOD_RATE));

                    // If pet is still hungry, send an alert.
                    if (pet.checkAutoFeedState())
                        pet.getOwner().sendPacket(SystemMessageId.YOUR_PET_ATE_A_LITTLE_BUT_IS_STILL_HUNGRY);

                    return true;
                }
            } else if (playable instanceof Player) {
                final Player player = ((Player) playable);
                final int itemId = item.getItemId();

                if (player.isMounted() && player.getPetTemplate().canEatFood(itemId)) {
                    if (player.destroyItem("Consume", item.getObjectId(), 1, null, false)) {
                        player.broadcastPacket(new MagicSkillUse(playable, playable, magicId, 1, 0, 0));
                        player.setCurrentFeed(player.getCurrentFeed() + (skill.getFeed() * Config.PET_FOOD_RATE));
                    }
                    return true;
                }

                playable.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addItemName(itemId));
                return false;
            }
        }
        return false;
    }
}