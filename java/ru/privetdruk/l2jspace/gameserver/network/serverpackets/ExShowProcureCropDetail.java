package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.data.manager.CastleManager;
import ru.privetdruk.l2jspace.gameserver.data.manager.CastleManorManager;
import ru.privetdruk.l2jspace.gameserver.model.entity.Castle;
import ru.privetdruk.l2jspace.gameserver.model.manor.CropProcure;

import java.util.HashMap;
import java.util.Map;

public class ExShowProcureCropDetail extends L2GameServerPacket {
    private final int _cropId;
    private final Map<Integer, CropProcure> _castleCrops;

    public ExShowProcureCropDetail(int cropId) {
        _cropId = cropId;
        _castleCrops = new HashMap<>();

        for (Castle c : CastleManager.getInstance().getCastles()) {
            final CropProcure cropItem = CastleManorManager.getInstance().getCropProcure(c.getCastleId(), cropId, false);
            if (cropItem != null && cropItem.getAmount() > 0)
                _castleCrops.put(c.getCastleId(), cropItem);
        }
    }

    @Override
    public void writeImpl() {
        writeC(0xFE);
        writeH(0x22);

        writeD(_cropId);
        writeD(_castleCrops.size());

        for (Map.Entry<Integer, CropProcure> entry : _castleCrops.entrySet()) {
            final CropProcure crop = entry.getValue();

            writeD(entry.getKey());
            writeD(crop.getAmount());
            writeD(crop.getPrice());
            writeC(crop.getReward());
        }
    }
}