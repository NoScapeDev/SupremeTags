package net.noscape.project.supremetags.listeners;

import net.noscape.project.supremetags.*;
import net.noscape.project.supremetags.handlers.*;
import net.noscape.project.supremetags.storage.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

import java.util.*;

import static net.noscape.project.supremetags.utils.Utils.*;

public class PlayerEvents implements Listener {

    private final Map<String, Tag> tags;

    public PlayerEvents() {
        tags = SupremeTags.getInstance().getTagManager().getTags();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UserData.createPlayer(player);

        //if (SupremeTags.getInstance().getConfig().getBoolean("settings.personal-tags")) {
            //SupremeTags.getInstance().getPlayerConfig().loadPlayer(player);
        //}

        player.getClientViewDistance();

        if (SupremeTags.getInstance().getConfig().getBoolean("settings.forced-tag")) {
            String activeTag = UserData.getActive(player.getUniqueId());
            if (activeTag.equalsIgnoreCase("None")) {
                String defaultTag = SupremeTags.getInstance().getConfig().getString("settings.default-tag");
                UserData.setActive(player, defaultTag);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String format = e.getFormat();

        // Store the value of UserData.getActive(player.getUniqueId()) in a local variable
        String activeTag = UserData.getActive(player.getUniqueId());
        String replace = format.replace("{tag}", "").replace("{supremetags_tag}", "").replace("{TAG}", "");
        if (activeTag == null || activeTag.equalsIgnoreCase("None")) {
            e.setFormat(replace);
        } else {
            // Store the value of SupremeTags.getInstance().getTagManager().getTags().get(activeTag) in a local variable
            Tag tag = SupremeTags.getInstance().getTagManager().getTags().get(activeTag);
            if (tag == null) {
                e.setFormat(replace);
            } else {
                // Store the value of format(twag.getTag()) in a local variable
                String formattedTag = format(tag.getTag());
                formattedTag = replacePlaceholders(player, formattedTag);

                e.setFormat(format.replace("{tag}", formattedTag).replace("{supremetags_tag}", formattedTag).replace("{TAG}", formattedTag));
            }
        }
    }

    public Map<String, Tag> getTags() {
        return tags;
    }
}