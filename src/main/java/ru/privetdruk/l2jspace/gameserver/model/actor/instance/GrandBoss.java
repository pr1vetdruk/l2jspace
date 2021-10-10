package ru.privetdruk.l2jspace.gameserver.model.actor.instance;

import ru.privetdruk.l2jspace.common.random.Rnd;
import ru.privetdruk.l2jspace.gameserver.data.manager.HeroManager;
import ru.privetdruk.l2jspace.gameserver.data.manager.RaidPointManager;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.model.group.Party;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.PlaySound;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;

/**
 * This class manages all {@link GrandBoss}es.<br>
 * <br>
 * Those npcs inherit from {@link Monster}. Since a script is generally associated to it, {@link GrandBoss#returnHome} returns false to avoid misbehavior. No random walking is allowed.
 */
public final class GrandBoss extends Monster {
    public GrandBoss(int objectId, NpcTemplate template) {
        super(objectId, template);
        setRaid(true);
    }

    @Override
    public void onSpawn() {
        setNoRndWalk(true);
        super.onSpawn();
    }

    @Override
    public boolean doDie(Creature killer) {
        if (!super.doDie(killer))
            return false;

        final Player player = killer.getActingPlayer();
        if (player != null) {
            broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.RAID_WAS_SUCCESSFUL));
            broadcastPacket(new PlaySound("systemmsg_e.1209"));

            final Party party = player.getParty();
            if (party != null) {
                for (Player member : party.getMembers()) {
                    RaidPointManager.getInstance().addPoints(member, getNpcId(), (getStatus().getLevel() / 2) + Rnd.get(-5, 5));
                    if (member.isNoble())
                        HeroManager.getInstance().setRBkilled(member.getId(), getNpcId());
                }
            } else {
                RaidPointManager.getInstance().addPoints(player, getNpcId(), (getStatus().getLevel() / 2) + Rnd.get(-5, 5));
                if (player.isNoble())
                    HeroManager.getInstance().setRBkilled(player.getId(), getNpcId());
            }
        }

        return true;
    }

    @Override
    public boolean returnHome() {
        return false;
    }
}