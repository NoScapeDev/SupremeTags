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
        SupremeTags.getInstance().getUserData().createPlayer(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        String format = e.getFormat();

        if (UserData.getActive(player).equalsIgnoreCase("None")) {
            e.setFormat(format.replace("{tag}", "").replace("{supremetags_tag}", ""));
        } else if (UserData.getActive(player) == null) {
            e.setFormat(format.replace("{tag}", "").replace("{supremetags_tag}", ""));
        } else {
            e.setFormat(format.replace("{tag}", format(SupremeTags.getInstance().getTagManager().getTags().get(UserData.getActive(player)).getTag())).replace("{supremetags_tag}", format(SupremeTags.getInstance().getTagManager().getTags().get(UserData.getActive(player)).getTag())));
        }
    }

    public Map<String, Tag> getTags() {
        return tags;
    }
}