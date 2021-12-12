package ru.privetdruk.l2jspace.gameserver.model.actor.attack;

import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Weapon;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;

/**
 * This class groups all attack data related to a {@link Creature}.
 */
public class PlayerAttack extends PlayableAttack<Player> {
    public PlayerAttack(Player creature) {
        super(creature);
    }

    @Override
    public boolean doAttack(Creature target) {
        final boolean isHit = super.doAttack(target);
        if (isHit) {
            // If hit by a CW or by an hero while holding a CW, CP are reduced to 0.
            if (target instanceof Player targetPlayer && !target.isInvul()) {
                if (_actor.isCursedWeaponEquipped() || (_actor.isHero() && targetPlayer.isCursedWeaponEquipped())) {
                    targetPlayer.getStatus().setCp(0);
                }
            }
        }

        _actor.clearRecentFakeDeath();
        return isHit;
    }

    @Override
    public boolean canDoAttack(Creature target) {
        if (!super.canDoAttack(target)) {
            return false;
        }

        Weapon weaponItem = _actor.getActiveWeaponItem();

        switch (weaponItem.getItemType()) {
            case FISHINGROD -> {
                _actor.sendPacket(SystemMessageId.CANNOT_ATTACK_WITH_FISHING_POLE);
                return false;
            }
            case BOW -> {
                if (!_actor.checkAndEquipArrows()) {
                    _actor.sendPacket(SystemMessageId.NOT_ENOUGH_ARROWS);
                    return false;
                }

                int mpConsume = weaponItem.getMpConsume();
                if (mpConsume > 0 && mpConsume > _actor.getStatus().getMp()) {
                    _actor.sendPacket(SystemMessageId.NOT_ENOUGH_MP);
                    return false;
                }
            }
        }

        return true;
    }
}