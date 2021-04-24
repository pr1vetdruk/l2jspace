package ru.privetdruk.l2jspace.gameserver.model.actor.instance;

import java.util.List;

import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.data.manager.FishingChampionshipManager;
import ru.privetdruk.l2jspace.gameserver.data.xml.SkillTreeData;
import ru.privetdruk.l2jspace.gameserver.enums.skills.AcquireSkillType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.model.holder.skillnode.FishingSkillNode;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.AcquireSkillDone;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.AcquireSkillList;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ActionFailed;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.NpcHtmlMessage;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;

/**
 * An instance type extending {@link Merchant}, used for fishing event.
 */
public class Fisherman extends Merchant {
    public Fisherman(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public String getHtmlPath(int npcId, int val) {
        String filename = "";

        if (val == 0)
            filename = "" + npcId;
        else
            filename = npcId + "-" + val;

        return "data/html/fisherman/" + filename + ".htm";
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        // Generic PK check. Send back the HTM if found and cancel current action.
        if (!Config.KARMA_PLAYER_CAN_SHOP && player.getKarma() > 0 && showPkDenyChatWindow(player, "fisherman"))
            return;

        if (command.startsWith("FishSkillList"))
            showFishSkillList(player);
        else if (command.startsWith("FishingChampionship")) {
            if (!Config.ALLOW_FISH_CHAMPIONSHIP) {
                final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                html.setFile("data/html/fisherman/championship/no_fish_event001.htm");
                player.sendPacket(html);
                return;
            }
            FishingChampionshipManager.getInstance().showChampScreen(player, getObjectId());
        } else if (command.startsWith("FishingReward")) {
            if (!Config.ALLOW_FISH_CHAMPIONSHIP) {
                final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                html.setFile("data/html/fisherman/championship/no_fish_event001.htm");
                player.sendPacket(html);
                return;
            }

            if (!FishingChampionshipManager.getInstance().isWinner(player.getName())) {
                final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                html.setFile("data/html/fisherman/championship/no_fish_event_reward001.htm");
                player.sendPacket(html);
                return;
            }
            FishingChampionshipManager.getInstance().getReward(player);
        } else
            super.onBypassFeedback(player, command);
    }

    @Override
    public void showChatWindow(Player player, int val) {
        // Generic PK check. Send back the HTM if found and cancel current action.
        if (!Config.KARMA_PLAYER_CAN_SHOP && player.getKarma() > 0 && showPkDenyChatWindow(player, "fisherman"))
            return;

        showChatWindow(player, getHtmlPath(getNpcId(), val));
    }

    public static void showFishSkillList(Player player) {
        final List<FishingSkillNode> skills = SkillTreeData.getInstance().getFishingSkillsFor(player);
        if (skills.isEmpty()) {
            final int minlevel = SkillTreeData.getInstance().getRequiredLevelForNextFishingSkill(player);
            if (minlevel > 0)
                player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.DO_NOT_HAVE_FURTHER_SKILLS_TO_LEARN_S1).addNumber(minlevel));
            else
                player.sendPacket(SystemMessageId.NO_MORE_SKILLS_TO_LEARN);

            player.sendPacket(AcquireSkillDone.STATIC_PACKET);
        } else
            player.sendPacket(new AcquireSkillList(AcquireSkillType.FISHING, skills));

        player.sendPacket(ActionFailed.STATIC_PACKET);
    }
}