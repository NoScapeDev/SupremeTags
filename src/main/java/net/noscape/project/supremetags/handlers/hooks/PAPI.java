package net.noscape.project.supremetags.handlers.hooks;

import me.clip.placeholderapi.expansion.*;
import net.noscape.project.supremetags.*;
import net.noscape.project.supremetags.handlers.Tag;
import net.noscape.project.supremetags.storage.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.*;

import java.util.*;

import static net.noscape.project.supremetags.utils.Utils.msgPlayer;
import static net.noscape.project.supremetags.utils.Utils.replacePlaceholders;

public class PAPI extends PlaceholderExpansion {

    private final Map<String, Tag> tags;

    public PAPI(SupremeTags plugin) {
        tags = plugin.getTagManager().getTags();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "supremetags";
    }

    @Override
    public @NotNull String getAuthor() {
        return "DevScape";
    }

    @Override
    public @NotNull String getVersion() {
        return SupremeTags.getInstance().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        String text = "";

        if (params.equalsIgnoreCase("hastag_selected")) {
            // %supremetags_hastag_selected%
            if (!UserData.getActive(player.getUniqueId()).equalsIgnoreCase("None")) {
                text = String.valueOf(true);
            } else if (UserData.getActive(player.getUniqueId()).equalsIgnoreCase("None")) {
                text = String.valueOf(false);
            }
        } else if (params.equalsIgnoreCase("hastag_tags")) {
            // %supremetags_hastag_tags%
            if (hasTags(player.getPlayer())) {
                text = String.valueOf(true);
            } else if (!hasTags(player.getPlayer())) {
                text = String.valueOf(false);
            }
        } else {
            if (tags.get(UserData.getActive(player.getUniqueId())) != null) {
                Tag t = tags.get(UserData.getActive(player.getUniqueId()));

                if (params.equalsIgnoreCase("tag")) {
                    replacePlaceholders(player.getPlayer(), t.getTag());
                    text = t.getTag();
                } else if (params.equalsIgnoreCase("identifier")) {
                    text = t.getIdentifier();
                } else if (params.equalsIgnoreCase("description")) {
                    text = t.getDescription();
                } else if (params.equalsIgnoreCase("permission")) {
                    text = t.getPermission();
                } else if (params.equalsIgnoreCase("category")) {
                    text = t.getCategory();
                } else if (params.equalsIgnoreCase("cost")) {
                    text = String.valueOf(t.getCost());
                }
            } else {
                text = SupremeTags.getInstance().getConfig().getString("settings.none-output");
            }
        }

        return text;
    }

    public boolean hasTags(Player player) {
        for (Tag tag : SupremeTags.getInstance().getTagManager().getTags().values()) {
            if (player.hasPermission(tag.getPermission())) {
                return true;
            }
        }
        return false;
    }


}