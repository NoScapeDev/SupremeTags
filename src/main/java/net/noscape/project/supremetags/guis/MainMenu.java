package net.noscape.project.supremetags.guis;

import net.noscape.project.supremetags.*;
import net.noscape.project.supremetags.handlers.menu.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

import java.util.*;

import static net.noscape.project.supremetags.utils.Utils.*;

public class MainMenu extends Menu {

    private final List<String> catorgies;
    private final Map<Integer, String> dataItem = new HashMap<>();
    private final Map<String, Integer> categoriesTags;

    public MainMenu(MenuUtil menuUtil) {
        super(menuUtil);
        this.catorgies = SupremeTagsPremium.getInstance().getCategoryManager().getCatorgies();
        this.categoriesTags = SupremeTagsPremium.getInstance().getCategoryManager().getCatorgiesTags();
    }

    @Override
    public String getMenuName() {
        return format(SupremeTagsPremium.getInstance().getConfig().getString("gui.main-menu.title"));
    }

    @Override
    public int getSlots() {
        return SupremeTagsPremium.getInstance().getConfig().getInt("gui.main-menu.size");
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {

        Player player = (Player) e.getWhoClicked();
        String category = dataItem.get(e.getSlot());
        String material = SupremeTagsPremium.getInstance().getCategoryManager().getCatConfig().getString("categories." + category + ".material");
        String permission = SupremeTagsPremium.getInstance().getCategoryManager().getCatConfig().getString("categories." + category + ".permission");

        boolean hasMinTags = false;

        for (String cats : getCatorgies()) {
            if (cats != null) {
                if (categoriesTags.get(cats) != null) {
                    hasMinTags = true;
                }
                break;
            }
        }

        if (category != null) {
            if (material != null && Objects.requireNonNull(e.getCurrentItem()).getType().equals(Material.valueOf(material.toUpperCase()))) {
                if (hasMinTags) {
                    if (permission != null && player.hasPermission(permission)) {
                        menuUtil.setCategory(category);
                        new CategoryMenu(SupremeTagsPremium.getMenuUtil(player, category)).open();
                    } else {
                        msgPlayer(player, "&cYou don't have permission to access these tags.");
                    }
                } else {
                    e.setCancelled(true);
                    msgPlayer(player, "&cThere are no tags in this category.");
                }
            }
        }
    }

    @Override
    public void setMenuItems() {
        for (String cats : getCatorgies()) {
            if (cats != null) {
                boolean canSee = SupremeTagsPremium.getInstance().getCategoryManager().getCatConfig().getBoolean("categories." + cats + ".permission-see-category");
                String permission = SupremeTagsPremium.getInstance().getCategoryManager().getCatConfig().getString("categories." + cats + ".permission");
                String material = SupremeTagsPremium.getInstance().getCategoryManager().getCatConfig().getString("categories." + cats + ".material");
                int slot = SupremeTagsPremium.getInstance().getCategoryManager().getCatConfig().getInt("categories." + cats + ".slot");
                int custom_model_data = SupremeTagsPremium.getInstance().getCategoryManager().getCatConfig().getInt("categories." + cats + ".custom-model-data");
                String displayname = SupremeTagsPremium.getInstance().getCategoryManager().getCatConfig().getString("categories." + cats + ".id_display");

                if (permission != null && menuUtil.getOwner().hasPermission(permission) && canSee) {
                    assert material != null;
                    ItemStack cat_item = new ItemStack(Material.valueOf(material.toUpperCase()), 1);
                    ItemMeta cat_itemMeta = cat_item.getItemMeta();
                    assert cat_itemMeta != null;
                    cat_itemMeta.setDisplayName(format(displayname));
                    cat_itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    cat_itemMeta.addItemFlags(ItemFlag.HIDE_DYE);
                    cat_itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);

                    if (custom_model_data > 0) {
                        cat_itemMeta.setCustomModelData(custom_model_data);
                    }

                    // Set lore
                    ArrayList<String> lore = (ArrayList<String>) SupremeTagsPremium.getInstance().getCategoryManager().getCatConfig().getStringList("categories." + cats + ".lore");
                    if (categoriesTags.get(cats) != null) {
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tags_amount%", String.valueOf(categoriesTags.get(cats))));
                    } else {
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tags_amount%", String.valueOf(0)));
                    }

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

                    if (custom_model_data > 0) {
                        cat_itemMeta.setCustomModelData(custom_model_data);
                    }

                    // Set lore
                    ArrayList<String> lore = (ArrayList<String>) SupremeTagsPremium.getInstance().getCategoryManager().getCatConfig().getStringList("categories." + cats + ".lore");
                    if (categoriesTags.get(cats) != null) {
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tags_amount%", String.valueOf(categoriesTags.get(cats))));
                    } else {
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tags_amount%", String.valueOf(0)));
                    }
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

                    if (custom_model_data > 0) {
                        cat_itemMeta.setCustomModelData(custom_model_data);
                    }

                    // Set lore
                    ArrayList<String> lore = (ArrayList<String>) SupremeTagsPremium.getInstance().getCategoryManager().getCatConfig().getStringList("categories." + cats + ".lore");
                    lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tags_amount%", String.valueOf(categoriesTags.get(cats))));
                    cat_itemMeta.setLore(color(lore));

                    cat_item.setItemMeta(cat_itemMeta);

                    dataItem.put(slot, cats);

                    inventory.setItem(slot, cat_item);
                }
            }
        }

        if (SupremeTagsPremium.getInstance().getCategoryManager().getCatConfig().getBoolean("categories-menu-fill-empty")) {
            fillEmpty();
        }
    }

    public List<String> getCatorgies() {
        return catorgies;
    }

    public Map<Integer, String> getDataItem() {
        return dataItem;
    }
}
