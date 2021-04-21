package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import java.util.Map.Entry;

import ru.privetdruk.l2jspace.Config;
import ru.privetdruk.l2jspace.gameserver.communitybbs.manager.MailBBSManager;
import ru.privetdruk.l2jspace.gameserver.data.SkillTable.FrequentSkill;
import ru.privetdruk.l2jspace.gameserver.data.manager.CastleManager;
import ru.privetdruk.l2jspace.gameserver.data.manager.ClanHallManager;
import ru.privetdruk.l2jspace.gameserver.custom.service.WeddingService;
import ru.privetdruk.l2jspace.gameserver.data.manager.DimensionalRiftManager;
import ru.privetdruk.l2jspace.gameserver.data.manager.PetitionManager;
import ru.privetdruk.l2jspace.gameserver.data.manager.SevenSignsManager;
import ru.privetdruk.l2jspace.gameserver.data.xml.AdminData;
import ru.privetdruk.l2jspace.gameserver.data.xml.AnnouncementData;
import ru.privetdruk.l2jspace.gameserver.data.xml.MapRegionData.TeleportType;
import ru.privetdruk.l2jspace.gameserver.enums.CabalType;
import ru.privetdruk.l2jspace.gameserver.enums.SealType;
import ru.privetdruk.l2jspace.gameserver.enums.SiegeSide;
import ru.privetdruk.l2jspace.gameserver.enums.ZoneId;
import ru.privetdruk.l2jspace.gameserver.enums.actors.ClassRace;
import ru.privetdruk.l2jspace.gameserver.model.World;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.clanhall.ClanHall;
import ru.privetdruk.l2jspace.gameserver.model.clanhall.SiegableHall;
import ru.privetdruk.l2jspace.gameserver.model.entity.Castle;
import ru.privetdruk.l2jspace.gameserver.model.entity.Siege;
import ru.privetdruk.l2jspace.gameserver.model.holder.IntIntHolder;
import ru.privetdruk.l2jspace.gameserver.model.olympiad.Olympiad;
import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;
import ru.privetdruk.l2jspace.gameserver.model.pledge.SubPledge;
import ru.privetdruk.l2jspace.gameserver.network.GameClient.GameClientState;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ActionFailed;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.Die;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.EtcStatusUpdate;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ExMailArrived;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ExStorageMaxCount;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.FriendList;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.HennaInfo;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ItemList;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.NpcHtmlMessage;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.PlaySound;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.PledgeShowMemberListAll;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.PledgeSkillList;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.QuestList;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ShortCutInit;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SkillCoolTime;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.UserInfo;
import ru.privetdruk.l2jspace.gameserver.scripting.Quest;
import ru.privetdruk.l2jspace.gameserver.scripting.QuestState;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;
import ru.privetdruk.l2jspace.gameserver.taskmanager.GameTimeTaskManager;

public class EnterWorld extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null) {
            getClient().closeNow();
            return;
        }

        getClient().setState(GameClientState.IN_GAME);

        final int objectId = player.getObjectId();

        if (player.isGM()) {
            if (Config.GM_STARTUP_INVULNERABLE && AdminData.getInstance().hasAccess("admin_invul", player.getAccessLevel()))
                player.setInvul(true);

            if (Config.GM_STARTUP_INVISIBLE && AdminData.getInstance().hasAccess("admin_hide", player.getAccessLevel()))
                player.getAppearance().setVisible(false);

            if (Config.GM_STARTUP_BLOCK_ALL)
                player.getBlockList().setInBlockingAll(true);

            if (Config.GM_STARTUP_AUTO_LIST && AdminData.getInstance().hasAccess("admin_gmlist", player.getAccessLevel()))
                AdminData.getInstance().addGm(player, false);
            else
                AdminData.getInstance().addGm(player, true);
        }

        // Set dead status if applies
        if (player.getStatus().getHp() < 0.5 && player.isMortal())
            player.setIsDead(true);

        player.getMacroList().sendUpdate();
        player.sendPacket(new ExStorageMaxCount(player));
        player.sendPacket(new HennaInfo(player));
        player.updateEffectIcons();
        player.sendPacket(new EtcStatusUpdate(player));

        // Clan checks.
        final Clan clan = player.getClan();
        if (clan != null) {
            player.sendPacket(new PledgeSkillList(clan));

            // Refresh player instance.
            clan.getClanMember(objectId).setPlayerInstance(player);

            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_LOGGED_IN).addCharName(player);
            final PledgeShowMemberListUpdate psmlu = new PledgeShowMemberListUpdate(player);

            // Send packets to others members.
            for (Player member : clan.getOnlineMembers()) {
                if (member == player)
                    continue;

                member.sendPacket(sm);
                member.sendPacket(psmlu);
            }

            // Send a login notification to sponsor or apprentice, if logged.
            if (player.getSponsor() != 0) {
                final Player sponsor = World.getInstance().getPlayer(player.getSponsor());
                if (sponsor != null)
                    sponsor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_APPRENTICE_S1_HAS_LOGGED_IN).addCharName(player));
            } else if (player.getApprentice() != 0) {
                final Player apprentice = World.getInstance().getPlayer(player.getApprentice());
                if (apprentice != null)
                    apprentice.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_SPONSOR_S1_HAS_LOGGED_IN).addCharName(player));
            }

            // Add message at connexion if clanHall not paid.
            final ClanHall ch = ClanHallManager.getInstance().getClanHallByOwner(clan);
            if (ch != null && !ch.getPaid())
                player.sendPacket(SystemMessageId.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW);

            for (Castle castle : CastleManager.getInstance().getCastles()) {
                final Siege siege = castle.getSiege();
                if (!siege.isInProgress())
                    continue;

                final SiegeSide type = siege.getSide(clan);
                if (type == SiegeSide.ATTACKER)
                    player.setSiegeState((byte) 1);
                else if (type == SiegeSide.DEFENDER || type == SiegeSide.OWNER)
                    player.setSiegeState((byte) 2);
            }

            for (SiegableHall hall : ClanHallManager.getInstance().getSiegableHalls()) {
                if (!hall.isInSiege())
                    continue;

                if (hall.isRegistered(clan)) {
                    player.setSiegeState((byte) 1);
                    player.setInSiegableHallSiege(true);
                }
            }

            player.sendPacket(new PledgeShowMemberListUpdate(player));
            player.sendPacket(new PledgeShowMemberListAll(clan, 0));

            for (SubPledge sp : clan.getAllSubPledges())
                player.sendPacket(new PledgeShowMemberListAll(clan, sp.getId()));

            player.sendPacket(new UserInfo(player));
        }

        // Updating Seal of Strife Buff/Debuff
        if (SevenSignsManager.getInstance().isSealValidationPeriod() && SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE) != CabalType.NORMAL) {
            CabalType cabal = SevenSignsManager.getInstance().getPlayerCabal(objectId);
            if (cabal != CabalType.NORMAL) {
                if (cabal == SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE))
                    player.addSkill(FrequentSkill.THE_VICTOR_OF_WAR.getSkill(), false);
                else
                    player.addSkill(FrequentSkill.THE_VANQUISHED_OF_WAR.getSkill(), false);
            }
        } else {
            player.removeSkill(FrequentSkill.THE_VICTOR_OF_WAR.getSkill().getId(), false);
            player.removeSkill(FrequentSkill.THE_VANQUISHED_OF_WAR.getSkill().getId(), false);
        }

        if (Config.PLAYER_SPAWN_PROTECTION > 0)
            player.setSpawnProtection(true);

        player.spawnMe();

        // Set the location of debug packets.
        player.setEnterWorldLoc(player.getX(), player.getY(), -16000);

        // Engage and notify partner.
        if (Config.ALLOW_WEDDING) {
            for (Entry<Integer, IntIntHolder> coupleEntry : WeddingService.getInstance().getCouples().entrySet()) {
                final IntIntHolder couple = coupleEntry.getValue();
                if (couple.getId() == objectId || couple.getValue() == objectId) {
                    player.setCoupleId(coupleEntry.getKey());
                    break;
                }
            }
        }

        // Announcements, welcome & Seven signs period messages.
        player.sendPacket(SystemMessageId.WELCOME_TO_LINEAGE);
        player.sendPacket(SevenSignsManager.getInstance().getCurrentPeriod().getMessageId());
        AnnouncementData.getInstance().showAnnouncements(player, false);

        // If the Player is a Dark Elf, check for Shadow Sense at night.
        if (player.getRace() == ClassRace.DARK_ELF && player.hasSkill(L2Skill.SKILL_SHADOW_SENSE))
            player.sendPacket(SystemMessage.getSystemMessage((GameTimeTaskManager.getInstance().isNight()) ? SystemMessageId.NIGHT_S1_EFFECT_APPLIES : SystemMessageId.DAY_S1_EFFECT_DISAPPEARS).addSkillName(L2Skill.SKILL_SHADOW_SENSE));

        // Notify quest for enterworld event, if quest allows it.
        player.getQuestList().getQuests(Quest::isTriggeredOnEnterWorld).forEach(q -> q.notifyEnterWorld(player));

        player.sendPacket(new QuestList(player));
        player.sendSkillList();
        player.sendPacket(new FriendList(player));
        player.sendPacket(new UserInfo(player));
        player.sendPacket(new ItemList(player, false));
        player.sendPacket(new ShortCutInit(player));

        player.checkCondition(player.getStatus().getMaxHp(), player.getStatus().getHp());

        // No broadcast needed since the player will already spawn dead to others.
        if (player.isAlikeDead())
            player.sendPacket(new Die(player));

        // Unread mails make a popup appears.
        if (Config.ENABLE_COMMUNITY_BOARD && MailBBSManager.getInstance().checkIfUnreadMail(player)) {
            player.sendPacket(SystemMessageId.NEW_MAIL);
            player.sendPacket(new PlaySound("systemmsg_e.1233"));
            player.sendPacket(ExMailArrived.STATIC_PACKET);
        }

        // Clan notice, if active.
        if (Config.ENABLE_COMMUNITY_BOARD && clan != null && clan.isNoticeEnabled()) {
            final NpcHtmlMessage html = new NpcHtmlMessage(0);
            html.setFile("data/html/clan_notice.htm");
            html.replace("%clan_name%", clan.getName());
            html.replace("%notice_text%", clan.getNotice().replaceAll("\r\n", "<br>").replaceAll("action", "").replaceAll("bypass", ""));
            sendPacket(html);
        } else if (Config.SERVER_NEWS) {
            final NpcHtmlMessage html = new NpcHtmlMessage(0);
            html.setFile("data/html/servnews.htm");
            sendPacket(html);
        }

        PetitionManager.getInstance().checkActivePetition(player);

        player.onPlayerEnter();

        sendPacket(new SkillCoolTime(player));

        // If player logs back in a stadium, port him in nearest town.
        if (Olympiad.getInstance().playerInStadia(player))
            player.teleportTo(TeleportType.TOWN);

        if (DimensionalRiftManager.getInstance().checkIfInRiftZone(player.getX(), player.getY(), player.getZ(), false))
            DimensionalRiftManager.getInstance().teleportToWaitingRoom(player);

        if (player.getClanJoinExpiryTime() > System.currentTimeMillis())
            player.sendPacket(SystemMessageId.CLAN_MEMBERSHIP_TERMINATED);

        // Attacker or spectator logging into a siege zone will be ported at town.
        if (player.isInsideZone(ZoneId.SIEGE) && player.getSiegeState() < 2)
            player.teleportTo(TeleportType.TOWN);

        // Tutorial
        final QuestState qs = player.getQuestList().getQuestState("Tutorial");
        if (qs != null)
            qs.getQuest().notifyEvent("UC", null, player);

        player.sendPacket(ActionFailed.STATIC_PACKET);
    }

    @Override
    protected boolean triggersOnActionRequest() {
        return false;
    }
}