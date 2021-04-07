package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import java.util.ArrayList;
import java.util.List;

import ru.privetdruk.l2jspace.gameserver.data.manager.CursedWeaponManager;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.entity.CursedWeapon;
import ru.privetdruk.l2jspace.gameserver.model.location.Location;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ExCursedWeaponLocation;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ExCursedWeaponLocation.CursedWeaponInfo;

public final class RequestCursedWeaponLocation extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        final List<CursedWeaponInfo> list = new ArrayList<>();
        for (CursedWeapon cw : CursedWeaponManager.getInstance().getCursedWeapons()) {
            if (!cw.isActive())
                continue;

            final Location loc = cw.getWorldPosition();
            if (loc != null)
                list.add(new CursedWeaponInfo(loc, cw.getItemId(), (cw.isActivated()) ? 1 : 0));
        }

        if (!list.isEmpty())
            player.sendPacket(new ExCursedWeaponLocation(list));
    }
}