package net.noscape.project.supremetags.storage;

import net.noscape.project.supremetags.*;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.sql.*;
import java.util.*;

public class MySQLUserData {

    public boolean exists(Player player) {
        try {
            PreparedStatement statement = SupremeTags.getMysql().getConnection().prepareStatement("SELECT * FROM `users` WHERE (UUID=?)");
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createPlayer(Player player) {
        if (exists(player)) {
            return;
        }

        String defaultTag = SupremeTags.getInstance().getConfig().getString("settings.default-tag");

        try (PreparedStatement statement = SupremeTags.getMysql().getConnection().prepareStatement(
                "INSERT INTO users (Name, UUID, Active) VALUES (?,?,?) ON DUPLICATE KEY UPDATE UUID = ?, Active = ?")) {
            statement.setString(1, player.getName());
            statement.setString(2, player.getUniqueId().toString());
            statement.setString(3, defaultTag);
            statement.setString(4, player.getUniqueId().toString()); // Update based on the same UUID
            statement.setString(5, defaultTag); // Update Active
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setActive(OfflinePlayer player, String identifier) {
        String sql = "INSERT INTO users (Name, UUID, Active) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE Name = ?, Active = ?";

        try (Connection connection = SupremeTags.getMysql().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, player.getName());
            statement.setString(2, player.getUniqueId().toString());
            statement.setString(3, identifier);
            statement.setString(4, player.getName());
            statement.setString(5, identifier);

            statement.executeUpdate();

            // Invalidate the cache for the updated data
            DataCache.removeFromCache(player.getUniqueId().toString());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getActive(UUID uuid) {
        // Check if data is in cache
        String cachedData = DataCache.getCachedData(uuid.toString());

        if (cachedData != null) {
            // Use cached data
            return cachedData;
        }

        // If not in cache, fetch from the database
        String query = "SELECT Active FROM users WHERE UUID=?";
        String value = "";

        try (Connection connection = SupremeTags.getMysql().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, uuid.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    value = resultSet.getString("Active");

                    // Cache the result for future use
                    DataCache.cacheData(uuid.toString(), value);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return value;
    }
}
