package net.noscape.project.supremetags.storage;

import net.noscape.project.supremetags.*;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.sql.*;

public class UserData {

    public boolean exists(Player player) {
        try {
            PreparedStatement statement = SupremeTags.getDatabase().getConnection().prepareStatement("SELECT * FROM `users` WHERE (UUID=?)");
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
        try {
            if (!exists(player)) {
                PreparedStatement statement = SupremeTags.getDatabase().getConnection().prepareStatement(
                        "INSERT INTO `users` " +
                                "(Name, UUID, Active) " +
                                "VALUES " +
                                "(?,?,?)");
                statement.setString(1, player.getName());
                statement.setString(2, player.getUniqueId().toString());
                statement.setString(3, SupremeTags.getInstance().getConfig().getString("settings.default-tag"));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setActive(OfflinePlayer player, String identifier) {
        try {
            PreparedStatement statement = SupremeTags.getDatabase().getConnection().prepareStatement("UPDATE `users` SET Active=? WHERE (UUID=?)");
            statement.setString(1, identifier);
            statement.setString(2, player.getUniqueId().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getActive(Player player) {
        try {
            PreparedStatement statement = SupremeTags.getDatabase().getConnection().prepareStatement("SELECT * FROM `users` WHERE (UUID=?)");
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();

            String value;
            if (resultSet.next()) {
                value = resultSet.getString("Active");
                return value;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

}
