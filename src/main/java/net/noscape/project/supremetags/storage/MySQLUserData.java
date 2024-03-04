package net.noscape.project.supremetags.storage;

import net.noscape.project.supremetags.*;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.sql.*;
import java.util.*;

public class MySQLUserData {

    public boolean exists(Player player) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = "SELECT * FROM `users` WHERE (UUID=?)";

        try {
            connection = SupremeTags.getMysql().getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, player.getUniqueId().toString());
            resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SupremeTags.getMysql().closeConnections(preparedStatement, connection, resultSet);
        }

        return false;
    }

    public void createPlayer(Player player) {
        if (exists(player)) {
            return;
        }

        String defaultTag = SupremeTags.getInstance().getConfig().getString("settings.default-tag");

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        String query = "INSERT INTO users (Name, UUID, Active) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE Name = ?, Active = ?";

        try {
            connection = SupremeTags.getMysql().getConnection();

            preparedStatement = connection.prepareStatement(query);

            // Set parameters
            preparedStatement.setString(1, player.getName());
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.setString(3, defaultTag);
            preparedStatement.setString(4, player.getName());
            preparedStatement.setString(5, defaultTag);

            // Execute the update
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                // No rows were affected, handle this scenario if needed
            }

            DataCache.removeFromCache(player.getUniqueId().toString());
            DataCache.cacheData(player.getUniqueId().toString(), defaultTag);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SupremeTags.getMysql().closeConnections(preparedStatement, connection, null); // No ResultSet to close
        }
    }

    public static void setActive(OfflinePlayer player, String identifier) {
        String cachedData = DataCache.getCachedData(player.getUniqueId().toString());

        if (cachedData != null && cachedData.equalsIgnoreCase(identifier)) {
            return;
        }

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        String query = "INSERT INTO users (Name, UUID, Active) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE Name = ?, Active = ?";

        try {
            connection = SupremeTags.getMysql().getConnection();

            preparedStatement = connection.prepareStatement(query);

            // Set parameters
            preparedStatement.setString(1, player.getName());
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.setString(3, identifier);
            preparedStatement.setString(4, player.getName());
            preparedStatement.setString(5, identifier);

            // Execute the update
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                // No rows were affected, handle this scenario if needed
            }

            DataCache.removeFromCache(player.getUniqueId().toString());
            DataCache.cacheData(player.getUniqueId().toString(), identifier);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SupremeTags.getMysql().closeConnections(preparedStatement, connection, null); // No ResultSet to close
        }
    }

    public static String getActive(UUID uuid) {
        String cachedData = DataCache.getCachedData(uuid.toString());

        if (cachedData != null) {
            return cachedData;
        }

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = "SELECT Active FROM users WHERE UUID=?";
        String value = "";

        try {
            connection = SupremeTags.getMysql().getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, uuid.toString());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                value = resultSet.getString("Active");

                DataCache.cacheData(uuid.toString(), value);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SupremeTags.getMysql().closeConnections(preparedStatement, connection, resultSet);
        }

        return value;
    }
}