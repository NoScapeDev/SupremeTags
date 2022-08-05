package net.noscape.project.supremetags.handlers.menu;

import net.noscape.project.supremetags.*;
import net.noscape.project.supremetags.storage.*;
import org.bukkit.*;

import java.util.*;

import static net.noscape.project.supremetags.utils.Utils.format;

public abstract class Paged extends Menu {

    protected int page = 0;
    protected int maxItems = 36;
    protected int index = 0;

    public Paged(MenuUtil menuUtil) {
        super(menuUtil);
    }

    public void addBottom() {
        inventory.setItem(48, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.back-next-material")).toUpperCase()), ChatColor.GRAY + "Back"));

        inventory.setItem(49, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.close-menu-material")).toUpperCase()), ChatColor.RED + "Close"));

        inventory.setItem(50, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.back-next-material")).toUpperCase()), ChatColor.GRAY + "Next"));

        if (!SupremeTags.getInstance().getConfig().getBoolean("settings.forced-tag")) {
            inventory.setItem(46, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.reset-tag-material")).toUpperCase()), ChatColor.RED + "Reset Tag"));
        }

        inventory.setItem(52, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.tag-material")).toUpperCase()), format("&7Active: &6" + UserData.getActive(menuUtil.getOwner()))));

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
