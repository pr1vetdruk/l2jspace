package ru.privetdruk.l2jspace.gameserver.model.actor.instance;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.HennaEquipList;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.HennaUnequipList;

public class SymbolMaker extends Folk {
    public SymbolMaker(int objectID, NpcTemplate template) {
        super(objectID, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (command.equals("Draw"))
            player.sendPacket(new HennaEquipList(player));
        else if (command.equals("RemoveList")) {
            if (player.getHennaList().isEmpty()) {
                player.sendPacket(SystemMessageId.SYMBOL_NOT_FOUND);
                return;
            }

            player.sendPacket(new HennaUnequipList(player));
        } else
            super.onBypassFeedback(player, command);
    }

    @Override
    public String getHtmlPath(int npcId, int val) {
        return "data/html/symbolmaker/SymbolMaker.htm";
    }
}