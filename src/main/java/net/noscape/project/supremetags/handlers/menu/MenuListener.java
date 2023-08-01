package net.noscape.project.supremetags.handlers.menu;

import net.noscape.project.supremetags.SupremeTagsPremium;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;

public class MenuListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != null) {
            InventoryHolder holder = e.getClickedInventory().getHolder();

            if (holder instanceof Menu) {
                e.setCancelled(true);

                if (e.getCurrentItem() == null) {
                    return;
                }

                Menu menu = (Menu) holder;
                menu.handleMenu(e);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();

        if (e.getInventory().getHolder() != null) {
            InventoryHolder holder = e.getInventory().getHolder();

            if (holder instanceof Menu) {
                SupremeTagsPremium.getInstance().getMenuUtil().remove(p);
            }
        }
    }
}