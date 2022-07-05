package net.noscape.project.supremetags.handlers.menu;

import net.noscape.project.supremetags.storage.*;
import org.bukkit.*;

import static net.noscape.project.supremetags.utils.Utils.format;

public abstract class Paged extends Menu {

    protected int page = 0;
    protected int maxItems = 36;
    protected int index = 0;

    public Paged(MenuUtil menuUtil) {
        super(menuUtil);
    }

    public void addBottom() {
        inventory.setItem(48, makeItem(Material.DARK_OAK_BUTTON, ChatColor.GRAY + "Back"));

        inventory.setItem(49, makeItem(Material.BARRIER, ChatColor.RED + "Close"));

        inventory.setItem(50, makeItem(Material.DARK_OAK_BUTTON, ChatColor.GRAY + "Next"));

        inventory.setItem(46, makeItem(Material.RED_DYE, ChatColor.RED + "Reset Tag"));

        inventory.setItem(52, makeItem(Material.NAME_TAG, format("&7Active: &6" + UserData.getActive(menuUtil.getOwner()))));

        inventory.setItem(36, super.GLASS);
        inventory.setItem(37, super.GLASS);
        inventory.setItem(38, super.GLASS);
        inventory.setItem(39, super.GLASS);
        inventory.setItem(40, super.GLASS);
        inventory.setItem(41, super.GLASS);
        inventory.setItem(42, super.GLASS);
        inventory.setItem(43, super.GLASS);
        inventory.setItem(44, super.GLASS);
    }

    public int getMaxItems() {
        return maxItems;
    }

}
