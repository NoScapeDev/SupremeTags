package net.noscape.project.supremetags.handlers.menu;

import net.noscape.project.supremetags.*;
import net.noscape.project.supremetags.storage.UserData;
import org.bukkit.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

import java.util.*;

import static net.noscape.project.supremetags.utils.Utils.color;
import static net.noscape.project.supremetags.utils.Utils.format;

public abstract class Menu implements InventoryHolder {

    protected Inventory inventory;

    protected MenuUtil menuUtil;

    public Menu(MenuUtil menuUtil) {
        this.menuUtil = menuUtil;
    }

    public abstract String getMenuName();

    public abstract int getSlots();

    public abstract void handleMenu(InventoryClickEvent e);

    public abstract void setMenuItems();

    public void open() {

        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());

        this.setMenuItems();

        menuUtil.getOwner().openInventory(inventory);
    }

    public void refresh() {
        menuUtil.getOwner().updateInventory();
    }


    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public ItemStack makeItem(Material material, String displayName, String... lore) {

        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(format(displayName));

        itemMeta.setLore(Arrays.asList(lore));
        item.setItemMeta(itemMeta);

        return item;
    }

    public ItemStack makeItem(Material material, String displayName, List<String> lore) {

        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(format(displayName));

        itemMeta.setLore(color(lore));
        item.setItemMeta(itemMeta);

        return item;
    }

    public void fillEmpty() {
        for (int i = 0; i <inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.glass-material")).toUpperCase()), " "));
            }
        }
    }
}
