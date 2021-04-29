package ru.privetdruk.l2jspace.gameserver.custom.instance;

import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventBypass;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ActionFailed;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.NpcHtmlMessage;

import java.util.logging.Logger;

import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventBypass.JOIN_TEAM;

public class EventManager extends Npc {
    protected static final Logger LOGGER = Logger.getLogger(EventManager.class.getName());
    private static final int FIRST_ELEMENT = 0;

    public EventManager(int objectId, NpcTemplate template) {
        super(objectId, template, "custom/event/");
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        EventEngine event = EventEngine.findActive();

        EventBypass action = EventBypass.fromBypass(command.split(" ")[FIRST_ELEMENT]);

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
            case JOIN_TEAM -> event.registerPlayer(player, command.substring(JOIN_TEAM.getBypass().length()));
            case LEAVE -> event.excludePlayer(player);
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
        html.replace("%objectId%", getObjectId());
        html.replace("%content%", event.configurePageContent(player));
        player.sendPacket(html);

        player.sendPacket(ActionFailed.STATIC_PACKET);
    }
}
