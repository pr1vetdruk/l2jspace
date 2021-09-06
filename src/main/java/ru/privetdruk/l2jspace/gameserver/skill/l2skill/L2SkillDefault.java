package ru.privetdruk.l2jspace.gameserver.skill.l2skill;

import ru.privetdruk.l2jspace.common.data.StatSet;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ActionFailed;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class L2SkillDefault extends L2Skill {
    public L2SkillDefault(StatSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature caster, WorldObject[] targets) {
        caster.sendPacket(ActionFailed.STATIC_PACKET);
        caster.sendMessage("Skill " + getId() + " [" + getSkillType() + "] isn't implemented.");
    }
}