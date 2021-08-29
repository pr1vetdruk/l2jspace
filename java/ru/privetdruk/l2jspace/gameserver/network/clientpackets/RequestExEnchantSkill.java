package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.common.random.Rnd;
import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.data.SkillTable;
import ru.privetdruk.l2jspace.gameserver.data.xml.SkillTreeData;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Folk;
import ru.privetdruk.l2jspace.gameserver.model.holder.skillnode.EnchantSkillNode;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.UserInfo;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public final class RequestExEnchantSkill extends L2GameClientPacket {
    private int _skillId;
    private int _skillLevel;

    @Override
    protected void readImpl() {
        _skillId = readD();
        _skillLevel = readD();
    }

    @Override
    protected void runImpl() {
        if (_skillId <= 0 || _skillLevel <= 0)
            return;

        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        if (player.getClassId().getLevel() < 3 || player.getStatus().getLevel() < 76)
            return;

        final Folk folk = player.getCurrentFolk();
        if (folk == null || !player.getAI().canDoInteract(folk))
            return;

        if (player.getSkillLevel(_skillId) >= _skillLevel)
            return;

        final L2Skill skill = SkillTable.getInstance().getInfo(_skillId, _skillLevel);
        if (skill == null)
            return;

        final EnchantSkillNode esn = SkillTreeData.getInstance().getEnchantSkillFor(player, _skillId, _skillLevel);
        if (esn == null)
            return;

        // Check exp and sp neccessary to enchant skill.
        if (player.getStatus().getSp() < esn.getSp()) {
            player.sendPacket(SystemMessageId.YOU_DONT_HAVE_ENOUGH_SP_TO_ENCHANT_THAT_SKILL);
            return;
        }

        if (player.getStatus().getExp() - esn.getExp() < player.getStatus().getExpForLevel(76)) {
            player.sendPacket(SystemMessageId.YOU_DONT_HAVE_ENOUGH_EXP_TO_ENCHANT_THAT_SKILL);
            return;
        }

        // Check item restriction, and try to consume item.
        if (Config.ES_SP_BOOK_NEEDED && esn.getItem() != null && !player.destroyItemByItemId("SkillEnchant", esn.getItem().getId(), esn.getItem().getValue(), folk, true)) {
            player.sendPacket(SystemMessageId.YOU_DONT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL);
            return;
        }

        // All conditions fulfilled, consume exp and sp.
        player.removeExpAndSp(esn.getExp(), esn.getSp());

        // Try to enchant skill.
        if (Rnd.get(100) <= esn.getEnchantRate(player.getStatus().getLevel())) {
            player.addSkill(skill, true, true);
            player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_SUCCEEDED_IN_ENCHANTING_THE_SKILL_S1).addSkillName(_skillId, _skillLevel));
        } else {
            player.addSkill(SkillTable.getInstance().getInfo(_skillId, SkillTable.getInstance().getMaxLevel(_skillId)), true, true);
            player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_ENCHANT_THE_SKILL_S1).addSkillName(_skillId, _skillLevel));
        }

        player.sendSkillList();
        player.sendPacket(new UserInfo(player));

        // Show enchant skill list.
        folk.showEnchantSkillList(player);
    }
}