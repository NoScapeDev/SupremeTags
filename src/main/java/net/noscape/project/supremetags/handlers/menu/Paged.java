package net.noscape.project.supremetags.handlers.menu;

import net.noscape.project.supremetags.*;
import net.noscape.project.supremetags.storage.*;
import org.bukkit.*;

import java.util.*;

import static net.noscape.project.supremetags.utils.Utils.format;

public abstract class Paged extends Menu {

    protected int page = 0;
    protected int maxItems = 35;
    protected int index = 0;

    public Paged(MenuUtil menuUtil) {
        super(menuUtil);
    }

    public void applyLayout() {
        if (SupremeTags.getLayout().equalsIgnoreCase("layout1")) {
            inventory.setItem(48, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.back-next-material")).toUpperCase()), ChatColor.GRAY + "Back"));

            inventory.setItem(49, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.close-menu-material")).toUpperCase()), ChatColor.RED + "Close"));

            inventory.setItem(50, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.back-next-material")).toUpperCase()), ChatColor.GRAY + "Next"));

            if (!SupremeTags.getInstance().getConfig().getBoolean("settings.forced-tag")) {
                inventory.setItem(46, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.reset-tag-material")).toUpperCase()), ChatColor.RED + "Reset Tag"));
            }

            inventory.setItem(45, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.refresh-material")).toUpperCase()), ChatColor.GREEN + "Refresh Menu"));

            inventory.setItem(52, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.tag-material")).toUpperCase()), format("&7Active: &6" + UserData.getActive(menuUtil.getOwner().getUniqueId()))));

            inventory.setItem(36, super.GLASS);
            inventory.setItem(37, super.GLASS);
            inventory.setItem(38, super.GLASS);
            inventory.setItem(39, super.GLASS);
            inventory.setItem(40, super.GLASS);
            inventory.setItem(41, super.GLASS);
            inventory.setItem(42, super.GLASS);
            inventory.setItem(43, super.GLASS);
            inventory.setItem(44, super.GLASS);
        } else if (SupremeTags.getLayout().equalsIgnoreCase("layout2")) {
            inventory.setItem(3, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.back-next-material")).toUpperCase()), ChatColor.GRAY + "Back"));

            inventory.setItem(4, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.close-menu-material")).toUpperCase()), ChatColor.RED + "Close"));

            inventory.setItem(5, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.back-next-material")).toUpperCase()), ChatColor.GRAY + "Next"));

            if (!SupremeTags.getInstance().getConfig().getBoolean("settings.forced-tag")) {
                inventory.setItem(1, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.reset-tag-material")).toUpperCase()), ChatColor.RED + "Reset Tag"));
            } else {
                inventory.setItem(1, super.GLASS);
            }

            inventory.setItem(0, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.refresh-material")).toUpperCase()), ChatColor.GREEN + "Refresh Menu"));

            inventory.setItem(7, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.tag-material")).toUpperCase()), format("&7Active: &6" + UserData.getActive(menuUtil.getOwner().getUniqueId()))));

            inventory.setItem(2, super.GLASS);
            inventory.setItem(6, super.GLASS);
            inventory.setItem(8, super.GLASS);
            inventory.setItem(9, super.GLASS);
            inventory.setItem(10, super.GLASS);
            inventory.setItem(11, super.GLASS);
            inventory.setItem(12, super.GLASS);
            inventory.setItem(13, super.GLASS);
            inventory.setItem(14, super.GLASS);
            inventory.setItem(15, super.GLASS);
            inventory.setItem(16, super.GLASS);
            inventory.setItem(17, super.GLASS);
        }
    }

    protected int getPage() {
        return page + 1;
    }

    public int getMaxItems() {
        return maxItems;
    }

}
