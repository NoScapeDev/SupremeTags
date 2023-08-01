package net.noscape.project.supremetags.guis.personaltags;

import net.noscape.project.supremetags.SupremeTagsPremium;
import net.noscape.project.supremetags.enums.EditingType;
import net.noscape.project.supremetags.handlers.Editor;
import net.noscape.project.supremetags.handlers.Tag;
import net.noscape.project.supremetags.handlers.menu.Menu;
import net.noscape.project.supremetags.handlers.menu.MenuUtil;
import net.noscape.project.supremetags.storage.UserData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static net.noscape.project.supremetags.utils.Utils.*;
import static net.noscape.project.supremetags.utils.Utils.msgPlayer;

public class PersonalTagEditorMenu extends Menu {

    public PersonalTagEditorMenu(MenuUtil menuUtil) {
        super(menuUtil);
    }

    @Override
    public String getMenuName() {
        return format("Tag Editor > " + menuUtil.getIdentifier());
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
        if (SupremeTagsPremium.getInstance().getEditorList().containsKey(player)) return;

        String displayname = deformat(i.getItemMeta().getDisplayName());

        if (displayname.equalsIgnoreCase("Change Tag")) {
            Editor editor = new Editor(menuUtil.getIdentifier(), EditingType.CHANGING_TAG, true);
            SupremeTagsPremium.getInstance().getEditorList().put(player, editor);
            player.closeInventory();
            msgPlayer(player, "&8[&6&lTags&8] &7You are setting a new tag. &6Type it in chat.");
        } else if (displayname.equalsIgnoreCase("Change Description")) {
            Editor editor = new Editor(menuUtil.getIdentifier(), EditingType.CHANGING_DESCRIPTION, true);
            SupremeTagsPremium.getInstance().getEditorList().put(player, editor);
            player.closeInventory();
            msgPlayer(player, "&8[&6&lTags&8] &7You are setting a new description. &6Type it in chat.");
        } else if (displayname.equalsIgnoreCase("Delete Tag")) {
            if (UserData.getActive(menuUtil.getOwner().getUniqueId()).equalsIgnoreCase(menuUtil.getIdentifier())) {
                msgPlayer(player, "&8[&6&lTags&8] &7Please unselect this tag before deleting!");
                e.setCancelled(true);
            }

            SupremeTagsPremium.getInstance().getPlayerManager().delete(player, menuUtil.getIdentifier());
            player.closeInventory();
            msgPlayer(player, "&8[&6&lTags&8] &7Tag deleted!");
        } else {
            e.setCancelled(true);
        }
    }

    @Override
    public void setMenuItems() {
        if (menuUtil.getIdentifier() != null) {
            Tag t = SupremeTagsPremium.getInstance().getPlayerManager().getTag(menuUtil.getOwner().getUniqueId(), menuUtil.getIdentifier());

            List<String> lore = new ArrayList<>();

            lore.add("&7Identifier: &6" + t.getIdentifier());
            lore.add("&7Description:");
            if (t.getDescription().isEmpty()) {
                lore.add("&6> ");
            } else {
                lore.add("&6> " + t.getDescription());
            }

            String displayname = format("&7Tag: " + t.getTag().get(0));

            getInventory().setItem(4, makeItem(Material.BOOK, format(displayname), lore));

            List<String> c_tag = new ArrayList<>();
            c_tag.add("&7Current: &6" + t.getTag().get(0));
            getInventory().setItem(11, makeItem(Material.NAME_TAG, format("&e&lChange Tag"), c_tag));

            List<String> c_desc = new ArrayList<>();
            c_desc.add("&7Current: &6" + t.getDescription());
            getInventory().setItem(13, makeItem(Material.OAK_SIGN, format("&e&lChange Description"), c_desc));

            List<String> c_delete = new ArrayList<>();
            c_delete.add("&7This cannot be undone!");
            getInventory().setItem(15, makeItem(Material.RED_WOOL, format("&c&lDelete Tag"), c_delete));
        }
        fillEmpty();
    }
}
