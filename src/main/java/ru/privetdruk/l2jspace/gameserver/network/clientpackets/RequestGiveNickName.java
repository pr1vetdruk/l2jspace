package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.common.lang.StringUtil;
import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;
import ru.privetdruk.l2jspace.gameserver.model.pledge.ClanMember;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;

public class RequestGiveNickName extends L2GameClientPacket {
    private String _name;
    private String _title;

    @Override
    protected void readImpl() {
        _name = readS();
        _title = readS();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        if (!StringUtil.isValidString(_title, Config.TITLE_TEMPLATE)) {
            player.sendPacket(SystemMessageId.NOT_WORKING_PLEASE_TRY_AGAIN_LATER);
            return;
        }

        // Noblesse can bestow a title to themselves
        if (player.isNoble() && _name.matches(player.getName()) && !player.isTopRank()) {
            player.setTitle(_title);
            player.sendPacket(SystemMessageId.TITLE_CHANGED);
            player.broadcastTitleInfo();
        } else {
            // Can the player change/give a title?
            if (!player.hasClanPrivileges(Clan.CP_CL_GIVE_TITLE)) {
                player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                return;
            }

            if (player.getClan().getLevel() < 3) {
                player.sendPacket(SystemMessageId.CLAN_LVL_3_NEEDED_TO_ENDOWE_TITLE);
                return;
            }

            ClanMember member = player.getClan().getClanMember(_name);
            if (member != null) {
                Player playerMember = member.getPlayerInstance();
                if (playerMember != null && !playerMember.isTopRank()) {
                    playerMember.setTitle(_title);

                    playerMember.sendPacket(SystemMessageId.TITLE_CHANGED);
                    if (player != playerMember) {
                        player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_TITLE_CHANGED_TO_S2).addCharName(playerMember).addString(_title));
                    }

                    playerMember.broadcastTitleInfo();
                } else {
                    player.sendPacket(SystemMessageId.TARGET_IS_NOT_FOUND_IN_THE_GAME);
                }
            } else {
                player.sendPacket(SystemMessageId.TARGET_MUST_BE_IN_CLAN);
            }
        }
    }
}