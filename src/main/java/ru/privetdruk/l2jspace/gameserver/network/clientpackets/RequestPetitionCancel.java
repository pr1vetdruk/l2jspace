package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.data.manager.PetitionManager;
import ru.privetdruk.l2jspace.gameserver.data.xml.AdminData;
import ru.privetdruk.l2jspace.gameserver.enums.SayType;
import ru.privetdruk.l2jspace.gameserver.enums.petitions.PetitionState;
import ru.privetdruk.l2jspace.gameserver.model.Petition;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.CreatureSay;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;

public final class RequestPetitionCancel extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        final Petition petition = PetitionManager.getInstance().getPetitionInProcess(player);
        if (petition != null) {
            // Regular Player can't end the Petition.
            if (petition.getPetitionerObjectId() == player.getObjectId())
                player.sendPacket(SystemMessageId.PETITION_UNDER_PROCESS);
                // Part of responders - leave conversation properly or end active petition.
            else if (petition.getResponders().contains(player.getObjectId())) {
                if (player.isGM())
                    petition.endConsultation(PetitionState.CLOSED);
                else
                    petition.removeAdditionalResponder(player);
            }
            return;
        }

        if (!PetitionManager.getInstance().cancelPendingPetition(player)) {
            player.sendPacket(SystemMessageId.PETITION_NOT_SUBMITTED);
            return;
        }

        player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PETITION_CANCELED_SUBMIT_S1_MORE_TODAY).addNumber(Config.MAX_PETITIONS_PER_PLAYER - PetitionManager.getInstance().getPetitionsCount(player)));

        // Notify all GMs that the player's pending petition has been cancelled.
        AdminData.getInstance().broadcastToGMs(new CreatureSay(player.getObjectId(), SayType.HERO_VOICE, "Petition System", player.getName() + " has canceled a pending petition."));
    }
}