package ru.privetdruk.l2jspace.gameserver.model.actor.instance;

import ru.privetdruk.l2jspace.gameserver.data.xml.SkillTreeData;
import ru.privetdruk.l2jspace.gameserver.enums.skills.AcquireSkillType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.model.holder.skillnode.EnchantSkillNode;
import ru.privetdruk.l2jspace.gameserver.model.holder.skillnode.GeneralSkillNode;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.*;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.effect.EffectBuff;
import ru.privetdruk.l2jspace.gameserver.skill.effect.EffectDebuff;

import java.util.List;

public class Folk extends Npc {
    public Folk(int objectId, NpcTemplate template) {
        super(objectId, template);

        setMortal(false);
    }

    @Override
    public void addEffect(AbstractEffect newEffect) {
        if (newEffect instanceof EffectDebuff || newEffect instanceof EffectBuff)
            super.addEffect(newEffect);
        else if (newEffect != null)
            newEffect.stopEffectTask();
    }

    /**
     * This method displays SkillList to the player.
     *
     * @param player The player who requested the method.
     */
    public void showSkillList(Player player) {
        if (!getTemplate().canTeach(player.getClassId())) {
            final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            html.setFile("data/html/trainer/" + getTemplate().getNpcId() + "-noskills.htm");
            player.sendPacket(html);
            return;
        }

        final List<GeneralSkillNode> skills = player.getAvailableSkills();
        if (skills.isEmpty()) {
            final int minlevel = player.getRequiredLevelForNextSkill();
            if (minlevel > 0)
                player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.DO_NOT_HAVE_FURTHER_SKILLS_TO_LEARN_S1).addNumber(minlevel));
            else
                player.sendPacket(SystemMessageId.NO_MORE_SKILLS_TO_LEARN);

            player.sendPacket(AcquireSkillDone.STATIC_PACKET);
        } else
            player.sendPacket(new AcquireSkillList(AcquireSkillType.USUAL, skills));

        player.sendPacket(ActionFailed.STATIC_PACKET);
    }

    /**
     * This method displays EnchantSkillList to the player.
     *
     * @param player The player who requested the method.
     */
    public void showEnchantSkillList(Player player) {
        if (!getTemplate().canTeach(player.getClassId())) {
            final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            html.setFile("data/html/trainer/" + getTemplate().getNpcId() + "-noskills.htm");
            player.sendPacket(html);
            return;
        }

        if (player.getClassId().getLevel() < 3) {
            final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            html.setHtml("<html><body> You must have 3rd class change quest completed.</body></html>");
            player.sendPacket(html);
            return;
        }

        final List<EnchantSkillNode> skills = SkillTreeData.getInstance().getEnchantSkillsFor(player);
        if (skills.isEmpty()) {
            player.sendPacket(SystemMessageId.THERE_IS_NO_SKILL_THAT_ENABLES_ENCHANT);

            if (player.getStatus().getLevel() < 74)
                player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.DO_NOT_HAVE_FURTHER_SKILLS_TO_LEARN_S1).addNumber(74));
            else
                player.sendPacket(SystemMessageId.NO_MORE_SKILLS_TO_LEARN);

            player.sendPacket(AcquireSkillDone.STATIC_PACKET);
        } else
            player.sendPacket(new ExEnchantSkillList(skills));

        player.sendPacket(ActionFailed.STATIC_PACKET);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (command.startsWith("SkillList"))
            showSkillList(player);
        else if (command.startsWith("EnchantSkillList"))
            showEnchantSkillList(player);
        else
            super.onBypassFeedback(player, command);
    }
}