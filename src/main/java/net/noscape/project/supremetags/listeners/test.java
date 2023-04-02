package net.noscape.project.supremetags.listeners;

import net.noscape.project.supremetags.api.events.TagAssignEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class test implements Listener {

    @EventHandler
    public void onAssign(TagAssignEvent event) {
        Player player = event.getPlayer();

        event.setTag("default");

    }
}