package net.noscape.project.supremetags.guis.configeditor;

import net.noscape.project.supremetags.SupremeTagsPremium;
import net.noscape.project.supremetags.handlers.menu.Menu;
import net.noscape.project.supremetags.handlers.menu.MenuUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.noscape.project.supremetags.utils.Utils.color;
import static net.noscape.project.supremetags.utils.Utils.format;

public class ConfigOneMenu extends Menu {

    public ConfigOneMenu(MenuUtil menuUtil) {
        super(menuUtil);
    }

    @Override
    public String getMenuName() {
        return format("&8SupremeTags Configuration");
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        if (Objects.requireNonNull(e.getCurrentItem()).getType().equals(Material.valueOf(Objects.requireNonNull(SupremeTagsPremium.getInstance().getConfig().getString("gui.items.glass.material")).toUpperCase()))) {
            e.setCancelled(true);
        }

        if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equalsIgnoreCase(format(SupremeTagsPremium.getInstance().getConfig().getString("gui.items.close.displayname")))) {
            player.closeInventory();
        }

        // categories
        if (e.getSlot() == 19) {
            if (e.getCurrentItem().getType().equals(Material.LIME_DYE)) {
                SupremeTagsPremium.getInstance().getConfig().set("settings.categories", false);
                SupremeTagsPremium.getInstance().saveConfig();

                SupremeTagsPremium.getInstance().reload();
                super.open();
            } else if (e.getCurrentItem().getType().equals(Material.GRAY_DYE)) {
                SupremeTagsPremium.getInstance().getConfig().set("settings.categories", true);
                SupremeTagsPremium.getInstance().saveConfig();

                SupremeTagsPremium.getInstance().reload();
                super.open();
            }
        }

        // cost system
        if (e.getSlot() == 21) {
            if (e.getCurrentItem().getType().equals(Material.LIME_DYE)) {
                SupremeTagsPremium.getInstance().getConfig().set("settings.cost-system", false);
                SupremeTagsPremium.getInstance().saveConfig();

                SupremeTagsPremium.getInstance().reload();
                super.open();
            } else if (e.getCurrentItem().getType().equals(Material.GRAY_DYE)) {
                SupremeTagsPremium.getInstance().getConfig().set("settings.cost-system", true);
                SupremeTagsPremium.getInstance().saveConfig();

                SupremeTagsPremium.getInstance().reload();
                super.open();
            }
        }

        // locked view
        if (e.getSlot() == 23) {
            if (e.getCurrentItem().getType().equals(Material.LIME_DYE)) {
                SupremeTagsPremium.getInstance().getConfig().set("settings.locked-view", false);
                SupremeTagsPremium.getInstance().saveConfig();

                SupremeTagsPremium.getInstance().reload();
                super.open();
            } else if (e.getCurrentItem().getType().equals(Material.GRAY_DYE)) {
                SupremeTagsPremium.getInstance().getConfig().set("settings.locked-view", true);
                SupremeTagsPremium.getInstance().saveConfig();

                SupremeTagsPremium.getInstance().reload();
                super.open();
            }
        }

        // personal tags
        if (e.getSlot() == 25) {
            if (e.getCurrentItem().getType().equals(Material.LIME_DYE)) {
                SupremeTagsPremium.getInstance().getConfig().set("settings.personal-tags.enable", false);
                SupremeTagsPremium.getInstance().saveConfig();

                SupremeTagsPremium.getInstance().reload();
                super.open();
            } else if (e.getCurrentItem().getType().equals(Material.GRAY_DYE)) {
                SupremeTagsPremium.getInstance().getConfig().set("settings.personal-tags.enable", true);
                SupremeTagsPremium.getInstance().saveConfig();

                SupremeTagsPremium.getInstance().reload();
                super.open();
            }
        }

        // active tag glow
        if (e.getSlot() == 47) {
            if (e.getCurrentItem().getType().equals(Material.LIME_DYE)) {
                SupremeTagsPremium.getInstance().getConfig().set("settings.active-tag-glow", false);
                SupremeTagsPremium.getInstance().saveConfig();

                SupremeTagsPremium.getInstance().reload();
                super.open();
            } else if (e.getCurrentItem().getType().equals(Material.GRAY_DYE)) {
                SupremeTagsPremium.getInstance().getConfig().set("settings.active-tag-glow", true);
                SupremeTagsPremium.getInstance().saveConfig();

                SupremeTagsPremium.getInstance().reload();
                super.open();
            }
        }

        // layout type
        if (e.getSlot() == 49) {
            if (e.getCurrentItem().getType().equals(Material.WHITE_STAINED_GLASS_PANE)) {
                SupremeTagsPremium.getInstance().getConfig().set("settings.layout-type", "BORDER");
                SupremeTagsPremium.getInstance().saveConfig();

                SupremeTagsPremium.getInstance().reload();

                super.open();
            } else if (e.getCurrentItem().getType().equals(Material.GRAY_STAINED_GLASS_PANE)) {
                SupremeTagsPremium.getInstance().getConfig().set("settings.layout-type", "FULL");
                SupremeTagsPremium.getInstance().saveConfig();

                SupremeTagsPremium.getInstance().reload();
                super.open();
            }
        }

        // forced tag
        if (e.getSlot() == 51) {
            if (e.getCurrentItem().getType().equals(Material.LIME_DYE)) {
                SupremeTagsPremium.getInstance().getConfig().set("settings.forced-tag", false);
                SupremeTagsPremium.getInstance().saveConfig();

                SupremeTagsPremium.getInstance().reload();
                super.open();
            } else if (e.getCurrentItem().getType().equals(Material.GRAY_DYE)) {
                SupremeTagsPremium.getInstance().getConfig().set("settings.forced-tag", true);
                SupremeTagsPremium.getInstance().saveConfig();

                SupremeTagsPremium.getInstance().reload();
                super.open();
            }
        }

        if (e.getSlot() == 8) {
            if (e.getCurrentItem().getType().equals(Material.ARROW)) {
                new ConfigTwoMenu(SupremeTagsPremium.getMenuUtil(player)).open();
            }
        }
    }

    @Override
    public void setMenuItems() {

        /// add all items needed.

        List<String> cat = new ArrayList<>();
        cat.add("&7Categories enhance your tagging system, ");
        cat.add("&7providing an organized and professional look.");
        inventory.setItem(10, makeItem(Material.PAPER, format("&c&lCategories"), color(cat)));
        if (SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.categories")) {
            inventory.setItem(19, makeItem(Material.LIME_DYE, format("&a&lEnabled"), 0));
        } else {
            inventory.setItem(19, makeItem(Material.GRAY_DYE, format("&7&lDisabled"), 0));
        }

        List<String> cost = new ArrayList<>();
        cost.add("&7Cost System allows tags to become buyable, ");
        cost.add("&7providing players to purchase tags.");
        inventory.setItem(12, makeItem(Material.PAPER, format("&6&lCost System"), color(cost)));
        if (SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.cost-system")) {
            inventory.setItem(21, makeItem(Material.LIME_DYE, format("&a&lEnabled"), 0));
        } else {
            inventory.setItem(21, makeItem(Material.GRAY_DYE, format("&7&lDisabled"), 0));
        }

        List<String> locked = new ArrayList<>();
        locked.add("&7Locked View allows all tags to become visible ");
        locked.add("&7in the gui, providing players to see tags before ");
        locked.add("&7unlocked them. ");
        inventory.setItem(14, makeItem(Material.PAPER, format("&e&lLocked View"), color(locked)));
        if (SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.locked-view")) {
            inventory.setItem(23, makeItem(Material.LIME_DYE, format("&a&lEnabled"), 0));
        } else {
            inventory.setItem(23, makeItem(Material.GRAY_DYE, format("&7&lDisabled"), 0));
        }

        List<String> personal = new ArrayList<>();
        personal.add("&7Personal tags allows players to build their ");
        personal.add("&7own tags with /mytags, you can set player limits ");
        personal.add("&7in you config.yml. ");
        inventory.setItem(16, makeItem(Material.PAPER, format("&a&lPersonal Tags"), color(personal)));
        if (SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.personal-tags.enable")) {
            inventory.setItem(25, makeItem(Material.LIME_DYE, format("&a&lEnabled"), 0));
        } else {
            inventory.setItem(25, makeItem(Material.GRAY_DYE, format("&7&lDisabled"), 0));
        }

        List<String> activeglow = new ArrayList<>();
        activeglow.add("&7Active Tag Glow allows the player's selected tag ");
        activeglow.add("&7to assign an enchanted effect in the gui, ");
        activeglow.add("&7indicating their active tag. ");
        inventory.setItem(38, makeItem(Material.PAPER, format("&b&lActive Tag Glow"), color(activeglow)));
        if (SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.active-tag-glow")) {
            inventory.setItem(47, makeItem(Material.LIME_DYE, format("&a&lEnabled"), 0));
        } else {
            inventory.setItem(47, makeItem(Material.GRAY_DYE, format("&7&lDisabled"), 0));
        }

        List<String> layoutype = new ArrayList<>();
        layoutype.add("&7There are 2 layout styles you can pick from, ");
        layoutype.add("&7I designed these 2 layout types to fit ");
        layoutype.add("&7professionalism and similar experience. ");
        layoutype.add("");
        layoutype.add("&fOptions:");
        if (SupremeTagsPremium.getInstance().getConfig().getString("settings.layout-type").equalsIgnoreCase("FULL")) {
            layoutype.add("&a➟ &fFull");
            layoutype.add("   &7Border");
        } else {
            layoutype.add("   &7Full");
            layoutype.add("&a➟ &fBorder");
        }
        inventory.setItem(40, makeItem(Material.PAPER, format("&3&lLayout Type"), color(layoutype)));
        if (SupremeTagsPremium.getInstance().getConfig().getString("settings.layout-type").equalsIgnoreCase("FULL")) {
            inventory.setItem(49, makeItem(Material.WHITE_STAINED_GLASS_PANE, format("&7Type: &f&lFull"), 0));
        } else {
            inventory.setItem(49, makeItem(Material.GRAY_STAINED_GLASS_PANE, format("&7Type: &f&lBorder"), 0));
        }

        List<String> forced = new ArrayList<>();
        forced.add("&7Forced tag allow you to essentially force ");
        forced.add("&7tags upon players, this means that the reset ");
        forced.add("&7gui button will not displayed. ");
        inventory.setItem(42, makeItem(Material.PAPER, format("&4&lForced Tag"), color(forced)));
        if (SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.forced-tag")) {
            inventory.setItem(51, makeItem(Material.LIME_DYE, format("&a&lEnabled"), 0));
        } else {
            inventory.setItem(51, makeItem(Material.GRAY_DYE, format("&7&lDisabled"), 0));
        }

        inventory.setItem(8, makeItem(Material.ARROW, format("&7Next"), 0));
        inventory.setItem(53, makeItem(Material.BARRIER, format("&cClose"), 0));

        ///fillEmpty();
    }
}
