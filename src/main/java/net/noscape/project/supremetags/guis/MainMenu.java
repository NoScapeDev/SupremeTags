package net.noscape.project.supremetags.guis;

import net.noscape.project.supremetags.*;
import net.noscape.project.supremetags.handlers.*;
import net.noscape.project.supremetags.handlers.menu.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.scheduler.*;

import java.util.*;

import static net.noscape.project.supremetags.utils.Utils.*;

public class MainMenu extends Menu {

    private final List<String> catorgies;
    private final Map<Integer, String> dataItem = new HashMap<>();

    public MainMenu(MenuUtil menuUtil) {
        super(menuUtil);
        this.catorgies = SupremeTags.getInstance().getCategoryManager().getCatorgies();
    }

    @Override
    public String getMenuName() {
        return format(SupremeTags.getInstance().getConfig().getString("gui.main-menu.title"));
    }

    @Override
    public int getSlots() {
        return SupremeTags.getInstance().getConfig().getInt("gui.main-menu.size");
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {

        Player player = (Player) e.getWhoClicked();

        String category = dataItem.get(e.getSlot());

        String material = SupremeTags.getInstance().getConfig().getString("categories." + category + ".material");
        String permission = SupremeTags.getInstance().getConfig().getString("categories." + category + ".permission");

        if (category != null) {
            if (material != null && Objects.requireNonNull(e.getCurrentItem()).getType().equals(Material.valueOf(material.toUpperCase()))) {
                if (permission != null && player.hasPermission(permission)) {
                    new CategoryMenu(SupremeTags.getMenuUtil(player, category)).open();
                    menuUtil.setCategory(category);
                } else {
                    msgPlayer(player, "&cYou don't have permission to access these tags.");
                }
            }
        }
    }

    @Override
    public void setMenuItems() {

        // loop through categories items.
        for (String cats : getCatorgies()) {
            if (cats != null) {
                boolean canSee = SupremeTags.getInstance().getConfig().getBoolean("categories." + cats + ".permission-see-category");
                String permission = SupremeTags.getInstance().getConfig().getString("categories." + cats + ".permission");
                String material = SupremeTags.getInstance().getConfig().getString("categories." + cats + ".material");
                int slot = SupremeTags.getInstance().getConfig().getInt("categories." + cats + ".slot");
                String displayname = SupremeTags.getInstance().getConfig().getString("categories." + cats + ".id_display");

                if (permission != null && menuUtil.getOwner().hasPermission(permission) && canSee) {

                    assert material != null;
                    ItemStack cat_item = new ItemStack(Material.valueOf(material.toUpperCase()), 1);
                    ItemMeta cat_itemMeta = cat_item.getItemMeta();

                    assert cat_itemMeta != null;
                    cat_itemMeta.setDisplayName(format(displayname));

                    cat_itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    cat_itemMeta.addItemFlags(ItemFlag.HIDE_DYE);
                    cat_itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);

                    // set lore
                    ArrayList<String> lore = (ArrayList<String>) SupremeTags.getInstance().getConfig().getStringList("categories." + cats + ".lore");
                    lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tags_amount%", String.valueOf(catorgies.size())));
                    cat_itemMeta.setLore(color(lore));

                    cat_item.setItemMeta(cat_itemMeta);

                    dataItem.put(slot, cats);

                    inventory.setItem(slot, cat_item);
                } else if (permission != null && !menuUtil.getOwner().hasPermission(permission) && !canSee) {

                    assert material != null;
                    ItemStack cat_item = new ItemStack(Material.valueOf(material.toUpperCase()), 1);
                    ItemMeta cat_itemMeta = cat_item.getItemMeta();

                    assert cat_itemMeta != null;
                    cat_itemMeta.setDisplayName(format(displayname));

                    cat_itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    cat_itemMeta.addItemFlags(ItemFlag.HIDE_DYE);
                    cat_itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);

                    // set lore
                    ArrayList<String> lore = (ArrayList<String>) SupremeTags.getInstance().getConfig().getStringList("categories." + cats + ".lore");
                    lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tags_amount%", String.valueOf(catorgies.size())));
                    cat_itemMeta.setLore(color(lore));

                    cat_item.setItemMeta(cat_itemMeta);

                    dataItem.put(slot, cats);

                    inventory.setItem(slot, cat_item);
                } else if (permission != null && menuUtil.getOwner().hasPermission(permission) && !canSee) {

                    assert material != null;
                    ItemStack cat_item = new ItemStack(Material.valueOf(material.toUpperCase()), 1);
                    ItemMeta cat_itemMeta = cat_item.getItemMeta();

                    assert cat_itemMeta != null;
                    cat_itemMeta.setDisplayName(format(displayname));

                    cat_itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    cat_itemMeta.addItemFlags(ItemFlag.HIDE_DYE);
                    cat_itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);

                    // set lore
                    ArrayList<String> lore = (ArrayList<String>) SupremeTags.getInstance().getConfig().getStringList("categories." + cats + ".lore");
                    lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tags_amount%", String.valueOf(catorgies.size())));
                    cat_itemMeta.setLore(color(lore));

                    cat_item.setItemMeta(cat_itemMeta);

                    dataItem.put(slot, cats);

                    inventory.setItem(slot, cat_item);
                }
            }
        }
    }

    public List<String> getCatorgies() {
        return catorgies;
    }

    public Map<Integer, String> getDataItem() {
        return dataItem;
    }
}
