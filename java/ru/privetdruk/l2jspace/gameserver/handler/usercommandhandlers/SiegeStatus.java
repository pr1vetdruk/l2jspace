package ru.privetdruk.l2jspace.gameserver.handler.usercommandhandlers;

import ru.privetdruk.l2jspace.common.lang.StringUtil;

import ru.privetdruk.l2jspace.gameserver.data.manager.CastleManager;
import ru.privetdruk.l2jspace.gameserver.enums.SiegeSide;
import ru.privetdruk.l2jspace.gameserver.handler.IUserCommandHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.entity.Castle;
import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.NpcHtmlMessage;

public class SiegeStatus implements IUserCommandHandler {
    private static final int[] COMMAND_IDS =
            {
                    99
            };

    private static final String IN_PROGRESS = "Castle Siege in Progress";
    private static final String OUTSIDE_ZONE = "Outside Castle Siege Zone";

    @Override
    public void useUserCommand(int id, Player player) {
        if (!player.isClanLeader()) {
            player.sendPacket(SystemMessageId.ONLY_CLAN_LEADER_CAN_ISSUE_COMMANDS);
            return;
        }

        if (!player.isNoble()) {
            player.sendPacket(SystemMessageId.ONLY_NOBLESSE_LEADER_CAN_VIEW_SIEGE_STATUS_WINDOW);
            return;
        }

        final Clan clan = player.getClan();

        final StringBuilder sb = new StringBuilder();

        for (Castle castle : CastleManager.getInstance().getCastles()) {
            // Search on lists : as a clan can only be registered in a single siege, break after one case is found.
            if (!castle.getSiege().isInProgress() || !castle.getSiege().checkSides(clan, SiegeSide.ATTACKER, SiegeSide.DEFENDER, SiegeSide.OWNER))
                continue;

            for (Player member : clan.getOnlineMembers())
                StringUtil.append(sb, "<tr><td width=170>", member.getName(), "</td><td width=100>", (castle.getSiegeZone().isInsideZone(member)) ? IN_PROGRESS : OUTSIDE_ZONE, "</td></tr>");

            final NpcHtmlMessage html = new NpcHtmlMessage(0);
            html.setFile("data/html/siege_status.htm");
            html.replace("%kills%", clan.getSiegeKills());
            html.replace("%deaths%", clan.getSiegeDeaths());
            html.replace("%content%", sb.toString());
            player.sendPacket(html);
            return;
        }

        player.sendPacket(SystemMessageId.ONLY_DURING_SIEGE);
    }

    @Override
    public int[] getUserCommandList() {
        return COMMAND_IDS;
    }
}