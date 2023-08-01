package net.noscape.project.supremetags.guis.tageditor;

import net.noscape.project.supremetags.SupremeTags;
import net.noscape.project.supremetags.enums.EditingType;
import net.noscape.project.supremetags.handlers.Editor;
import net.noscape.project.supremetags.handlers.Tag;
import net.noscape.project.supremetags.handlers.menu.Menu;
import net.noscape.project.supremetags.handlers.menu.MenuUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.noscape.project.supremetags.utils.Utils.*;

public class SpecificTagMenu extends Menu {

    public SpecificTagMenu(MenuUtil menuUtil) {
        super(menuUtil);
    }

    @Override
    public String getMenuName() {
        return "Tag ➟ [" + menuUtil.getIdentifier() + "]";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack i = e.getCurrentItem();

        if (i == null) return;
        if (i.getItemMeta() == null) return;
        if (SupremeTags.getInstance().getEditorList().containsKey(player)) return;

        String displayname = deformat(i.getItemMeta().getDisplayName());

        if (displayname.equalsIgnoreCase("Change Tag")) {
            Editor editor = new Editor(menuUtil.getIdentifier(), EditingType.CHANGING_TAG);
            SupremeTags.getInstance().getEditorList().put(player, editor);
            player.closeInventory();
            msgPlayer(player, "&8[&6&lTags&8] &7You are setting a new tag. &6Type it in chat.");
        } else if (displayname.equalsIgnoreCase("Change Description")) {
            Editor editor = new Editor(menuUtil.getIdentifier(), EditingType.CHANGING_DESCRIPTION);
            SupremeTags.getInstance().getEditorList().put(player, editor);
            player.closeInventory();
            msgPlayer(player, "&8[&6&lTags&8] &7You are setting a new description. &6Type it in chat.");
        } else if (displayname.equalsIgnoreCase("Change Category")) {
            Editor editor = new Editor(menuUtil.getIdentifier(), EditingType.CHANGING_CATEGORY);
            SupremeTags.getInstance().getEditorList().put(player, editor);
            player.closeInventory();
            msgPlayer(player, "&8[&6&lTags&8] &7You are setting a new category. &6Type it in chat.");
        } else if (displayname.equalsIgnoreCase("Change Permission")) {
            Editor editor = new Editor(menuUtil.getIdentifier(), EditingType.CHANGING_PERMISSION);
            SupremeTags.getInstance().getEditorList().put(player, editor);
            player.closeInventory();
            msgPlayer(player, "&8[&6&lTags&8] &7You are setting a new permission. &6Type it in chat.");
        } else if (displayname.equalsIgnoreCase("Delete Tag")) {
            SupremeTags.getInstance().getTagManager().deleteTag(player, menuUtil.getIdentifier());
            player.closeInventory();
            msgPlayer(player, "&8[&6&lTags&8] &7Tag deleted!");
        } else {
            e.setCancelled(true);
        }
    }

    @Override
    public void setMenuItems() {
        if (menuUtil.getIdentifier() != null) {
            if (SupremeTags.getInstance().getTagManager().getTag(menuUtil.getIdentifier()) != null) {
                Tag t = SupremeTags.getInstance().getTagManager().getTag(menuUtil.getIdentifier());

                List<String> lore = new ArrayList<>();

                lore.add("&7Identifier: &6" + t.getIdentifier());
                lore.add("&7Tag: " + t.getTag());
                lore.add("&7Permission: &6" + t.getPermission());
                lore.add("&7Category: &6" + t.getCategory());
                lore.add("&7Cost: &6" + t.getCost());
                lore.add("&7Description:");
                lore.add("&6" + t.getDescription());

                String displayname;

                if (SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".displayname") != null) {
                    displayname = Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".displayname")).replace("%tag%", t.getTag());
                } else {
                    displayname = format("&7Tag: " + t.getTag());
                }

                getInventory().setItem(4, makeItem(Material.BOOK, format(displayname), lore));

                List<String> c_tag = new ArrayList<>();
                c_tag.add("&7Current: &6" + t.getTag());
                getInventory().setItem(20, makeItem(Material.NAME_TAG, format("&e&lChange Tag"), c_tag));

                List<String> c_desc = new ArrayList<>();
                c_desc.add("&7Current: &6" + t.getDescription());
                getInventory().setItem(29, makeItem(Material.OAK_SIGN, format("&e&lChange Description"), c_desc));

                List<String> c_cat = new ArrayList<>();
                c_cat.add("&7Current: &6" + t.getCategory());
                getInventory().setItem(22, makeItem(Material.BOOK, format("&e&lChange Category"), c_cat));

                List<String> c_perm = new ArrayList<>();
                c_perm.add("&7Current: &6" + t.getPermission());
                getInventory().setItem(31, makeItem(Material.REDSTONE_TORCH, format("&e&lChange Permission"), c_perm));

                getInventory().setItem(24, makeItem(Material.BARRIER, format("&c&lComing Soon!")));
                getInventory().setItem(33, makeItem(Material.BARRIER, format("&c&lComing Soon!")));

                List<String> c_delete = new ArrayList<>();
                c_delete.add("&7This cannot be undone!");
                getInventory().setItem(49, makeItem(Material.RED_WOOL, format("&c&lDelete Tag"), c_delete));
            }
        }
        fillEmpty();
    }
}
