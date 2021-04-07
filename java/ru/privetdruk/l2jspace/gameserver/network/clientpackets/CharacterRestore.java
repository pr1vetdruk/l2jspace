package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.enums.FloodProtector;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.CharSelectInfo;

public final class CharacterRestore extends L2GameClientPacket {
    private int _slot;

    @Override
    protected void readImpl() {
        _slot = readD();
    }

    @Override
    protected void runImpl() {
        if (!getClient().performAction(FloodProtector.CHARACTER_SELECT))
            return;

        getClient().markRestoredChar(_slot);

        final CharSelectInfo csi = new CharSelectInfo(getClient().getAccountName(), getClient().getSessionId().playOkID1, 0);
        sendPacket(csi);
        getClient().setCharSelectSlot(csi.getCharacterSlots());
    }
}