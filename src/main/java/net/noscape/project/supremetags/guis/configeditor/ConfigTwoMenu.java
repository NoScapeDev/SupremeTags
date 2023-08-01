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

public class ConfigTwoMenu extends Menu {

    public ConfigTwoMenu(MenuUtil menuUtil) {
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

        // legacy hex format
        if (e.getSlot() == 19) {
            if (e.getCurrentItem().getType().equals(Material.LIME_DYE)) {
                SupremeTagsPremium.getInstance().getConfig().set("settings.legacy-hex-format", false);
                SupremeTagsPremium.getInstance().saveConfig();

                SupremeTagsPremium.getInstance().reload();
                super.open();
            } else if (e.getCurrentItem().getType().equals(Material.GRAY_DYE)) {
                SupremeTagsPremium.getInstance().getConfig().set("settings.legacy-hex-format", true);
                SupremeTagsPremium.getInstance().saveConfig();

                SupremeTagsPremium.getInstance().reload();
                super.open();
            }
        }

        // cmi color support
        if (e.getSlot() == 21) {
            if (e.getCurrentItem().getType().equals(Material.LIME_DYE)) {
                SupremeTagsPremium.getInstance().getConfig().set("settings.cmi-color-support", false);
                SupremeTagsPremium.getInstance().saveConfig();

                SupremeTagsPremium.getInstance().reload();
                super.open();
            } else if (e.getCurrentItem().getType().equals(Material.GRAY_DYE)) {
                SupremeTagsPremium.getInstance().getConfig().set("settings.cmi-color-support", true);
                SupremeTagsPremium.getInstance().saveConfig();

                SupremeTagsPremium.getInstance().reload();
                super.open();
            }
        }

        // tag vouchers
        if (e.getSlot() == 23) {
            if (e.getCurrentItem().getType().equals(Material.LIME_DYE)) {
                SupremeTagsPremium.getInstance().getConfig().set("settings.tag-vouchers", false);
                SupremeTagsPremium.getInstance().saveConfig();

                SupremeTagsPremium.getInstance().reload();
                super.open();
            } else if (e.getCurrentItem().getType().equals(Material.GRAY_DYE)) {
                SupremeTagsPremium.getInstance().getConfig().set("settings.tag-vouchers", true);
                SupremeTagsPremium.getInstance().saveConfig();

                SupremeTagsPremium.getInstance().reload();
                super.open();
            }
        }

        // category fill empty
        if (e.getSlot() == 25) {
            if (e.getCurrentItem().getType().equals(Material.LIME_DYE)) {
                SupremeTagsPremium.getInstance().getConfig().set("categories-menu-fill-empty", false);
                SupremeTagsPremium.getInstance().saveConfig();

                SupremeTagsPremium.getInstance().reload();
                super.open();
            } else if (e.getCurrentItem().getType().equals(Material.GRAY_DYE)) {
                SupremeTagsPremium.getInstance().getConfig().set("categories-menu-fill-empty", true);
                SupremeTagsPremium.getInstance().saveConfig();

                SupremeTagsPremium.getInstance().reload();
                super.open();
            }
        }

        if (e.getSlot() == 0) {
            if (e.getCurrentItem().getType().equals(Material.ARROW)) {
                new ConfigOneMenu(SupremeTagsPremium.getMenuUtil(player)).open();
            }
        }
    }

    @Override
    public void setMenuItems() {

        /// add all items needed.
        List<String> legacy = new ArrayList<>();
        legacy.add("&7Legacy Hex Support is allows you to enable ");
        legacy.add("&7&#<hexcode> color formatting in SupremeTags.");
        inventory.setItem(10, makeItem(Material.PAPER, format("&c&lLegacy Hex"), color(legacy)));
        if (SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.legacy-hex-format")) {
            inventory.setItem(19, makeItem(Material.LIME_DYE, format("&a&lEnabled"), 0));
        } else {
            inventory.setItem(19, makeItem(Material.GRAY_DYE, format("&7&lDisabled"), 0));
        }

        List<String> cmi_color = new ArrayList<>();
        cmi_color.add("&7CMI Support allows you to use &-x&-x formatting ");
        cmi_color.add("&7in SupremeTags. Allowing you to use Tag placeholder in ");
        cmi_color.add("&7in CMI chat function");
        inventory.setItem(12, makeItem(Material.PAPER, format("&6&lCMI Color Support"), color(cmi_color)));
        if (SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.cmi-color-support")) {
            inventory.setItem(21, makeItem(Material.LIME_DYE, format("&a&lEnabled"), 0));
        } else {
            inventory.setItem(21, makeItem(Material.GRAY_DYE, format("&7&lDisabled"), 0));
        }

        List<String> tag_vouchers = new ArrayList<>();
        tag_vouchers.add("&7Tag vouchers allows players to withdraw ");
        tag_vouchers.add("&7a tag, providing the ability to gift public ");
        tag_vouchers.add("&7tags. ");
        tag_vouchers.add("");
        tag_vouchers.add("&7On withdrawing a tag, an tag voucher item ");
        tag_vouchers.add("&7is given to the player.");
        inventory.setItem(14, makeItem(Material.PAPER, format("&e&lTag Vouchers"), color(tag_vouchers)));
        if (SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.tag-vouchers")) {
            inventory.setItem(23, makeItem(Material.LIME_DYE, format("&a&lEnabled"), 0));
        } else {
            inventory.setItem(23, makeItem(Material.GRAY_DYE, format("&7&lDisabled"), 0));
        }

        List<String> cat_empty = new ArrayList<>();
        cat_empty.add("&7Category Empty Fill simply fills the category ");
        cat_empty.add("&7main menu with the glass material stated in config. ");
        inventory.setItem(16, makeItem(Material.PAPER, format("&a&lCategory Empty Fill"), color(cat_empty)));
        if (SupremeTagsPremium.getInstance().getConfig().getBoolean("categories-menu-fill-empty")) {
            inventory.setItem(25, makeItem(Material.LIME_DYE, format("&a&lEnabled"), 0));
        } else {
            inventory.setItem(25, makeItem(Material.GRAY_DYE, format("&7&lDisabled"), 0));
        }

        inventory.setItem(38, makeItem(Material.RED_DYE, format("&c"), 0));
        inventory.setItem(40, makeItem(Material.RED_DYE, format("&c"), 0));
        inventory.setItem(42, makeItem(Material.RED_DYE, format("&c"), 0));

        inventory.setItem(0, makeItem(Material.ARROW, format("&7Back"), 0));
        inventory.setItem(53, makeItem(Material.BARRIER, format("&cClose"), 0));
    }
}