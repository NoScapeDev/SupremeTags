package net.noscape.project.supremetags.guis;

import net.noscape.project.supremetags.*;
import net.noscape.project.supremetags.handlers.*;
import net.noscape.project.supremetags.handlers.menu.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

import java.util.*;

import static net.noscape.project.supremetags.utils.Utils.*;

public class MainMenu extends Menu {



    // ==============================================
    //  !! THIS IS NOT YET BEEN IMPLEMENTED !!
    // ==============================================



    private final Map<String, Category> catorgies;
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
                } else {
                    msgPlayer(player, "&cYou don't have permission to access these tags.");
                }
            }
        }
    }

    @Override
    public void setMenuItems() {

        // loop through categories items.
        for (String config_cats : Objects.requireNonNull(SupremeTags.getInstance().getConfig().getConfigurationSection("categories")).getKeys(false)) {
            if (config_cats != null) {
                boolean permission_see_category = SupremeTags.getInstance().getConfig().getBoolean("categories." + config_cats + ".permission-see-category");
                String permission = SupremeTags.getInstance().getConfig().getString("categories." + config_cats + ".permission");
                String material = SupremeTags.getInstance().getConfig().getString("categories." + config_cats + ".material");
                int slot = SupremeTags.getInstance().getConfig().getInt("categories." + config_cats + ".slot");
                String displayname = SupremeTags.getInstance().getConfig().getString("categories." + config_cats + ".id_display");

                if (permission_see_category && permission != null && menuUtil.getOwner().hasPermission(permission)) {

                    assert material != null;
                    ItemStack cat_item = new ItemStack(Material.valueOf(material.toUpperCase()), 1);
                    ItemMeta cat_itemMeta = cat_item.getItemMeta();

                    assert cat_itemMeta != null;
                    cat_itemMeta.setDisplayName(format(displayname));

                    cat_itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    cat_itemMeta.addItemFlags(ItemFlag.HIDE_DYE);
                    cat_itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);

                    // set lore
                    ArrayList<String> lore = (ArrayList<String>) SupremeTags.getInstance().getConfig().getStringList("categories." + config_cats + ".lore");
                    lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tags_amount%", String.valueOf(catorgies.get(config_cats).getTags().size())));
                    cat_itemMeta.setLore(color(lore));

                    cat_item.setItemMeta(cat_itemMeta);

                    dataItem.put(slot, catorgies.get(config_cats).getCategory());

                    inventory.setItem(slot, cat_item);
                } else if (!permission_see_category && permission != null && !menuUtil.getOwner().hasPermission(permission)) {

                    assert material != null;
                    ItemStack cat_item = new ItemStack(Material.valueOf(material.toUpperCase()), 1);
                    ItemMeta cat_itemMeta = cat_item.getItemMeta();

                    assert cat_itemMeta != null;
                    cat_itemMeta.setDisplayName(format(displayname));

                    cat_itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    cat_itemMeta.addItemFlags(ItemFlag.HIDE_DYE);
                    cat_itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);

                    // set lore
                    ArrayList<String> lore = (ArrayList<String>) SupremeTags.getInstance().getConfig().getStringList("categories." + config_cats + ".lore");
                    lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tags_amount%", String.valueOf(catorgies.get(config_cats).getTags().size())));
                    cat_itemMeta.setLore(color(lore));

                    cat_item.setItemMeta(cat_itemMeta);

                    dataItem.put(slot, catorgies.get(config_cats).getCategory());

                    inventory.setItem(slot, cat_item);
                }
            } else {
                msgPlayer(menuUtil.getOwner(), "&cCategories is null!");
            }
        }
    }

    public Map<String, Category> getCatorgies() {
        return catorgies;
    }

    public Map<Integer, String> getDataItem() {
        return dataItem;
    }
}
