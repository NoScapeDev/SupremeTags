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
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        String format = e.getFormat();

        final String replace = format.replace("{tag}", "").replace("{supremetags_tag}", "").replace("{TAG}", "");
        if (UserData.getActive(player.getUniqueId()).equalsIgnoreCase("None")) {
            e.setFormat(replace);
        } else if (UserData.getActive(player.getUniqueId()) == null) {
            e.setFormat(replace);
        } else if(SupremeTags.getInstance().getTagManager().getTags().get(UserData.getActive(player.getUniqueId())).getTag() == null) {
            e.setFormat(replace);
        } else {
            e.setFormat(format.replace("{tag}", format(SupremeTags.getInstance().getTagManager().getTags().get(UserData.getActive(player.getUniqueId())).getTag())).replace("{supremetags_tag}", format(SupremeTags.getInstance().getTagManager().getTags().get(UserData.getActive(player.getUniqueId())).getTag())).replace("{TAG}", format(SupremeTags.getInstance().getTagManager().getTags().get(UserData.getActive(player.getUniqueId())).getTag())));
        }
    }

    public Map<String, Tag> getTags() {
        return tags;
    }
}