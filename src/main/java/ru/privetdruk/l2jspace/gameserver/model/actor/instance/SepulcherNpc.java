package ru.privetdruk.l2jspace.gameserver.model.actor.instance;

import ru.privetdruk.l2jspace.common.pool.ThreadPool;
import ru.privetdruk.l2jspace.gameserver.data.manager.FourSepulchersManager;
import ru.privetdruk.l2jspace.gameserver.data.xml.DoorData;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.model.group.Party;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ActionFailed;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.NpcHtmlMessage;

import java.util.Calendar;

public class SepulcherNpc extends Folk {
    private static final String HTML_FILE_PATH = "data/html/sepulchers/";
    private static final int HALLS_KEY = 7260;

    public SepulcherNpc(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onInteract(Player player) {
        if (isDead()) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        switch (getNpcId()) {
            case 31468, 31469, 31470, 31471, 31472, 31473, 31474, 31475, 31476, 31477, 31478, 31479, 31480, 31481, 31482, 31483, 31484, 31485, 31486, 31487 -> {
                // Time limit is reached. You can't open anymore Mysterious boxes after the 49th minute.
                if (Calendar.getInstance().get(Calendar.MINUTE) >= 50) {
                    broadcastNpcSay("You can start at the scheduled time.");
                    return;
                }
                FourSepulchersManager.getInstance().spawnMonster(getNpcId());
                deleteMe();
            }
            case 31455, 31456, 31457, 31458, 31459, 31460, 31461, 31462, 31463, 31464, 31465, 31466, 31467 -> {
                if (player.isInParty() && !player.getParty().isLeader(player))
                    player = player.getParty().getLeader();
                player.addItem("Quest", HALLS_KEY, 1, player, true);
                deleteMe();
            }
            default -> super.onInteract(player);
        }
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }

    @Override
    public String getHtmlPath(int npcId, int val) {
        String filename = "";
        if (val == 0)
            filename = "" + npcId;
        else
            filename = npcId + "-" + val;

        return HTML_FILE_PATH + filename + ".htm";
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (command.startsWith("open_gate")) {
            final ItemInstance hallsKey = player.getInventory().getItemByItemId(HALLS_KEY);
            if (hallsKey == null)
                showHtmlFile(player, "Gatekeeper-no.htm");
            else if (FourSepulchersManager.getInstance().isAttackTime()) {
                switch (getNpcId()) {
                    case 31929:
                    case 31934:
                    case 31939:
                    case 31944:
                        FourSepulchersManager.getInstance().spawnShadow(getNpcId());
                    default: {
                        openNextDoor(getNpcId());

                        final Party party = player.getParty();
                        if (party != null) {
                            for (Player member : player.getParty().getMembers()) {
                                final ItemInstance key = member.getInventory().getItemByItemId(HALLS_KEY);
                                if (key != null)
                                    member.destroyItemByItemId("Quest", HALLS_KEY, key.getCount(), member, true);
                            }
                        } else
                            player.destroyItemByItemId("Quest", HALLS_KEY, hallsKey.getCount(), player, true);
                    }
                }
            }
        } else
            super.onBypassFeedback(player, command);
    }

    public void openNextDoor(int npcId) {
        final int doorId = FourSepulchersManager.getInstance().getHallGateKeepers().get(npcId);
        final Door door = DoorData.getInstance().getDoor(doorId);

        // Open the door.
        door.openMe();

        // Schedule the automatic door close.
        ThreadPool.schedule(() -> door.closeMe(), 10000);

        // Spawn the next mysterious box.
        FourSepulchersManager.getInstance().spawnMysteriousBox(npcId);

        sayInShout("The monsters have spawned!");
    }

    public void sayInShout(String msg) {
        if (msg == null || msg.isEmpty())
            return;

        broadcastNpcShout(msg);
    }

    public void showHtmlFile(Player player, String file) {
        final NpcHtmlMessage html = new NpcHtmlMessage(getId());
        html.setFile("data/html/sepulchers/" + file);
        html.replace("%npcname%", getName());
        player.sendPacket(html);
    }
}