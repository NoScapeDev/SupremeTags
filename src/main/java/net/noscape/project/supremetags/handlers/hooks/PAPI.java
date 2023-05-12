package net.noscape.project.supremetags.handlers.hooks;

import me.clip.placeholderapi.expansion.*;
import net.noscape.project.supremetags.*;
import net.noscape.project.supremetags.handlers.Tag;
import net.noscape.project.supremetags.storage.*;
import org.bukkit.*;
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

        if (tags.get(UserData.getActive(player.getUniqueId())) != null) {
            Tag t = tags.get(UserData.getActive(player.getUniqueId()));

            if (params.equalsIgnoreCase("tag")) {
                replacePlaceholders(player.getPlayer(), t.getTag());
                return t.getTag();
            } else if (params.equalsIgnoreCase("identifier")) {
                return t.getIdentifier();
            } else if (params.equalsIgnoreCase("description")) {
                return t.getDescription();
            } else if (params.equalsIgnoreCase("permission")) {
                return t.getPermission();
            } else if (params.equalsIgnoreCase("category")) {
                return t.getCategory();
            } else if (params.equalsIgnoreCase("cost")) {
                return String.valueOf(t.getCost());
            }
        } else {
            return "&6";
        }
        return text;
    }
}