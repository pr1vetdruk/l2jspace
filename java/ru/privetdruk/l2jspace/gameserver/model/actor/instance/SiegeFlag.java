package ru.privetdruk.l2jspace.gameserver.model.actor.instance;

import ru.privetdruk.l2jspace.common.pool.ThreadPool;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

import java.util.concurrent.Future;

public class SiegeFlag extends Npc {
    private final Clan _clan;

    private Future<?> _task;

    public SiegeFlag(Clan clan, int objectId, NpcTemplate template) {
        super(objectId, template);

        _clan = clan;
        _clan.setFlag(this);
    }

    @Override
    public boolean doDie(Creature killer) {
        if (!super.doDie(killer))
            return false;

        // Reset clan flag to null.
        _clan.setFlag(null);

        return true;
    }

    @Override
    public void deleteMe() {
        // Stop the task.
        if (_task != null) {
            _task.cancel(false);
            _task = null;
        }

        super.deleteMe();
    }

    @Override
    public void onInteract(Player player) {
    }

    @Override
    public boolean hasRandomAnimation() {
        return false;
    }

    @Override
    public void reduceCurrentHp(double damage, Creature attacker, L2Skill skill) {
        // Any SiegeSummon can hurt SiegeFlag (excepted Swoop Cannon - anti-infantery summon).
        if (attacker instanceof SiegeSummon && ((SiegeSummon) attacker).getNpcId() == SiegeSummon.SWOOP_CANNON_ID)
            return;

        // Send warning to owners of headquarters that theirs base is under attack.
        if (_task == null) {
            _task = ThreadPool.schedule(() -> _task = null, 30000);

            _clan.broadcastToMembers(SystemMessage.getSystemMessage(SystemMessageId.BASE_UNDER_ATTACK));
        }
        super.reduceCurrentHp(damage, attacker, skill);
    }

    @Override
    public void addFunctionsToNewCharacter() {
    }

    @Override
    public boolean canBeHealed() {
        return false;
    }
}