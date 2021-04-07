package ru.privetdruk.l2jspace.gameserver.model.actor.instance;

import ru.privetdruk.l2jspace.gameserver.enums.ZoneId;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class SiegeSummon extends Servitor {
    public static final int SIEGE_GOLEM_ID = 14737;
    public static final int HOG_CANNON_ID = 14768;
    public static final int SWOOP_CANNON_ID = 14839;

    public SiegeSummon(int objectId, NpcTemplate template, Player owner, L2Skill skill) {
        super(objectId, template, owner, skill);
    }

    @Override
    public void onSpawn() {
        super.onSpawn();

        if (!isInsideZone(ZoneId.SIEGE)) {
            unSummon(getOwner());
            getOwner().sendPacket(SystemMessageId.YOUR_SERVITOR_HAS_VANISHED);
        }
    }

    @Override
    public void onTeleported() {
        super.onTeleported();

        if (!isInsideZone(ZoneId.SIEGE)) {
            unSummon(getOwner());
            getOwner().sendPacket(SystemMessageId.YOUR_SERVITOR_HAS_VANISHED);
        }
    }
}