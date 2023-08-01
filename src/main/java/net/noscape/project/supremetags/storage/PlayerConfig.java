package net.noscape.project.supremetags.storage;

import net.md_5.bungee.api.ChatColor;
import net.noscape.project.supremetags.SupremeTagsPremium;
import net.noscape.project.supremetags.handlers.Tag;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerConfig {

    public static boolean exists(OfflinePlayer player) {
        return new File(SupremeTagsPremium.getInstance().getDataFolder(), "data/" + player.getUniqueId() + ".yml").exists();
    }

    public static FileConfiguration get(OfflinePlayer offlinePlayer) {
        File file = new File(SupremeTagsPremium.getInstance().getDataFolder(), "data/" + offlinePlayer.getUniqueId() + ".yml");
        return YamlConfiguration.loadConfiguration(file);
    }

    public static void saveConfigOnly(Player player) {
        File file = new File(SupremeTagsPremium.getInstance().getDataFolder(), "data/" + player.getUniqueId() + ".yml");
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save(Player player) {
        File file = new File(SupremeTagsPremium.getInstance().getDataFolder(), "data/" + player.getUniqueId() + ".yml");
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        // Retrieve the tags from the player manager
        List<Tag> tags = SupremeTagsPremium.getInstance().getPlayerManager().getPlayerTags().get(player.getUniqueId());

        if (tags != null && !tags.isEmpty()) {
            ConfigurationSection tagsSection = configuration.getConfigurationSection("tags");
            if (tagsSection == null) {
                tagsSection = configuration.createSection("tags");
            }

            for (Tag tag : tags) {
                ConfigurationSection tagSection = tagsSection.getConfigurationSection(tag.getIdentifier());
                if (tagSection == null) {
                    tagSection = tagsSection.createSection(tag.getIdentifier());
                }

                // Convert '§' color codes to '&' color codes before saving
                String tagString = ChatColor.translateAlternateColorCodes('§', tag.getTag().get(0));

                tagSection.set("tag", tagString); // Save the tag directly as a string, not a list
                tagSection.set("description", tag.getDescription());
            }

        }

        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void loadPlayer(OfflinePlayer player) {
        if (exists(player)) {
            File file = new File(SupremeTagsPremium.getInstance().getDataFolder(), "data/" + player.getUniqueId() + ".yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            List<Tag> tags = new ArrayList<>();

            for (String identifier : Objects.requireNonNull(config.getConfigurationSection("tags")).getKeys(false)) {
                String tag = config.getString("tags." + identifier + ".tag");
                String description = config.getString("tags." + identifier + ".description");

                List<String> tabList = new ArrayList<>();
                tabList.add(tag);

                Tag t = new Tag(identifier, tabList, "", "", description, 0, false);
                tags.add(t);
            }

            SupremeTagsPremium.getInstance().getPlayerManager().getPlayerTags().put(player.getUniqueId(), tags);
        } else {
            createFile(player);
            loadPlayer(player);
        }
    }

    void createFile(OfflinePlayer player) {
        File folder = new File(SupremeTagsPremium.getInstance().getDataFolder(), "data");
        File file = new File(SupremeTagsPremium.getInstance().getDataFolder(), "data/" + player.getUniqueId() + ".yml");

        if (!folder.exists()) {
            boolean created = folder.mkdir(); // or folder.mkdirs() if you want to create parent directories as well
            if (!created) {
                // Handle the case when the folder couldn't be created
                throw new RuntimeException("Failed to create the data folder.");
            }
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
        File file = new File(SupremeTagsPremium.getInstance().getDataFolder(), "data/" + player.getUniqueId() + ".yml");
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        configuration.set("tags", null);

        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}