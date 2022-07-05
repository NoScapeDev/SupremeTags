package net.noscape.project.supremetags.listeners;

import net.noscape.project.supremetags.*;
import net.noscape.project.supremetags.storage.*;
import org.bukkit.event.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.*;

import static net.noscape.project.supremetags.utils.Utils.format;

public class PlayerEvents implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        SupremeTags.getInstance().getUserData().createPlayer(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        String format = e.getFormat();

        if (UserData.getActive(player).equals("None")) {
            e.setFormat(format.replace("{tag}", "").replace("{supremetags_tag}", ""));
        } else {
            e.setFormat(format.replace("{tag}", format(SupremeTags.getInstance().getTagManager().getTags().get(UserData.getActive(player)).getTag())).replace("{supremetags_tag}", format(SupremeTags.getInstance().getTagManager().getTags().get(UserData.getActive(player)).getTag())));
        }
    }
}