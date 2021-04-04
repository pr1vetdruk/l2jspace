package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.model.World;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Pet;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ActionFailed;

public final class RequestPetGetItem extends L2GameClientPacket {
    private int _objectId;

    @Override
    protected void readImpl() {
        _objectId = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null || !player.hasPet())
            return;

        final WorldObject item = World.getInstance().getObject(_objectId);
        if (item == null)
            return;

        final Pet pet = (Pet) player.getSummon();
        if (pet.isDead() || pet.isOutOfControl()) {
            sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        pet.getAI().tryToPickUp(_objectId, false);
    }
}