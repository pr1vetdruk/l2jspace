package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ManagePledgePower;

public final class RequestPledgePower extends L2GameClientPacket {
    private int _rank;
    private int _action;
    private int _privs;

    @Override
    protected void readImpl() {
        _rank = readD();
        _action = readD();
        _privs = (_action == 2) ? readD() : 0;
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getPlayer();
        if (player == null) {
            return;
        }

        Clan clan = player.getClan();
        if (clan == null) {
            return;
        }

        if (_action == 2) {
            if (player.isClanLeader()) {
                if (_rank == 9) {
                    _privs = (_privs & Clan.CP_CL_VIEW_WAREHOUSE) + (_privs & Clan.CP_CH_OPEN_DOOR) + (_privs & Clan.CP_CS_OPEN_DOOR) + (_privs & Clan.CP_CH_USE_FUNCTIONS) + (_privs & Clan.CP_CS_USE_FUNCTIONS);
                }

                clan.setPrivilegesForRanking(_rank, _privs);
            }
        } else {
            player.sendPacket(new ManagePledgePower(clan, _action, _rank));
        }
    }
}