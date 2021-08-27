package ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;

import ru.privetdruk.l2jspace.gameserver.handler.IAdminCommandHandler;
import ru.privetdruk.l2jspace.gameserver.model.PetDataEntry;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.Summon;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Pet;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.GMViewItemList;

public class AdminSummon implements IAdminCommandHandler {
    private static final String[] ADMIN_COMMANDS =
            {
                    "admin_ride",
                    "admin_unride",
                    "admin_unsummon",
                    "admin_summon"
            };

    @Override
    public void useAdminCommand(String command, Player player) {
        if (command.startsWith("admin_ride")) {
            if (player.isCursedWeaponEquipped()) {
                player.sendMessage("You can't use //ride owning a Cursed Weapon.");
                return;
            }

            StringTokenizer st = new StringTokenizer(command, " ");
            st.nextToken();

            if (!st.hasMoreTokens()) {
                player.sendMessage("You must enter a parameter for that command.");
                return;
            }

            String mount = st.nextToken();

            int npcId;
            if (mount.equals("wyvern") || mount.equals("2")) {
                npcId = 12621;
            } else if (mount.equals("strider") || mount.equals("1")) {
                npcId = 12526;
            } else {
                player.sendMessage("Parameter '" + mount + "' isn't recognized for that command.");
                return;
            }

            if (player.isMounted()) {
                player.dismount();
            } else if (player.getSummon() != null) {
                player.getSummon().unSummon(player);
            }

            player.mount(npcId, 0);
        } else if (command.equals("admin_unride")) {
            player.dismount();
        } else {
            Player targetPlayer = getTarget(Playable.class, player, true).getActingPlayer();
            if (targetPlayer == null) {
                player.sendPacket(SystemMessageId.INVALID_TARGET);
                return;
            }

            Summon summon = targetPlayer.getSummon();

            if (command.startsWith("admin_unsummon")) {
                if (summon == null) {
                    player.sendPacket(SystemMessageId.INVALID_TARGET);
                    return;
                }

                summon.unSummon(targetPlayer);
            } else if (command.startsWith("admin_summon")) {
                if (!(summon instanceof Pet)) {
                    player.sendPacket(SystemMessageId.INVALID_TARGET);
                    return;
                }

                StringTokenizer st = new StringTokenizer(command);
                st.nextToken();

                try {
                    switch (st.nextToken()) {
                        case "food":
                            ((Pet) summon).setCurrentFed(((Pet) summon).getPetData().getMaxMeal());
                            break;
                        case "inventory":
                            player.sendPacket(new GMViewItemList((Pet) summon));
                            break;
                        case "level":
                            int level = Integer.parseInt(st.nextToken());

                            PetDataEntry pde = ((Pet) summon).getTemplate().getPetDataEntry(level);
                            if (pde == null) {
                                player.sendMessage("Invalid level for //summon level.");
                                return;
                            }

                            long oldExp = summon.getStatus().getExp();
                            long newExp = pde.getMaxExp();

                            if (oldExp > newExp) {
                                summon.getStatus().removeExp(oldExp - newExp);
                            } else if (oldExp < newExp) {
                                summon.getStatus().addExp(newExp - oldExp);
                            }

                            break;
                        default:
                            player.sendMessage("Usage: //summon food|inventory|level>");
                            break;
                    }
                } catch (Exception e) {
                    player.sendMessage("Usage: //summon food|inventory|level>");
                }
            }
        }
    }

    @Override
    public String[] getAdminCommandList() {
        return ADMIN_COMMANDS;
    }
}