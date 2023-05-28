package net.noscape.project.supremetags.managers;

import net.noscape.project.supremetags.*;

import net.noscape.project.supremetags.handlers.Tag;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;

import java.util.*;

import static net.noscape.project.supremetags.utils.Utils.msgPlayer;

public class TagManager {

    private final Map<String, Tag> tags = new HashMap<>();
    private final Map<Integer, String> dataItem = new HashMap<>();

    private boolean isCost;

    private String reload = SupremeTags.getInstance().getConfig().getString("messages.reload");
    private String noperm = SupremeTags.getInstance().getConfig().getString("messages.no-permission");
    private String notags = SupremeTags.getInstance().getConfig().getString("messages.no-tags");
    private String commanddisabled = SupremeTags.getInstance().getConfig().getString("messages.tag-command-disabled");
    private String invalidtag = SupremeTags.getInstance().getConfig().getString("messages.invalid-tag");
    private String validtag = SupremeTags.getInstance().getConfig().getString("messages.valid-tag");
    private String invalidcategory = SupremeTags.getInstance().getConfig().getString("messages.invalid-category");

    public TagManager(boolean isCost) {
        this.sort();
        this.isCost = isCost;
    }

    public void createTag(Player player, String identifier, String tag_string, String description, String permission, double cost) {
        if (!tags.containsKey(identifier)) {

            String default_category = SupremeTags.getInstance().getConfig().getString("settings.default-category");

            Tag tag = new Tag(identifier, tag_string, default_category, permission, description, cost);
            tags.put(identifier, tag);

            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".tag", tag_string);
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".permission", permission);
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".description", description);
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".category", default_category);
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".display-item", "NAME_TAG");
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".displayname", "&7Tag: %tag%");
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".cost", cost);
            SupremeTags.getInstance().saveConfig();
            SupremeTags.getInstance().reloadConfig();

            msgPlayer(player, "&8[&6&lTAG&8] &7New tag created &6" + identifier + " &f- " + tag_string);
        } else {
            msgPlayer(player, validtag);
        }
    }

    public void createTag(CommandSender player, String identifier, String tag_string, String description, String permission, double cost) {
        if (!tags.containsKey(identifier)) {

            String default_category = SupremeTags.getInstance().getConfig().getString("settings.default-category");

            Tag tag = new Tag(identifier, tag_string, default_category, permission, description, cost);
            tags.put(identifier, tag);

            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".tag", tag_string);
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".permission", permission);
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".description", description);
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".category", default_category);
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".display-item", "NAME_TAG");
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".displayname", "&7Tag: %tag%");
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".cost", cost);
            SupremeTags.getInstance().saveConfig();
            SupremeTags.getInstance().reloadConfig();

            msgPlayer(player, "&8[&6&lTAG&8] &7New tag created &6" + identifier + " &f- " + tag_string);
        } else {
            msgPlayer(player, validtag);
        }
    }

    public void createTag(String identifier, String tag_string, String description, String permission, double cost) {
        if (!tags.containsKey(identifier)) {

            String default_category = SupremeTags.getInstance().getConfig().getString("settings.default-category");

            Tag tag = new Tag(identifier, tag_string, default_category, permission, description, cost);
            tags.put(identifier, tag);

            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".tag", tag_string);
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".permission", permission);
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".description", description);
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".category", default_category);
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".display-item", "NAME_TAG");
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".displayname", "&7Tag: %tag%");
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".cost", cost);
            SupremeTags.getInstance().saveConfig();
            SupremeTags.getInstance().reloadConfig();
        }
    }

    public void createTag(String identifier, String material, String tag_string, String description, String permission, double cost) {
        if (!tags.containsKey(identifier)) {

            String default_category = SupremeTags.getInstance().getConfig().getString("settings.default-category");

            Tag tag = new Tag(identifier, tag_string, default_category, permission, description, cost);
            tags.put(identifier, tag);

            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".tag", tag_string);
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".permission", permission);
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".description", description);
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".category", default_category);
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".display-item", material);
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".displayname", "&7Tag: %tag%");
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".cost", cost);
            SupremeTags.getInstance().saveConfig();
            SupremeTags.getInstance().reloadConfig();
        }
    }

    public void deleteTag(CommandSender player, String identifier) {
        if (tags.containsKey(identifier)) {
            tags.remove(identifier);

            try {
                SupremeTags.getInstance().getConfig().set("tags." + identifier, null);
                SupremeTags.getInstance().saveConfig();
                SupremeTags.getInstance().reloadConfig();
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
                SupremeTags.getInstance().getConfig().set("tags." + identifier, null);
                SupremeTags.getInstance().saveConfig();
                SupremeTags.getInstance().reloadConfig();
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
        for (String identifier : Objects.requireNonNull(SupremeTags.getInstance().getConfig().getConfigurationSection("tags")).getKeys(false)) {
            String tag = SupremeTags.getInstance().getConfig().getString("tags." + identifier + ".tag");
            String category = SupremeTags.getInstance().getConfig().getString("tags." + identifier + ".category");
            String description = SupremeTags.getInstance().getConfig().getString("tags." + identifier + ".description");

            String permission;

            if (SupremeTags.getInstance().getConfig().getString("tags." + identifier + ".permission") != null) {
                permission = SupremeTags.getInstance().getConfig().getString("tags." + identifier + ".permission");
            } else {
                permission = "none";
            }

            double cost = SupremeTags.getInstance().getConfig().getDouble("tags." + identifier + ".cost");

            Tag t = new Tag(identifier, tag, category, permission, description, cost);
            tags.put(identifier, t);

            count++;
        }

        Bukkit.getConsoleSender().sendMessage("[TAGS] loaded " + count + " tag(s) successfully.");
    }

    public Tag getTag(String tag) {
        Tag t = null;
        for (Tag tg : tags.values()) {
            if (tg.getIdentifier().equalsIgnoreCase(tag)) {
                t = tg;
            }
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
            t.setTag(tag);

            try {
                SupremeTags.getInstance().getConfig().set("tags." + identifier + ".tag", t.getTag());
                SupremeTags.getInstance().saveConfig();
                SupremeTags.getInstance().reloadConfig();
            } catch (Exception e) {
                e.printStackTrace();
            }

            msgPlayer(player, "&8[&6&lTAG&8] &6" + t.getIdentifier() + "'s tag &7changed to " + t.getTag());
        } else {
            msgPlayer(player, invalidtag);
        }
    }

    public void setCategory(Player player, String identifier, String category) {
        if (tags.containsKey(identifier)) {
            Tag t = tags.get(identifier);
            t.setCategory(category);

            try {
                SupremeTags.getInstance().getConfig().set("tags." + identifier + ".category", t.getCategory());
                SupremeTags.getInstance().saveConfig();
                SupremeTags.getInstance().reloadConfig();
            } catch (Exception e) {
                e.printStackTrace();
            }

            msgPlayer(player, "&8[&6&lTAG&8] &6" + t.getIdentifier() + "'s category &7changed to " + t.getCategory());
        } else {
            msgPlayer(player, invalidcategory);
        }
    }

    public void setTag(CommandSender player, String identifier, String tag) {
        if (tags.containsKey(identifier)) {
            Tag t = tags.get(identifier);
            t.setTag(tag);

            try {
                SupremeTags.getInstance().getConfig().set("tags." + identifier + ".tag", t.getTag());
                SupremeTags.getInstance().saveConfig();
                SupremeTags.getInstance().reloadConfig();
            } catch (Exception e) {
                e.printStackTrace();
            }

            msgPlayer(player, "&8[&6&lTAG&8] &6" + t.getIdentifier() + "'s tag &7changed to " + t.getTag());
        } else {
            msgPlayer(player, invalidtag);
        }
    }

    public void sort() {
        // Create a comparator that compares Map.Entry objects by their key in ascending order
        Comparator<Map.Entry<String, Tag>> comparator = Map.Entry.comparingByKey();

        // Create a sorted set with the comparator
        SortedSet<Map.Entry<String, Tag>> sortedSet = new TreeSet<>(comparator);

        // Add all the entries from the tags map to the sorted set
        sortedSet.addAll(tags.entrySet());
    }

    public boolean isCost() {
        return isCost;
    }

    public void setCost(boolean isCost) {
        this.isCost = isCost;
    }

    public void saveTag(Tag tag) {
        SupremeTags.getInstance().getConfig().set("tags." + tag.getIdentifier() + ".tag", tag.getTag());
        SupremeTags.getInstance().getConfig().set("tags." + tag.getIdentifier() + ".permission", tag.getPermission());
        SupremeTags.getInstance().getConfig().set("tags." + tag.getIdentifier() + ".description", tag.getDescription());
        SupremeTags.getInstance().getConfig().set("tags." + tag.getIdentifier() + ".category", tag.getCategory());
        SupremeTags.getInstance().getConfig().set("tags." + tag.getIdentifier() + ".cost", tag.getCost());
        SupremeTags.getInstance().saveConfig();
        SupremeTags.getInstance().reloadConfig();
    }
}
