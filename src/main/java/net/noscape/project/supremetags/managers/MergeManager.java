package net.noscape.project.supremetags.managers;

import net.noscape.project.supremetags.SupremeTags;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.logging.Logger;

import static net.noscape.project.supremetags.utils.Utils.msgPlayer;

public class MergeManager {

    /*
     * SUPPORT TAG PLUGINS:
     * - DeluxeTags
     * - ExternalTags
     */

    public void merge(Logger log) {
        File deluxetagsFile = new File(Bukkit.getServer().getWorldContainer().getAbsolutePath() + "/plugins/DeluxeTags/config.yml"); // First we will load the file.
        FileConfiguration deluxetagsConfig = YamlConfiguration.loadConfiguration(deluxetagsFile); // Now we will load the file into a FileConfiguration.

        File eternaltagsFile = new File(Bukkit.getServer().getWorldContainer().getAbsolutePath() + "/plugins/EternalTags/tags.yml"); // First we will load the file.
        FileConfiguration eternaltagsConfig = YamlConfiguration.loadConfiguration(deluxetagsFile); // Now we will load the file into a FileConfiguration.

        if (SupremeTags.getInstance().getConfig().getBoolean("settings.auto-merge")) {
            ConfigurationSection eternaltagsSection = eternaltagsConfig.getConfigurationSection("tags");
            ConfigurationSection deluxeTagsSection = deluxetagsConfig.getConfigurationSection("deluxetags");

            if (deluxeTagsSection == null && eternaltagsSection == null) {
                log.warning("Merger: Supremetags only supports DeluxeTags & EternalTags to be merged.");
                return;
            }

            if (eternaltagsSection != null) {
                for (String identifier : eternaltagsSection.getKeys(false)) {
                    if (!SupremeTags.getInstance().getTagManager().getTags().containsKey(identifier)) {
                        String tag = eternaltagsConfig.getString(String.format("tags.%s.tag", identifier));
                        String description = eternaltagsConfig.getString(String.format("tags.%s.description", identifier));
                        String permission = eternaltagsConfig.getString(String.format("tags.%s.permission", identifier));
                        String material = eternaltagsConfig.getString(String.format("tags.%s.icon.material", identifier));

                        SupremeTags.getInstance().getTagManager().createTag(identifier, material, tag, description, permission, 100);
                    }
                }
                log.info("Merger: Added all new tags from EternalTags, any existing tags with the same name were not added.");
            }

            if (deluxeTagsSection != null) {
                for (String identifier : deluxeTagsSection.getKeys(false)) {
                    if (!SupremeTags.getInstance().getTagManager().getTags().containsKey(identifier)) {
                        String tag = deluxetagsConfig.getString(String.format("deluxetags.%s.tag", identifier));
                        String description = deluxetagsConfig.getString(String.format("deluxetags.%s.description", identifier));
                        String permission = deluxetagsConfig.getString(String.format("deluxetags.%s.permission", identifier));

                        SupremeTags.getInstance().getTagManager().createTag(identifier, tag, description, permission, 100);
                    }
                }
                log.info("Merger: Added all new tags from DeluxeTags, any existing tags with the same name were not added.");
            }
        }
    }

    public void merge(Player player) {
        File deluxetagsFile = new File(Bukkit.getServer().getWorldContainer().getAbsolutePath() + "/plugins/DeluxeTags/config.yml"); // First we will load the file.
        FileConfiguration deluxetagsConfig = YamlConfiguration.loadConfiguration(deluxetagsFile); // Now we will load the file into a FileConfiguration.

        File eternaltagsFile = new File(Bukkit.getServer().getWorldContainer().getAbsolutePath() + "/plugins/EternalTags/tags.yml"); // First we will load the file.
        FileConfiguration eternaltagsConfig = YamlConfiguration.loadConfiguration(deluxetagsFile); // Now we will load the file into a FileConfiguration.

        if (SupremeTags.getInstance().getConfig().getBoolean("settings.auto-merge")) {
            ConfigurationSection eternaltagsSection = eternaltagsConfig.getConfigurationSection("tags");
            ConfigurationSection deluxeTagsSection = deluxetagsConfig.getConfigurationSection("deluxetags");

            if (deluxeTagsSection == null && eternaltagsSection == null) {
                msgPlayer(player, "&6Merger: &7Supremetags only supports DeluxeTags & EternalTags to be merged.");
                return;
            }

            if (eternaltagsSection != null) {
                for (String identifier : eternaltagsSection.getKeys(false)) {
                    if (!SupremeTags.getInstance().getTagManager().getTags().containsKey(identifier)) {
                        String tag = eternaltagsConfig.getString(String.format("tags.%s.tag", identifier));
                        String description = eternaltagsConfig.getString(String.format("tags.%s.description", identifier));
                        String permission = eternaltagsConfig.getString(String.format("tags.%s.permission", identifier));
                        String material = eternaltagsConfig.getString(String.format("tags.%s.icon.material", identifier));

                        SupremeTags.getInstance().getTagManager().createTag(identifier, material, tag, description, permission, 100);
                    }
                }
                msgPlayer(player, "&6Merger: &7Added all new tags from &6EternalTags&7 were added, any existing tags with the same name won't be added.");
            }

            if (deluxeTagsSection != null) {
                for (String identifier : deluxeTagsSection.getKeys(false)) {
                    if (!SupremeTags.getInstance().getTagManager().getTags().containsKey(identifier)) {
                        String tag = deluxetagsConfig.getString(String.format("deluxetags.%s.tag", identifier));
                        String description = deluxetagsConfig.getString(String.format("deluxetags.%s.description", identifier));
                        String permission = deluxetagsConfig.getString(String.format("deluxetags.%s.permission", identifier));

                        SupremeTags.getInstance().getTagManager().createTag(identifier, tag, description, permission, 100);
                    }
                }
                msgPlayer(player, "&6Merger: &7Added all new tags from &6DeluxeTags&7 were added, any existing tags with the same name won't be added.");
            }
        }
    }
}
