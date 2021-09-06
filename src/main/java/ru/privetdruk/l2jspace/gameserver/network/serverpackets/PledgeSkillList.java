package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

import java.util.Collection;

public class PledgeSkillList extends L2GameServerPacket {
    private final Collection<L2Skill> _skills;

    public PledgeSkillList(Clan clan) {
        _skills = clan.getClanSkills().values();
    }

    @Override
    protected void writeImpl() {
        writeC(0xfe);
        writeH(0x39);

        writeD(_skills.size());

        for (L2Skill skill : _skills) {
            writeD(skill.getId());
            writeD(skill.getLevel());
        }
    }
}