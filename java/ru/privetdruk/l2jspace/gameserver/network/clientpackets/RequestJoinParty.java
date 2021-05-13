package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.enums.LootRule;
import ru.privetdruk.l2jspace.gameserver.model.World;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.group.Party;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.AskJoinParty;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;

public final class RequestJoinParty extends L2GameClientPacket {
    private String _targetName;
    private int _lootRuleId;

    @Override
    protected void readImpl() {
        _targetName = readS();
        _lootRuleId = readD();
    }

    @Override
    protected void runImpl() {
        final Player requestor = getClient().getPlayer();
        if (requestor == null)
            return;

        final Player target = World.getInstance().getPlayer(_targetName);
        if (target == null) {
            requestor.sendPacket(SystemMessageId.FIRST_SELECT_USER_TO_INVITE_TO_PARTY);
            return;
        }

        if (target.getBlockList().isBlockingAll()) {
            requestor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_BLOCKED_EVERYTHING).addCharName(target));
            return;
        }

        if (target.getBlockList().isInBlockList(requestor)) {
            requestor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_ADDED_YOU_TO_IGNORE_LIST).addCharName(target));
            return;
        }

        if (target.equals(requestor) || target.isCursedWeaponEquipped() || requestor.isCursedWeaponEquipped() || !target.getAppearance().isVisible()) {
            requestor.sendPacket(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET);
            return;
        }

        if (target.isInParty()) {
            requestor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_IS_ALREADY_IN_PARTY).addCharName(target));
            return;
        }

        if (target.getClient().isDetached()) {
            requestor.sendMessage("The player you tried to invite is in offline mode.");
            return;
        }

        if (target.isInJail() || requestor.isInJail()) {
            requestor.sendMessage("The player you tried to invite is currently jailed.");
            return;
        }

        if (target.isInOlympiadMode() || requestor.isInOlympiadMode())
            return;

        if (requestor.isProcessingRequest()) {
            requestor.sendPacket(SystemMessageId.WAITING_FOR_ANOTHER_REPLY);
            return;
        }

        if (target.isProcessingRequest()) {
            requestor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_IS_BUSY_TRY_LATER).addCharName(target));
            return;
        }

        if (target.isEventPlayer() || requestor.isEventPlayer()) {
            requestor.sendMessage("Вы не можете пригласить этого игрока в группу: вы или ваша цель участвуете в ивенте.");
            return;
        }

        final Party party = requestor.getParty();
        if (party != null) {
            if (!party.isLeader(requestor)) {
                requestor.sendPacket(SystemMessageId.ONLY_LEADER_CAN_INVITE);
                return;
            }

            if (party.getMembersCount() >= 9) {
                requestor.sendPacket(SystemMessageId.PARTY_FULL);
                return;
            }

            if (party.getPendingInvitation() && !party.isInvitationRequestExpired()) {
                requestor.sendPacket(SystemMessageId.WAITING_FOR_ANOTHER_REPLY);
                return;
            }

            party.setPendingInvitation(true);
        } else
            requestor.setLootRule(LootRule.VALUES[_lootRuleId]);

        requestor.onTransactionRequest(target);
        requestor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_INVITED_S1_TO_PARTY).addCharName(target));

        target.sendPacket(new AskJoinParty(requestor.getName(), (party != null) ? party.getLootRule().ordinal() : _lootRuleId));
    }
}