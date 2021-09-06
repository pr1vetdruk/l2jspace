package ru.privetdruk.l2jspace.gameserver.handler.skillhandlers;

import ru.privetdruk.l2jspace.gameserver.data.manager.CastleManager;
import ru.privetdruk.l2jspace.gameserver.enums.SiegeSide;
import ru.privetdruk.l2jspace.gameserver.enums.items.ShotType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.ShieldDefense;
import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillType;
import ru.privetdruk.l2jspace.gameserver.handler.ISkillHandler;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Door;
import ru.privetdruk.l2jspace.gameserver.model.entity.Siege;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.skill.Formula;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class StriderSiegeAssault implements ISkillHandler {
    private static final SkillType[] SKILL_IDS =
            {
                    SkillType.STRIDER_SIEGE_ASSAULT
            };

    @Override
    public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets) {
        if (!(activeChar instanceof Player))
            return;

        final Player player = (Player) activeChar;
        if (!check(player, targets[0], skill))
            return;

        final Door door = (Door) targets[0];
        if (door.isAlikeDead())
            return;

        boolean isCrit = Formula.calcCrit(activeChar, door, skill);
        boolean ss = activeChar.isChargedShot(ShotType.SOULSHOT);
        ShieldDefense shieldDefense = Formula.calcShieldUse(activeChar, door, skill, isCrit);

        int damage = (int) Formula.calcPhysicalSkillDamage(activeChar, door, skill, shieldDefense, isCrit, ss);
        if (damage > 0) {
            activeChar.sendDamageMessage(door, damage, false, false, false);
            door.reduceCurrentHp(damage, activeChar, skill);
        } else {
            activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ATTACK_FAILED));
        }

        activeChar.setChargedShot(ShotType.SOULSHOT, skill.isStaticReuse());
    }

    @Override
    public SkillType[] getSkillIds() {
        return SKILL_IDS;
    }

    /**
     * @param player : The {@link Player} to test.
     * @param target : The {@link WorldObject} to test.
     * @param skill  : The {@link L2Skill} to test.
     * @return True if the {@link Player} can cast the {@link L2Skill} on the {@link WorldObject}.
     */
    public static boolean check(Player player, WorldObject target, L2Skill skill) {
        SystemMessage sm = null;

        if (!player.isRiding())
            sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addSkillName(skill);
        else if (!(target instanceof Door))
            sm = SystemMessage.getSystemMessage(SystemMessageId.INVALID_TARGET);
        else {
            final Siege siege = CastleManager.getInstance().getActiveSiege(player);
            if (siege == null || !siege.checkSide(player.getClan(), SiegeSide.ATTACKER))
                sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addSkillName(skill);
        }

        if (sm != null)
            player.sendPacket(sm);

        return sm == null;
    }
}