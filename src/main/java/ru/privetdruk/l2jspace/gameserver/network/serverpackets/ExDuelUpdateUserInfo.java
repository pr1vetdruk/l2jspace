package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

public class ExDuelUpdateUserInfo extends L2GameServerPacket {
    private final Player _player;

    public ExDuelUpdateUserInfo(Player player) {
        _player = player;
    }

    @Override
    protected void writeImpl() {
        writeC(0xfe);
        writeH(0x4f);
        writeS(_player.getName());
        writeD(_player.getId());
        writeD(_player.getClassId().getId());
        writeD(_player.getStatus().getLevel());
        writeD((int) _player.getStatus().getHp());
        writeD(_player.getStatus().getMaxHp());
        writeD((int) _player.getStatus().getMp());
        writeD(_player.getStatus().getMaxMp());
        writeD((int) _player.getStatus().getCp());
        writeD(_player.getStatus().getMaxCp());
    }
}