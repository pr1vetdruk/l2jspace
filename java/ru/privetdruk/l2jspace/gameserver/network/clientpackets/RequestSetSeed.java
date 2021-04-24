package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import java.util.ArrayList;
import java.util.List;

import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.data.manager.CastleManorManager;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.manor.Seed;
import ru.privetdruk.l2jspace.gameserver.model.manor.SeedProduction;
import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ActionFailed;

public class RequestSetSeed extends L2GameClientPacket {
    private static final int BATCH_LENGTH = 12;

    private int _manorId;
    private List<SeedProduction> _items;

    @Override
    protected void readImpl() {
        _manorId = readD();
        final int count = readD();
        if (count <= 0 || count > Config.MAX_ITEM_IN_PACKET || (count * BATCH_LENGTH) != _buf.remaining())
            return;

        _items = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            final int itemId = readD();
            final int sales = readD();
            final int price = readD();

            if (itemId < 1 || sales < 0 || price < 0) {
                _items.clear();
                return;
            }

            if (sales > 0)
                _items.add(new SeedProduction(itemId, sales, price, sales));
        }
    }

    @Override
    protected void runImpl() {
        if (_items.isEmpty())
            return;

        final CastleManorManager manor = CastleManorManager.getInstance();
        if (!manor.isModifiablePeriod()) {
            sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Check player privileges
        final Player player = getClient().getPlayer();
        if (player == null || player.getClan() == null || player.getClan().getCastleId() != _manorId || !player.hasClanPrivileges(Clan.CP_CS_MANOR_ADMIN) || !player.getAI().canDoInteract(player.getCurrentFolk())) {
            sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Filter seeds with start amount lower than 0 and incorrect price
        final List<SeedProduction> list = new ArrayList<>(_items.size());
        for (SeedProduction sp : _items) {
            final Seed s = manor.getSeed(sp.getId());
            if (s != null && sp.getStartAmount() <= s.getSeedLimit() && sp.getPrice() >= s.getSeedMinPrice() && sp.getPrice() <= s.getSeedMaxPrice())
                list.add(sp);
        }

        // Save new list
        manor.setNextSeedProduction(list, _manorId);
    }
}