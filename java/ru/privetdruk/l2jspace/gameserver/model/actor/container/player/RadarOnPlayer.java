package ru.privetdruk.l2jspace.gameserver.model.actor.container.player;

import ru.privetdruk.l2jspace.common.pool.ThreadPool;
import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.CtfEventPlayer;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.RadarControl;

import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventType.CTF;

public class RadarOnPlayer implements Runnable {
    private final Player player;
    private final Player targetPlayer;

        public RadarOnPlayer(Player player, Player targetPlayer) {
            this.player = player;
            this.targetPlayer = targetPlayer;
        }

        @Override
        public void run() {
            try {
                if (player == null || !player.isOnline()) {
                    return;
                }

                player.sendPacket(new RadarControl(1, 1, player.getX(), player.getY(), player.getZ()));

                EventEngine event = EventEngine.findActive();

                if (targetPlayer == null || !targetPlayer.isOnline() || event == null) {
                    return;
                }

                if (event.getEventType() == CTF) {
                    CtfEventPlayer eventPlayer = (CtfEventPlayer) event.getPlayers().get(targetPlayer.getObjectId());
                    if (eventPlayer != null && eventPlayer.isHasFlag()) {
                        return;
                    }
                }

                player.sendPacket(new RadarControl(0, 1, targetPlayer.getX(), targetPlayer.getY(), targetPlayer.getZ()));

                ThreadPool.schedule(new RadarOnPlayer(targetPlayer, player), 15000);
            } catch (Throwable ignored) {
            }
        }
    }