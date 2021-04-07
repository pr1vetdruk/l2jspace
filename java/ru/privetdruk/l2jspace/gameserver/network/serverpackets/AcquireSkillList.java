package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import ru.privetdruk.l2jspace.gameserver.enums.skills.AcquireSkillType;
import ru.privetdruk.l2jspace.gameserver.model.holder.skillnode.ClanSkillNode;
import ru.privetdruk.l2jspace.gameserver.model.holder.skillnode.FishingSkillNode;
import ru.privetdruk.l2jspace.gameserver.model.holder.skillnode.GeneralSkillNode;
import ru.privetdruk.l2jspace.gameserver.model.holder.skillnode.SkillNode;

public final class AcquireSkillList extends L2GameServerPacket {
    private List<? extends SkillNode> _skills;

    private final AcquireSkillType _skillType;

    public AcquireSkillList(AcquireSkillType type, List<? extends SkillNode> skills) {
        _skillType = type;
        _skills = new ArrayList<>(skills);
    }

    @Override
    protected final void writeImpl() {
        writeC(0x8a);
        writeD(_skillType.ordinal());
        writeD(_skills.size());

        switch (_skillType) {
            case USUAL:
                _skills.stream().map(GeneralSkillNode.class::cast).forEach(gsn ->
                {
                    writeD(gsn.getId());
                    writeD(gsn.getValue());
                    writeD(gsn.getValue());
                    writeD(gsn.getCorrectedCost());
                    writeD(0);
                });
                break;

            case FISHING:
                _skills.stream().map(FishingSkillNode.class::cast).forEach(gsn ->
                {
                    writeD(gsn.getId());
                    writeD(gsn.getValue());
                    writeD(gsn.getValue());
                    writeD(0);
                    writeD(1);
                });
                break;

            case CLAN:
                _skills.stream().map(ClanSkillNode.class::cast).forEach(gsn ->
                {
                    writeD(gsn.getId());
                    writeD(gsn.getValue());
                    writeD(gsn.getValue());
                    writeD(gsn.getCost());
                    writeD(0);
                });
                break;
        }
    }
}