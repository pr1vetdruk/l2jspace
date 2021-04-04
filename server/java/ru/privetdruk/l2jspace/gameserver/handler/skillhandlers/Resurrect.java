package ru.privetdruk.l2jspace.gameserver.handler.skillhandlers;

import ru.privetdruk.l2jspace.gameserver.enums.items.ShotType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillType;
import ru.privetdruk.l2jspace.gameserver.handler.ISkillHandler;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Pet;
import ru.privetdruk.l2jspace.gameserver.skill.Formulas;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;
import ru.privetdruk.l2jspace.gameserver.taskmanager.DecayTaskManager;

public class Resurrect implements ISkillHandler {
    private static final SkillType[] SKILL_IDS =
            {
                    SkillType.RESURRECT
            };

    @Override
    public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets) {
        for (WorldObject cha : targets) {
            final Creature target = (Creature) cha;
            if (activeChar instanceof Player) {
                if (cha instanceof Player)
                    ((Player) cha).reviveRequest((Player) activeChar, skill, false);
                else if (cha instanceof Pet) {
                    if (((Pet) cha).getOwner() == activeChar)
                        target.doRevive(Formulas.calculateSkillResurrectRestorePercent(skill.getPower(), activeChar));
                    else
                        ((Pet) cha).getOwner().reviveRequest((Player) activeChar, skill, true);
                } else
                    target.doRevive(Formulas.calculateSkillResurrectRestorePercent(skill.getPower(), activeChar));
            } else {
                DecayTaskManager.getInstance().cancel(target);
                target.doRevive(Formulas.calculateSkillResurrectRestorePercent(skill.getPower(), activeChar));
            }
        }
        activeChar.setChargedShot(activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOT) ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, skill.isStaticReuse());
    }

    @Override
    public SkillType[] getSkillIds() {
        return SKILL_IDS;
    }
}