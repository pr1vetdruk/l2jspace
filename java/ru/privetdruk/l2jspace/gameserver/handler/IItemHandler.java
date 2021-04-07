package ru.privetdruk.l2jspace.gameserver.handler;

import ru.privetdruk.l2jspace.common.logging.CLogger;

import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;

/**
 * Mother class of all itemHandlers.
 */
public interface IItemHandler {
    public static final CLogger LOGGER = new CLogger(IItemHandler.class.getName());

    /**
     * Launch task associated to the item.
     *
     * @param playable L2Playable designating the player
     * @param item     ItemInstance designating the item to use
     * @param forceUse ctrl hold on item use
     */
    public void useItem(Playable playable, ItemInstance item, boolean forceUse);
}