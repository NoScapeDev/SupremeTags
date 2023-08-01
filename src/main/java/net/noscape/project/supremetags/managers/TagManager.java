package net.noscape.project.supremetags.managers;

import com.google.common.base.Charsets;
import net.noscape.project.supremetags.*;

import net.noscape.project.supremetags.handlers.Tag;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static net.noscape.project.supremetags.utils.Utils.msgPlayer;

public class TagManager {

    private final Map<String, Tag> tags = new HashMap<>();
    private final Map<Integer, String> dataItem = new HashMap<>();

    private boolean isCost;

    private File tagFile;
    private FileConfiguration tagConfig;

    private final String invalidtag = SupremeTagsPremium.getInstance().getConfig().getString("messages.invalid-tag");
    private final String validtag = SupremeTagsPremium.getInstance().getConfig().getString("messages.valid-tag");
    private final String invalidcategory = SupremeTagsPremium.getInstance().getConfig().getString("messages.invalid-category");

    public TagManager(boolean isCost) {
        this.isCost = isCost;
    }

    public void createTag(Player player, String identifier, String tag_string, String description, String permission, double cost) {
        if (!tags.containsKey(identifier)) {

            String default_category = SupremeTagsPremium.getInstance().getConfig().getString("settings.default-category");

            List<String> tagList = new ArrayList<>();
            tagList.add(tag_string);

            int orderID = tags.size() + 1;

            Tag tag = new Tag(identifier, tagList, default_category, permission, description, cost, orderID, true);
            tags.put(identifier, tag);

            List<String> unlocked_lore = new ArrayList<>();
            unlocked_lore.add("&7&m-----------------------------");
            unlocked_lore.add("");
            unlocked_lore.add("&bIdentifier: &7%identifier%");
            unlocked_lore.add("");
            unlocked_lore.add("&dDescription:");
            unlocked_lore.add("&7%description%");
            unlocked_lore.add("");
            unlocked_lore.add("&eClick to Assign!");
            unlocked_lore.add("&7&m-----------------------------");

            List<String> locked_lore = new ArrayList<>();
            unlocked_lore.add("&c&m-----------------------------");
            unlocked_lore.add("");
            unlocked_lore.add("&7Buy &8&l> &fRight-Click");
            unlocked_lore.add("&7Cost &8&l> &f$%cost%");
            unlocked_lore.add("");
            unlocked_lore.add("&cThis tag is locked!");
            unlocked_lore.add("&c&m-----------------------------");

            List<String> locked_permission = new ArrayList<>();
            unlocked_lore.add("&c&m-----------------------------");
            unlocked_lore.add("");
            unlocked_lore.add("&7No permission to select this tag!");
            unlocked_lore.add("");
            unlocked_lore.add("&cThis tag is locked!");
            unlocked_lore.add("&c&m-----------------------------");

            getTagConfig().set("tags." + identifier + ".tag", tag_string);
            getTagConfig().set("tags." + identifier + ".permission", permission);
            getTagConfig().set("tags." + identifier + ".description", description);
            getTagConfig().set("tags." + identifier + ".category", default_category);
            getTagConfig().set("tags." + identifier + ".order", orderID);
            getTagConfig().set("tags." + identifier + ".withdrawable", true);
            getTagConfig().set("tags." + identifier + ".displayname", "&7Tag: %tag%");
            getTagConfig().set("tags." + identifier + ".custom-model-data", 0);
            getTagConfig().set("tags." + identifier + ".display-item", "NAME_TAG");
            getTagConfig().set("tags." + identifier + ".cost", cost);
            getTagConfig().set("tags." + identifier + ".unlocked-lore", unlocked_lore);
            getTagConfig().set("tags." + identifier + ".locked-lore", locked_lore);
            getTagConfig().set("tags." + identifier + ".locked-permission", locked_permission);
            saveTagConfig();

            msgPlayer(player, "&8[&6&lTAG&8] &7New tag created &6" + identifier + " &f- " + tag_string);
        } else {
            msgPlayer(player, validtag);
        }
    }

    public void createTag(CommandSender player, String identifier, String tag_string, String description, String permission, double cost) {
        if (!tags.containsKey(identifier)) {

            String default_category = SupremeTagsPremium.getInstance().getConfig().getString("settings.default-category");

            List<String> tagList = new ArrayList<>();
            tagList.add(tag_string);

            int orderID = tags.size() + 1;

            Tag tag = new Tag(identifier, tagList, default_category, permission, description, cost, orderID, true);
            tags.put(identifier, tag);

            List<String> unlocked_lore = new ArrayList<>();
            unlocked_lore.add("&7&m-----------------------------");
            unlocked_lore.add("");
            unlocked_lore.add("&bIdentifier: &7%identifier%");
            unlocked_lore.add("");
            unlocked_lore.add("&dDescription:");
            unlocked_lore.add("&7%description%");
            unlocked_lore.add("");
            unlocked_lore.add("&eClick to Assign!");
            unlocked_lore.add("&7&m-----------------------------");

            List<String> locked_lore = new ArrayList<>();
            unlocked_lore.add("&c&m-----------------------------");
            unlocked_lore.add("");
            unlocked_lore.add("&7Buy &8&l> &fRight-Click");
            unlocked_lore.add("&7Cost &8&l> &f$%cost%");
            unlocked_lore.add("");
            unlocked_lore.add("&cThis tag is locked!");
            unlocked_lore.add("&c&m-----------------------------");

            List<String> locked_permission = new ArrayList<>();
            unlocked_lore.add("&c&m-----------------------------");
            unlocked_lore.add("");
            unlocked_lore.add("&7No permission to select this tag!");
            unlocked_lore.add("");
            unlocked_lore.add("&cThis tag is locked!");
            unlocked_lore.add("&c&m-----------------------------");

            getTagConfig().set("tags." + identifier + ".tag", tag_string);
            getTagConfig().set("tags." + identifier + ".permission", permission);
            getTagConfig().set("tags." + identifier + ".description", description);
            getTagConfig().set("tags." + identifier + ".category", default_category);
            getTagConfig().set("tags." + identifier + ".order", orderID);
            getTagConfig().set("tags." + identifier + ".withdrawable", true);
            getTagConfig().set("tags." + identifier + ".displayname", "&7Tag: %tag%");
            getTagConfig().set("tags." + identifier + ".custom-model-data", 0);
            getTagConfig().set("tags." + identifier + ".display-item", "NAME_TAG");
            getTagConfig().set("tags." + identifier + ".cost", cost);
            getTagConfig().set("tags." + identifier + ".unlocked-lore", unlocked_lore);
            getTagConfig().set("tags." + identifier + ".locked-lore", locked_lore);
            getTagConfig().set("tags." + identifier + ".locked-permission", locked_permission);
            saveTagConfig();

            msgPlayer(player, "&8[&6&lTAG&8] &7New tag created &6" + identifier + " &f- " + tag_string);
        } else {
            msgPlayer(player, validtag);
        }
    }

    public void createTag(String identifier, String tag_string, String description, String permission, double cost) {
        if (!tags.containsKey(identifier)) {

            String default_category = SupremeTagsPremium.getInstance().getConfig().getString("settings.default-category");

            List<String> tagList = new ArrayList<>();
            tagList.add(tag_string);

            int orderID = tags.size() + 1;

            Tag tag = new Tag(identifier, tagList, default_category, permission, description, cost, orderID, true);
            tags.put(identifier, tag);

            List<String> unlocked_lore = new ArrayList<>();
            unlocked_lore.add("&7&m-----------------------------");
            unlocked_lore.add("");
            unlocked_lore.add("&bIdentifier: &7%identifier%");
            unlocked_lore.add("");
            unlocked_lore.add("&dDescription:");
            unlocked_lore.add("&7%description%");
            unlocked_lore.add("");
            unlocked_lore.add("&eClick to Assign!");
            unlocked_lore.add("&7&m-----------------------------");

            List<String> locked_lore = new ArrayList<>();
            unlocked_lore.add("&c&m-----------------------------");
            unlocked_lore.add("");
            unlocked_lore.add("&7Buy &8&l> &fRight-Click");
            unlocked_lore.add("&7Cost &8&l> &f$%cost%");
            unlocked_lore.add("");
            unlocked_lore.add("&cThis tag is locked!");
            unlocked_lore.add("&c&m-----------------------------");

            List<String> locked_permission = new ArrayList<>();
            unlocked_lore.add("&c&m-----------------------------");
            unlocked_lore.add("");
            unlocked_lore.add("&7No permission to select this tag!");
            unlocked_lore.add("");
            unlocked_lore.add("&cThis tag is locked!");
            unlocked_lore.add("&c&m-----------------------------");

            getTagConfig().set("tags." + identifier + ".tag", tag_string);
            getTagConfig().set("tags." + identifier + ".permission", permission);
            getTagConfig().set("tags." + identifier + ".description", description);
            getTagConfig().set("tags." + identifier + ".category", default_category);
            getTagConfig().set("tags." + identifier + ".order", orderID);
            getTagConfig().set("tags." + identifier + ".withdrawable", true);
            getTagConfig().set("tags." + identifier + ".displayname", "&7Tag: %tag%");
            getTagConfig().set("tags." + identifier + ".custom-model-data", 0);
            getTagConfig().set("tags." + identifier + ".display-item", "NAME_TAG");
            getTagConfig().set("tags." + identifier + ".cost", cost);
            getTagConfig().set("tags." + identifier + ".unlocked-lore", unlocked_lore);
            getTagConfig().set("tags." + identifier + ".locked-lore", locked_lore);
            getTagConfig().set("tags." + identifier + ".locked-permission", locked_permission);
            saveTagConfig();
        }
    }

    public void createTag(String identifier, String material, String tag_string, String description, String permission, double cost) {
        if (!tags.containsKey(identifier)) {

            String default_category = SupremeTagsPremium.getInstance().getConfig().getString("settings.default-category");

            List<String> tagList = new ArrayList<>();
            tagList.add(tag_string);

            int orderID = tags.size() + 1;

            Tag tag = new Tag(identifier, tagList, default_category, permission, description, cost, orderID, true);
            tags.put(identifier, tag);

            List<String> unlocked_lore = new ArrayList<>();
            unlocked_lore.add("&7&m-----------------------------");
            unlocked_lore.add("");
            unlocked_lore.add("&bIdentifier: &7%identifier%");
            unlocked_lore.add("");
            unlocked_lore.add("&dDescription:");
            unlocked_lore.add("&7%description%");
            unlocked_lore.add("");
            unlocked_lore.add("&eClick to Assign!");
            unlocked_lore.add("&7&m-----------------------------");

            List<String> locked_lore = new ArrayList<>();
            unlocked_lore.add("&c&m-----------------------------");
            unlocked_lore.add("");
            unlocked_lore.add("&7Buy &8&l> &fRight-Click");
            unlocked_lore.add("&7Cost &8&l> &f$%cost%");
            unlocked_lore.add("");
            unlocked_lore.add("&cThis tag is locked!");
            unlocked_lore.add("&c&m-----------------------------");

            List<String> locked_permission = new ArrayList<>();
            unlocked_lore.add("&c&m-----------------------------");
            unlocked_lore.add("");
            unlocked_lore.add("&7No permission to select this tag!");
            unlocked_lore.add("");
            unlocked_lore.add("&cThis tag is locked!");
            unlocked_lore.add("&c&m-----------------------------");

            getTagConfig().set("tags." + identifier + ".tag", tag_string);
            getTagConfig().set("tags." + identifier + ".permission", permission);
            getTagConfig().set("tags." + identifier + ".description", description);
            getTagConfig().set("tags." + identifier + ".category", default_category);
            getTagConfig().set("tags." + identifier + ".order", orderID);
            getTagConfig().set("tags." + identifier + ".withdrawable", true);
            getTagConfig().set("tags." + identifier + ".displayname", "&7Tag: %tag%");
            getTagConfig().set("tags." + identifier + ".custom-model-data", 0);
            getTagConfig().set("tags." + identifier + ".display-item", material);
            getTagConfig().set("tags." + identifier + ".cost", cost);
            getTagConfig().set("tags." + identifier + ".unlocked-lore", unlocked_lore);
            getTagConfig().set("tags." + identifier + ".locked-lore", locked_lore);
            getTagConfig().set("tags." + identifier + ".locked-permission", locked_permission);
            saveTagConfig();
        }
    }

    public void deleteTag(CommandSender player, String identifier) {
        if (tags.containsKey(identifier)) {
            tags.remove(identifier);

            try {
                getTagConfig().set("tags." + identifier, null);
                saveTagConfig();
            } catch (Exception e) {
                e.printStackTrace();
            }

            msgPlayer(player, "&8[&6&lTAG&8] &7Tag &6" + identifier + " &7is now deleted!");
        } else {
            msgPlayer(player, invalidtag);
        }
    }

    public void deleteTag(Player player, String identifier) {
        if (tags.containsKey(identifier)) {
            tags.remove(identifier);

            try {
                getTagConfig().set("tags." + identifier, null);
                saveTagConfig();
            } catch (Exception e) {
                e.printStackTrace();
            }

            msgPlayer(player, "&8[&6&lTAG&8] &7Tag &6" + identifier + " &7is now deleted!");
        } else {
            msgPlayer(player, invalidtag);
        }
    }

    public void loadTags() {
        int count = 0;
        for (String identifier : Objects.requireNonNull(getTagConfig().getConfigurationSection("tags")).getKeys(false)) {
            List<String> tag = getTagConfig().getStringList("tags." + identifier + ".tag");
            String category = getTagConfig().getString("tags." + identifier + ".category");
            String description = getTagConfig().getString("tags." + identifier + ".description");

            String permission;

            if (getTagConfig().getString("tags." + identifier + ".permission") != null) {
                permission = getTagConfig().getString("tags." + identifier + ".permission");
            } else {
                permission = "none";
            }

            int orderID = getTagConfig().getInt("tags." + identifier + ".order");

            double cost = getTagConfig().getDouble("tags." + identifier + ".cost");

            boolean withdrawable = getTagConfig().getBoolean("tags." + identifier + ".withdrawable");

            Tag t = new Tag(identifier, tag, category, permission, description, cost, orderID, withdrawable);
            tags.put(identifier, t);
            count++;
        }

        if (getTags().size() != 0) {
            for (Tag t : getTags().values()) {
                if (t.getTag().size() > 1) {
                    t.startAnimation();
                }
            }
        }

        Bukkit.getConsoleSender().sendMessage("[TAGS] loaded " + count + " tag(s) successfully.");
    }

    public Tag getTag(String identifier) {
        Tag t = null;

        if (tags.containsKey(identifier)) {
            t = tags.get(identifier);
        }

        return t;
    }

    public void unloadTags() {
        if (!tags.isEmpty()) {
            tags.clear();
        }
    }

    public Map<String, Tag> getTags() {
        return tags;
    }
    public Map<Integer, String> getDataItem() { return dataItem; }

    public void setTag(Player player, String identifier, String tag) {
        if (tags.containsKey(identifier)) {
            Tag t = tags.get(identifier);
            List<String> tagsList = t.getTag();
            tagsList.add(tag);
            t.setTag(tagsList);

            try {
                SupremeTagsPremium.getInstance().getConfig().set("tags." + identifier + ".tag", tagsList);
                SupremeTagsPremium.getInstance().saveConfig();
                SupremeTagsPremium.getInstance().reloadConfig();
            } catch (Exception e) {
                e.printStackTrace();
            }

            msgPlayer(player, "&8[&6&lTAG&8] &6" + t.getIdentifier() + "'s tag &7changed to " + t.getCurrentTag());
        } else {
            msgPlayer(player, invalidtag);
        }
    }

    public void setCategory(Player player, String identifier, String category) {
        if (SupremeTagsPremium.getInstance().getTagManager().getTag(identifier) == null) {
            msgPlayer(player, invalidtag);
            return;
        }

        if (!SupremeTagsPremium.getInstance().getCategoryManager().getCatorgies().contains(category)) {
            msgPlayer(player, invalidcategory);
            return;
        }

        Tag t = tags.get(identifier);
        t.setCategory(category);

        try {
            SupremeTagsPremium.getInstance().getTagManager().getTagConfig().set("tags." + identifier + ".category", t.getCategory());
            SupremeTagsPremium.getInstance().getTagManager().saveTagConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }

        msgPlayer(player, "&8[&6&lTAG&8] &6" + t.getIdentifier() + "'s category &7changed to " + t.getCategory());
    }

    public void setTag(CommandSender player, String identifier, String tag) {
        if (tags.containsKey(identifier)) {
            Tag t = tags.get(identifier);
            List<String> tagsList = t.getTag();
            tagsList.add(tag);
            t.setTag(tagsList);

            try {
                SupremeTagsPremium.getInstance().getConfig().set("tags." + identifier + ".tag", t.getCurrentTag());
                SupremeTagsPremium.getInstance().saveConfig();
                SupremeTagsPremium.getInstance().reloadConfig();
            } catch (Exception e) {
                e.printStackTrace();
            }

            msgPlayer(player, "&8[&6&lTAG&8] &6" + t.getIdentifier() + "'s tag &7changed to " + t.getCurrentTag());
        } else {
            msgPlayer(player, invalidtag);
        }
    }

    public boolean isCost() {
        return isCost;
    }

    public void setCost(boolean isCost) {
        this.isCost = isCost;
    }

    public void saveTag(Tag tag) {
        getTagConfig().set("tags." + tag.getIdentifier() + ".tag", tag.getTag());
        getTagConfig().set("tags." + tag.getIdentifier() + ".permission", tag.getPermission());
        getTagConfig().set("tags." + tag.getIdentifier() + ".description", tag.getDescription());
        getTagConfig().set("tags." + tag.getIdentifier() + ".category", tag.getCategory());
        getTagConfig().set("tags." + tag.getIdentifier() + ".cost", tag.getCost());
        saveTagConfig();
    }

    public void saveTagConfig() {
        if (tagFile != null && tagConfig != null) {
            this.tagConfig = YamlConfiguration.loadConfiguration(this.tagFile);
            InputStream defConfigStream = SupremeTagsPremium.getInstance().getResource("tags.yml");
            if (defConfigStream != null) {
                this.tagConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
            }
        } else {
            loadFile();
            saveTagConfig();
        }
    }

    public FileConfiguration getTagConfig() {
        return tagConfig;
    }

    public void loadFile() {
        this.tagFile = new File(SupremeTagsPremium.getInstance().getDataFolder(), "tags.yml");

        if (!tagFile.exists()) {
            SupremeTagsPremium.getInstance().saveResource("tags.yml", true);
        }

        this.tagConfig = YamlConfiguration.loadConfiguration(tagFile);
    }
}