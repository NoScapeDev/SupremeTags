package net.noscape.project.supremetags.storage;

import net.noscape.project.supremetags.*;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.util.*;

public class UserData {

    public static void createPlayer(Player player) {
        if (SupremeTagsPremium.getInstance().isH2()) {
            SupremeTagsPremium.getInstance().getUserData().createPlayer(player);
        } else if (SupremeTagsPremium.getInstance().isMySQL()) {
            SupremeTagsPremium.getInstance().getUser().createPlayer(player);
        } else if (SupremeTagsPremium.getInstance().isMaria()) {
            SupremeTagsPremium.getInstance().getMariaUser().createPlayer(player);
        } else if (SupremeTagsPremium.getInstance().isSQLite()) {
            SupremeTagsPremium.getInstance().getSQLiteUser().createPlayer(player);
        }
    }

    public static void setActive(OfflinePlayer player, String identifier) {
        if (SupremeTagsPremium.getInstance().isH2()) {
            H2UserData.setActive(player, identifier);
        } else if (SupremeTagsPremium.getInstance().isMySQL()) {
            MySQLUserData.setActive(player, identifier);
        } else if (SupremeTagsPremium.getInstance().isMaria()) {
            MariaUserData.setActive(player, identifier);
        } else if (SupremeTagsPremium.getInstance().isSQLite()) {
            SQLiteUserData.setActive(player, identifier);
        }
    }

    public static String getActive(UUID uuid) {

        if (SupremeTagsPremium.getInstance().isH2()) {
            return H2UserData.getActive(uuid);
        } else if (SupremeTagsPremium.getInstance().isMySQL()) {
            return MySQLUserData.getActive(uuid);
        } else if (SupremeTagsPremium.getInstance().isMaria()) {
            return MySQLUserData.getActive(uuid);
        } else if (SupremeTagsPremium.getInstance().isSQLite()) {
            return SQLiteUserData.getActive(uuid);
        }

        return "";
    }
}
