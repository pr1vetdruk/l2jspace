package ru.privetdruk.l2jspace.gameserver.skill.l2skill;

import ru.privetdruk.l2jspace.common.data.StatSet;
import ru.privetdruk.l2jspace.gameserver.data.manager.CastleManager;
import ru.privetdruk.l2jspace.gameserver.data.manager.ClanHallManager;
import ru.privetdruk.l2jspace.gameserver.enums.SiegeSide;
import ru.privetdruk.l2jspace.gameserver.enums.ZoneId;
import ru.privetdruk.l2jspace.gameserver.idfactory.IdFactory;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.SiegeFlag;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.model.entity.ClanHallSiege;
import ru.privetdruk.l2jspace.gameserver.model.entity.Siege;
import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

import static ru.privetdruk.l2jspace.gameserver.network.SystemMessageId.*;
import static ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

public class L2SkillSiegeFlag extends L2Skill {
    private final boolean _isAdvanced;

    public L2SkillSiegeFlag(StatSet set) {
        super(set);

        _isAdvanced = set.getBool("isAdvanced", false);
    }

    @Override
    public void useSkill(Creature activeChar, WorldObject[] targets) {
        if (!(activeChar instanceof Player))
            return;

        final Player player = activeChar.getActingPlayer();

        if (!check(player, true))
            return;

        final Clan clan = player.getClan();
        if (clan == null)
            return;

        // Template initialization
        final StatSet npcDat = new StatSet();

        npcDat.set("id", 35062);
        npcDat.set("type", "");

        npcDat.set("name", clan.getName());
        npcDat.set("usingServerSideName", true);

        npcDat.set("hp", (_isAdvanced) ? 100000 : 50000);
        npcDat.set("mp", 0);

        npcDat.set("radius", 10);
        npcDat.set("height", 80);

        npcDat.set("pAtk", 0);
        npcDat.set("mAtk", 0);
        npcDat.set("pDef", 500);
        npcDat.set("mDef", 500);

        npcDat.set("runSpd", 0); // Have to keep this, static object MUST BE 0 (critical error otherwise).

        // Spawn a new flag.
        final SiegeFlag flag = new SiegeFlag(clan, IdFactory.getInstance().getNextId(), new NpcTemplate(npcDat));
        flag.getStatus().setMaxHp();
        flag.spawnMe(player.getPosition());
    }

    /**
     * @param player      : The {@link Player} to test.
     * @param isCheckOnly : If false, send a notification to the {@link Player} telling him why the operation failed.
     * @return True if the {@link Player} can place a {@link SiegeFlag}.
     */
    public static boolean check(Player player, boolean isCheckOnly) {
        boolean isAttackerUnderActiveSiege = false;

        // Check first if an active Siege is under process.
        Siege siege = CastleManager.getInstance().getActiveSiege(player);
        if (siege != null) {
            isAttackerUnderActiveSiege = siege.checkSide(player.getClan(), SiegeSide.ATTACKER);
        } else {
            // If no Siege, check ClanHallSiege.
            ClanHallSiege chs = ClanHallManager.getInstance().getActiveSiege(player);
            if (chs != null) {
                isAttackerUnderActiveSiege = chs.checkSide(player.getClan(), SiegeSide.ATTACKER);
            }
        }

        SystemMessage sm = null;
        if (!isAttackerUnderActiveSiege) {
            sm = getSystemMessage(S1_CANNOT_BE_USED).addSkillName(247);
        } else if (!player.isClanLeader()) {
            sm = getSystemMessage(ONLY_CLAN_LEADER_CAN_ISSUE_COMMANDS);
        } else if (player.getClan().getFlag() != null) {
            sm = getSystemMessage(NOT_ANOTHER_HEADQUARTERS);
        } else if (!player.isInsideZone(ZoneId.HQ)) {
            sm = getSystemMessage(NOT_SET_UP_BASE_HERE);
        } else if (!player.getKnownTypeInRadius(SiegeFlag.class, 400).isEmpty()) {
            sm = getSystemMessage(HEADQUARTERS_TOO_CLOSE);
        }

        if (sm != null && !isCheckOnly) {
            player.sendPacket(sm);
        }

        return sm == null;
    }
}