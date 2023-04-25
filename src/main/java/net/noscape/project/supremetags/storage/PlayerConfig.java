package net.noscape.project.supremetags.storage;

import net.noscape.project.supremetags.SupremeTags;
import net.noscape.project.supremetags.handlers.Tag;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class PlayerConfig {

    public boolean exists(OfflinePlayer player) {
        return new File(SupremeTags.getInstance().getDataFolder(), "data/" + player.getUniqueId() + ".yml").exists();
    }

    public static FileConfiguration get(OfflinePlayer offlinePlayer) {
        File file = new File(SupremeTags.getInstance().getDataFolder(), "data/" + offlinePlayer.getUniqueId() + ".yml");
        return YamlConfiguration.loadConfiguration(file);
    }

    public void save(Player player) {
        if (exists(player)) {
            File file = new File(SupremeTags.getInstance().getDataFolder(), "data/" + player.getUniqueId() + ".yml");
            FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);

            try {
                configuration.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadPlayer(OfflinePlayer player) {
        if (exists(player)) {
            File file = new File(SupremeTags.getInstance().getDataFolder(), "data/" + player.getUniqueId() + ".yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            for (String identifier : config.getConfigurationSection("tags").getKeys(false)) {
                String tag = config.getString("tags." + identifier + ".tag");
                String description = config.getString("tags." + identifier + ".description");

                Tag t = new Tag(identifier, tag, "", "", description, 0);
                SupremeTags.getInstance().getPlayerManager().getPlayerTags().put(player.getUniqueId(), t);
            }
        } else {
            createFile(player);
        }
    }

    void createFile(OfflinePlayer player) {
        File folder = new File(SupremeTags.getInstance().getDataFolder(), "data");
        File file = new File(SupremeTags.getInstance().getDataFolder(), "data/" + player.getUniqueId() + ".yml");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        if (!file.exists()) {
            FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            configuration.createSection("tags");

            try {
                configuration.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void reset(OfflinePlayer player) {
        File file = new File(SupremeTags.getInstance().getDataFolder(), "data/" + player.getUniqueId() + ".yml");
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        configuration.set("tags", null);

        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}