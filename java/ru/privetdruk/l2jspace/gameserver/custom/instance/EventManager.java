package ru.privetdruk.l2jspace.gameserver.custom.instance;

import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventBypass;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ActionFailed;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.NpcHtmlMessage;

import java.util.logging.Logger;

public class EventManager extends Npc {
    protected static final Logger LOGGER = Logger.getLogger(EventManager.class.getName());

    private static final int COMMAND = 0;
    private static final int TEAM_NAME = 1;

    public EventManager(int objectId, NpcTemplate template) {
        super(objectId, template, "custom/event/");
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        EventEngine event = EventEngine.findActive();

        String[] parameters = command.split(" ");
        EventBypass action = EventBypass.fromBypass(parameters[COMMAND]);

        if (action == null) {
            LOGGER.warning(String.format(
                    "Perhaps a cheater, nickname: %s, id: %d. Unknown bypass: %s.",
                    player.getName(),
                    player.getObjectId(),
                    command
            ));

            return;
        }

        switch (action) {
            case JOIN_TEAM -> event.register(player, parameters[TEAM_NAME]);
            case LEAVE -> event.leave(player);
        }

        player.sendPacket(ActionFailed.STATIC_PACKET);
    }

    @Override
    public void showChatWindow(Player player, String filename) {
        EventEngine event = EventEngine.findActive();

        if (event == null) {
            return;
        }

        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        html.setFile(filename);
        html.replace("%content%", event.configureMainPageContent(player));
        html.replace("%eventName%", event.getSettings().getEventName());
        html.replace("%eventDescription%", event.getSettings().getEventDescription());
        html.replace("%objectId%", getObjectId());
        player.sendPacket(html);

        player.sendPacket(ActionFailed.STATIC_PACKET);
    }
}
