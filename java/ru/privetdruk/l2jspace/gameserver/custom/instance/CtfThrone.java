package ru.privetdruk.l2jspace.gameserver.custom.instance;

import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;
import ru.privetdruk.l2jspace.gameserver.custom.event.CTF;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;

public class CtfThrone extends Npc {
    public CtfThrone(int objectId, NpcTemplate template) {
        super(objectId, template, "custom/event/");
    }

    @Override
    public void onInteract(Player player) {
        CTF event = (CTF) EventEngine.findActive();

        if (event == null || event.getEventState() != EventState.IN_PROGRESS || !event.getPlayers().containsKey(player.getObjectId())) {
            return;
        }

        event.restoreFlag();
    }
}
