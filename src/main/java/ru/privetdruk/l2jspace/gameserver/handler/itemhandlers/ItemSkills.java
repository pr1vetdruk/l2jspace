package ru.privetdruk.l2jspace.gameserver.handler.itemhandlers;

import ru.privetdruk.l2jspace.common.util.ArraysUtil;
import ru.privetdruk.l2jspace.config.custom.EventConfig;
import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventType;
import ru.privetdruk.l2jspace.gameserver.handler.IItemHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Pet;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Servitor;
import ru.privetdruk.l2jspace.gameserver.model.holder.IntIntHolder;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ActionFailed;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;
import ru.privetdruk.l2jspace.gameserver.skill.effect.EffectTemplate;

public class ItemSkills implements IItemHandler {

    private static final int[] HP_POTION_SKILL_IDS =
            {
                    2031, // Lesser Healing Potion
                    2032, // Healing potion
                    2037 // Greater Healing Potion
            };

    @Override
    public void useItem(Playable playable, ItemInstance item, boolean forceUse) {
        if (playable instanceof Servitor)
            return;

        final boolean isPet = playable instanceof Pet;
        final Player player = playable.getActingPlayer();
        final Creature target = playable.getTarget() instanceof Creature ? (Creature) playable.getTarget() : null;

        if (player.isEventPlayer() && item.isPotion()) {
            EventEngine event = EventEngine.findActive();

            boolean allowPotions = event.getEventType() == EventType.CTF && EventConfig.CTF.ALLOW_POTIONS;

            if (event.getEventState() == EventState.IN_PROGRESS && !allowPotions) {
                player.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
        }

        // Pets can only use tradable items.
        if (isPet && !item.isTradable()) {
            player.sendPacket(SystemMessageId.ITEM_NOT_FOR_PETS);
            return;
        }

        final IntIntHolder[] skills = item.getEtcItem().getSkills();
        if (skills == null) {
            LOGGER.warn("{} doesn't have any registered skill for handler.", item.getName());
            return;
        }

        for (final IntIntHolder skillInfo : skills) {
            if (skillInfo == null)
                continue;

            final L2Skill itemSkill = skillInfo.getSkill();
            if (itemSkill == null)
                continue;

            if (!itemSkill.checkCondition(playable, target, false))
                return;

            // No message on retail, the use is just forgotten.
            if (playable.isSkillDisabled(itemSkill))
                return;

            // Potions and Energy Stones bypass the AI system. The rest does not.
            if (itemSkill.isPotion() || itemSkill.isSimultaneousCast()) {
                playable.getCast().doInstantCast(itemSkill, item);

                if (!isPet && item.isHerb() && player.hasServitor())
                    player.getSummon().getCast().doInstantCast(itemSkill, item);
            } else
                playable.getAI().tryToCast(target, itemSkill, forceUse, false, (item.isEtcItem() ? item.getId() : 0));

            // Send message to owner.
            if (isPet)
                player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_USES_S1).addSkillName(itemSkill));
            else {
                // Buff icon for healing potions.
                final int skillId = skillInfo.getId();
                if (ArraysUtil.contains(HP_POTION_SKILL_IDS, skillId) && skillId >= player.getShortBuffTaskSkillId()) {
                    final EffectTemplate template = itemSkill.getEffectTemplates().get(0);
                    if (template != null)
                        player.shortBuffStatusUpdate(skillId, skillInfo.getValue(), template.getCounter() * template.getPeriod());
                }
            }
        }
    }
}