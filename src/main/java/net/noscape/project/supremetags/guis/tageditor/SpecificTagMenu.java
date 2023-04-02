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
        return 27;
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

                getInventory().setItem(4, makeItem(Material.NAME_TAG, displayname, lore));
            }
        }

        getInventory().setItem(13, makeItem(Material.PAPER, format("&e&lChange Tag")));

        fillEmpty();
    }
}
