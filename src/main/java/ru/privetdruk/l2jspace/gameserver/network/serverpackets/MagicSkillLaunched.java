package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class MagicSkillLaunched extends L2GameServerPacket {
    private final int _objectId;
    private final int _skillId;
    private final int _skillLevel;
    private Creature[] _targets;

    public MagicSkillLaunched(Creature cha, L2Skill skill, Creature[] targets) {
        _objectId = cha.getId();
        _skillId = skill.getId();
        _skillLevel = skill.getLevel();
        _targets = targets;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x76);
        writeD(_objectId);
        writeD(_skillId);
        writeD(_skillLevel);

        if (_targets.length == 0) {
            writeD(0);
            writeD(0);
        } else {
            writeD(_targets.length);
            for (Creature target : _targets)
                writeD(target.getId());
        }
    }
}