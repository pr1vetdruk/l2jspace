package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.data.manager.PetitionManager;
import ru.privetdruk.l2jspace.gameserver.enums.petitions.PetitionRate;
import ru.privetdruk.l2jspace.gameserver.model.Petition;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

public final class PetitionVote extends L2GameClientPacket {
    private int _rate;
    private String _feedback;

    @Override
    protected void readImpl() {
        readD(); // Always 1
        _rate = readD();
        _feedback = readS();
    }

    @Override
    public void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        final Petition petition = PetitionManager.getInstance().getFeedbackPetition(player);
        if (petition == null)
            return;

        petition.setFeedback(PetitionRate.VALUES[_rate], _feedback.trim());
    }
}