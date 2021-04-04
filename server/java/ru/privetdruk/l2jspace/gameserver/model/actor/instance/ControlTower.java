package ru.privetdruk.l2jspace.gameserver.model.actor.instance;

import java.util.ArrayList;
import java.util.List;

import ru.privetdruk.l2jspace.gameserver.enums.SiegeSide;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.model.entity.Siege;
import ru.privetdruk.l2jspace.gameserver.model.spawn.Spawn;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;

public class ControlTower extends Npc {
    private final List<Spawn> _spawns = new ArrayList<>();

    private boolean _isActive = true;

    public ControlTower(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public boolean isAttackableBy(Creature attacker) {
        if (!super.isAttackableBy(attacker))
            return false;

        if (!(attacker instanceof Playable))
            return false;

        if (getCastle() != null && getCastle().getSiege().isInProgress())
            return getCastle().getSiege().checkSides(attacker.getActingPlayer().getClan(), SiegeSide.ATTACKER);

        return false;
    }

    @Override
    public boolean isAttackableWithoutForceBy(Playable attacker) {
        return isAttackableBy(attacker);
    }

    @Override
    public void onInteract(Player player) {
    }

    @Override
    public boolean doDie(Creature killer) {
        if (getCastle() != null) {
            final Siege siege = getCastle().getSiege();
            if (siege.isInProgress()) {
                _isActive = false;

                for (Spawn spawn : _spawns)
                    spawn.setRespawnState(false);

                // If siege life controls reach 0, broadcast a message to defenders.
                if (siege.getControlTowerCount() == 0)
                    siege.announce(SystemMessageId.TOWER_DESTROYED_NO_RESURRECTION, SiegeSide.DEFENDER);

                // Spawn a little version of it. This version is a simple NPC, cleaned on siege end.
                try {
                    final Spawn spawn = new Spawn(13003);
                    spawn.setLoc(getPosition());

                    final Npc tower = spawn.doSpawn(false);
                    tower.setCastle(getCastle());

                    siege.getDestroyedTowers().add(tower);
                } catch (Exception e) {
                    LOGGER.error("Couldn't spawn the control tower.", e);
                }
            }
        }
        return super.doDie(killer);
    }

    @Override
    public boolean hasRandomAnimation() {
        return false;
    }

    public final List<Spawn> getSpawns() {
        return _spawns;
    }

    public void addSpawn(Spawn spawn) {
        _spawns.add(spawn);
    }

    public final boolean isActive() {
        return _isActive;
    }
}