package ru.privetdruk.l2jspace.gameserver.handler.skillhandlers;

import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillType;
import ru.privetdruk.l2jspace.gameserver.handler.ISkillHandler;
import ru.privetdruk.l2jspace.gameserver.handler.SkillHandler;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

import java.util.ArrayList;
import java.util.List;

public class BalanceLife implements ISkillHandler {
    private static final SkillType[] SKILL_IDS =
            {
                    SkillType.BALANCE_LIFE
            };

    @Override
    public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets) {
        final ISkillHandler handler = SkillHandler.getInstance().getHandler(SkillType.BUFF);
        if (handler != null)
            handler.useSkill(activeChar, skill, targets);

        final Player player = activeChar.getActingPlayer();
        final List<Creature> finalList = new ArrayList<>();

        double fullHP = 0;
        double currentHPs = 0;

        for (WorldObject obj : targets) {
            if (!(obj instanceof Creature))
                continue;

            final Creature target = ((Creature) obj);
            if (target.isDead())
                continue;

            // Player holding a cursed weapon can't be healed and can't heal
            if (target != activeChar) {
                if (target instanceof Player && ((Player) target).isCursedWeaponEquipped())
                    continue;
                else if (player != null && player.isCursedWeaponEquipped())
                    continue;
            }

            fullHP += target.getStatus().getMaxHp();
            currentHPs += target.getStatus().getHp();

            // Add the character to the final list.
            finalList.add(target);
        }

        if (!finalList.isEmpty()) {
            double percentHP = currentHPs / fullHP;

            for (Creature target : finalList)
                target.getStatus().setHp(target.getStatus().getMaxHp() * percentHP);
        }
    }

    @Override
    public SkillType[] getSkillIds() {
        return SKILL_IDS;
    }
}