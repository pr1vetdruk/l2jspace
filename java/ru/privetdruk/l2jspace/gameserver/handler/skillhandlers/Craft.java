package ru.privetdruk.l2jspace.gameserver.handler.skillhandlers;

import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillType;
import ru.privetdruk.l2jspace.gameserver.handler.ISkillHandler;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.RecipeBookItemList;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class Craft implements ISkillHandler {
    private static final SkillType[] SKILL_IDS =
            {
                    SkillType.COMMON_CRAFT,
                    SkillType.DWARVEN_CRAFT
            };

    @Override
    public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets) {
        if (!(activeChar instanceof Player))
            return;

        final Player player = (Player) activeChar;
        if (player.isOperating()) {
            player.sendPacket(SystemMessageId.CANNOT_CREATED_WHILE_ENGAGED_IN_TRADING);
            return;
        }

        player.sendPacket(new RecipeBookItemList(player, skill.getSkillType() == SkillType.DWARVEN_CRAFT));
    }

    @Override
    public SkillType[] getSkillIds() {
        return SKILL_IDS;
    }
}