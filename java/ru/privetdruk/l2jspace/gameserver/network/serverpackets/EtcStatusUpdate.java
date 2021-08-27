package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.enums.ZoneId;
import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectFlag;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

public class EtcStatusUpdate extends L2GameServerPacket {
    private final Player _player;

    public EtcStatusUpdate(Player player) {
        _player = player;
    }

    @Override
    protected void writeImpl() {
        writeC(0xF3);
        writeD(_player.getCharges());
        writeD(_player.getWeightPenalty().ordinal());
        writeD((_player.getBlockList().isBlockingAll() || _player.isChatBanned()) ? 1 : 0);
        writeD(_player.isInsideZone(ZoneId.DANGER_AREA) ? 1 : 0);
        writeD((_player.getWeaponGradePenalty() || _player.getArmorGradePenalty() > 0) ? 1 : 0);
        writeD(_player.isAffected(EffectFlag.CHARM_OF_COURAGE) ? 1 : 0);
        writeD(_player.getDeathPenaltyBuffLevel());
    }
}