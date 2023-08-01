package net.noscape.project.supremetags.storage;

import net.noscape.project.supremetags.SupremeTagsPremium;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLiteUserData {

    public boolean exists(Player player) {
        try (Connection connection = SupremeTagsPremium.getSQLite().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM `users` WHERE (UUID=?)")) {
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createPlayer(Player player) {
        if (exists(player)) {
            return;
        }

        String defaultTag = SupremeTagsPremium.getInstance().getConfig().getString("settings.default-tag");

        try (Connection connection = SupremeTagsPremium.getSQLite().getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO `users` (Name, UUID, Active) VALUES (?,?,?)")) {
            statement.setString(1, player.getName());
            statement.setString(2, player.getUniqueId().toString());
            statement.setString(3, defaultTag);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setActive(OfflinePlayer player, String identifier) {
        String sql = "UPDATE `users` SET Active=? WHERE (UUID=?)";
        try (Connection connection = SupremeTagsPremium.getSQLite().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, identifier);
            statement.setString(2, player.getUniqueId().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static String getActive(UUID uuid) {
        String value = "";
        String query = "SELECT * FROM `users` WHERE (UUID=?)";
        try (Connection connection = SupremeTagsPremium.getSQLite().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, uuid.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    value = resultSet.getString("Active");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return value;
    }
}
