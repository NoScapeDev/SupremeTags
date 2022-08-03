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

    public void createTag(Player player, String identifier, String tag_string, String description, String permission) {
        if (!tags.containsKey(identifier)) {
            Tag tag = new Tag(identifier, tag_string);
            tags.put(identifier, tag);

            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".tag", tag_string);
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".permission", permission);
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".description", description);
            SupremeTags.getInstance().saveConfig();
            SupremeTags.getInstance().reloadConfig();

            msgPlayer(player, "&6[TAG] &7New tag created &6" + identifier + " &f- " + tag_string);
        } else {
            msgPlayer(player, "&c[&l!&c] &7This tag already exists.");
        }
    }

    public void createTag(CommandSender player, String identifier, String tag_string, String description, String permission) {
        if (!tags.containsKey(identifier)) {
            Tag tag = new Tag(identifier, tag_string);
            tags.put(identifier, tag);

            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".tag", tag_string);
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".permission", permission);
            SupremeTags.getInstance().getConfig().set("tags." + identifier + ".description", description);
            SupremeTags.getInstance().saveConfig();
            SupremeTags.getInstance().reloadConfig();

            msgPlayer(player, "&6[TAG] &7New tag created &6" + identifier + " &f- " + tag_string);
        } else {
            msgPlayer(player, "&c[&l!&c] &7This tag already exists.");
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

            msgPlayer(player, "&6[TAG] &7Tag &6" + identifier + " &7is now deleted!");
        } else {
            msgPlayer(player, "&c[&l!&c] &7This tag doesn't exist!");
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

            msgPlayer(player, "&6[TAG] &7Tag &6" + identifier + " &7is now deleted!");
        } else {
            msgPlayer(player, "&c[&l!&c] &7This tag doesn't exist!");
        }
    }

    public void loadTags() {
        int count = 0;
        for (String identifier : Objects.requireNonNull(SupremeTags.getInstance().getConfig().getConfigurationSection("tags")).getKeys(false)) {
            String tag = SupremeTags.getInstance().getConfig().getString("tags." + identifier + ".tag");

            Tag t = new Tag(identifier, tag);
            tags.put(identifier, t);

            count++;
        }

        Bukkit.getConsoleSender().sendMessage("[TAGS] loaded " + count + " tag(s) successfully.");
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

            msgPlayer(player, "&6[TAG] &6" + t.getIdentifier() + "'s tag &7changed to " + t.getTag());
        } else {
            msgPlayer(player, "&6[TAG] &7This tag doesn't exist!");
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

            msgPlayer(player, "&6[TAG] &6" + t.getIdentifier() + "'s tag &7changed to " + t.getTag());
        } else {
            msgPlayer(player, "&6[TAG] &7This tag doesn't exist!");
        }
    }

}
