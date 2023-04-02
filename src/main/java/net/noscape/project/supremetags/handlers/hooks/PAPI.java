package net.noscape.project.supremetags.handlers.hooks;

import me.clip.placeholderapi.expansion.*;
import net.noscape.project.supremetags.*;
import net.noscape.project.supremetags.handlers.Tag;
import net.noscape.project.supremetags.storage.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

import java.util.*;

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
        if (!UserData.getActive(player.getUniqueId()).equalsIgnoreCase("None")) {
            if (params.equalsIgnoreCase("tag")) {
                text = tags.get(UserData.getActive(player.getUniqueId())).getTag();
            } else if (params.equalsIgnoreCase("identifier")) {
                text = UserData.getActive(player.getUniqueId());
            }
        }
        return text;
    }

}
