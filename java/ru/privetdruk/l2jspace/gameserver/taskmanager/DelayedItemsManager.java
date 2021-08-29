package ru.privetdruk.l2jspace.gameserver.taskmanager;

import ru.privetdruk.l2jspace.common.pool.ConnectionPool;
import ru.privetdruk.l2jspace.common.pool.ThreadPool;
import ru.privetdruk.l2jspace.gameserver.model.World;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;

public class DelayedItemsManager implements Runnable {
    private static final Logger _log = Logger.getLogger(DelayedItemsManager.class.getName());

    private static final String SELECT = "SELECT * FROM items_delayed WHERE payment_status = 0";
    private static final String UPDATE = "UPDATE items_delayed SET payment_status = 1 WHERE payment_id = ?";

    private DelayedItemsManager() {
        ThreadPool.scheduleAtFixedRate(this, 60000L, 60000L);
    }

    @Override
    public void run() {
        try (Connection con = ConnectionPool.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(SELECT);
                 ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    Player player = World.getInstance().getPlayer(resultSet.getInt("owner_id"));
                    if (player == null)
                        continue;

                    if (player.addItem("", resultSet.getInt("item_id"), resultSet.getInt("count"), null, true) != null) {
                        try (PreparedStatement statementUpdate = con.prepareStatement(UPDATE)) {
                            statementUpdate.setInt(1, resultSet.getInt("payment_id"));
                            statementUpdate.execute();
                        }
                    }
                }
            }
        } catch (Exception e) {
            _log.warning("DelayedItemsManager: " + e);
        }
    }

    public static final DelayedItemsManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static final class SingletonHolder {
        protected static final DelayedItemsManager INSTANCE = new DelayedItemsManager();
    }
}