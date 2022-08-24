package net.noscape.project.supremetags.storage;

import net.noscape.project.supremetags.*;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.util.*;

public class UserData {

    public static void createPlayer(Player player) {
        if (SupremeTags.getInstance().isH2()) {
            SupremeTags.getInstance().getUserData().createPlayer(player);
        } else if (SupremeTags.getInstance().isMySQL()) {
            SupremeTags.getInstance().getUser().createPlayer(player);
        }
    }


    public static void setActive(OfflinePlayer player, String identifier) {
        if (SupremeTags.getInstance().isH2()) {
            H2UserData.setActive(player, identifier);
        } else if (SupremeTags.getInstance().isMySQL()) {
            MySQLUserData.setActive(player, identifier);
        }
    }

    public static String getActive(UUID uuid) {
        if (SupremeTags.getInstance().isH2())
            return H2UserData.getActive(uuid);
        else if (SupremeTags.getInstance().isMySQL())
            return MySQLUserData.getActive(uuid);
        return "";
    }
}
