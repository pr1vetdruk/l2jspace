package ru.privetdruk.l2jspace.gameserver.model.actor.cast;

import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Chest;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Door;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.MagicSkillUse;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

/**
 * This class groups all cast data related to a {@link Player}.
 *
 * @param <T> : The {@link Playable} used as actor.
 */
public class PlayableCast<T extends Playable> extends CreatureCast<T> {
    public PlayableCast(T actor) {
        super(actor);
    }

    @Override
    public void doInstantCast(L2Skill skill, ItemInstance item) {
        if (!item.isHerb() && !_actor.destroyItem("Consume", item.getId(), (skill.getItemConsumeId() == 0 && skill.getItemConsume() > 0) ? skill.getItemConsume() : 1, null, false)) {
            _actor.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
            return;
        }

        int reuseDelay = skill.getReuseDelay();
        if (reuseDelay > 10)
            _actor.disableSkill(skill, reuseDelay);

        _actor.broadcastPacket(new MagicSkillUse(_actor, _actor, skill.getId(), skill.getLevel(), 0, 0));

        callSkill(skill, new Creature[]
                {
                        _actor
                });
    }

    @Override
    public void doCast(L2Skill skill, Creature target, ItemInstance itemInstance) {
        if (itemInstance != null) {
            // Consume item if needed.
            if (!(itemInstance.isHerb() || itemInstance.isSummonItem()) && !_actor.destroyItem("Consume", itemInstance.getId(), 1, null, false)) {
                _actor.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
                return;
            }

            // Set item timestamp.
            _actor.addItemSkillTimeStamp(skill, itemInstance);
        }

        super.doCast(skill, target, null);
    }

    @Override
    public boolean canDoCast(Creature target, L2Skill skill, boolean isCtrlPressed, int itemObjectId) {
        if (!super.canDoCast(target, skill, isCtrlPressed, itemObjectId)) {
            return false;
        }

        if (!skill.checkCondition(_actor, target, false)) {
            return false;
        }

        if (_actor.getActingPlayer().isInOlympiadMode() && (skill.isHeroSkill() || skill.getSkillType() == SkillType.RESURRECT)) {
            _actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.THIS_SKILL_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT));
            return false;
        }

        // Check item consumption validity.
        if (itemObjectId != 0 && _actor.getInventory().getItemByObjectId(itemObjectId) == null) {
            _actor.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
            return false;
        }

        if ((skill.getSkillType() == SkillType.UNLOCK || skill.getSkillType() == SkillType.UNLOCK_SPECIAL)) {
            if (!(target instanceof Door || target instanceof Chest)) {
                _actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.INVALID_TARGET));
                return false;
            }
        }

        if (skill.getItemConsumeId() > 0) {
            ItemInstance requiredItems = _actor.getInventory().getItemByItemId(skill.getItemConsumeId());

            if (requiredItems == null || requiredItems.getCount() < skill.getItemConsume()) {
                _actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addSkillName(skill));
                return false;
            }
        }

        return skill.meetCastConditions(_actor, target, isCtrlPressed);
    }
}