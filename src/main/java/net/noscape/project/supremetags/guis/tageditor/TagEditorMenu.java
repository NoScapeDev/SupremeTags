package net.noscape.project.supremetags.guis.tageditor;

import de.tr7zw.nbtapi.NBTItem;
import net.noscape.project.supremetags.SupremeTagsPremium;
import net.noscape.project.supremetags.handlers.Tag;
import net.noscape.project.supremetags.handlers.menu.MenuUtil;
import net.noscape.project.supremetags.handlers.menu.Paged;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import static net.noscape.project.supremetags.utils.Utils.format;

public class TagEditorMenu extends Paged {

    private final Map<String, Tag> tags;

    public TagEditorMenu(MenuUtil menuUtil) {
        super(menuUtil);
        tags = SupremeTagsPremium.getInstance().getTagManager().getTags();
    }

    @Override
    public String getMenuName() {
        return format(Objects.requireNonNull(SupremeTagsPremium.getInstance().getConfig().getString("gui.tag-editor-menu.title")).replaceAll("%page%", String.valueOf(this.getPage())));
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {

        Player player = (Player) e.getWhoClicked();

        ArrayList<String> tag = new ArrayList<>(tags.keySet());

        String back = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.back.displayname");
        String close = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.close.displayname");
        String next = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.next.displayname");
        String reset = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.reset.displayname");
        String active = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.active.displayname");

        NBTItem nbt = new NBTItem(e.getCurrentItem());

        if (nbt.hasTag("identifier")) {
            String identifier = nbt.getString("identifier");
            menuUtil.setIdentifier(identifier);
            new SpecificTagMenu(SupremeTagsPremium.getMenuUtilIdentifier(player, identifier)).open();
        }

        if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(format(close))) {
            player.closeInventory();
        }

        if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(format(back))) {
            if (page != 0) {
                page = page - 1;
                super.open();
            }
        } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(format(next))) {
            if (!((index + 1) >= tag.size())) {
                if (getCurrentItemsOnPage() == 36) {
                    page = page + 1;
                    super.open();
                }
            }
        }
    }


    @Override
    public void setMenuItems() {
        getTagItemsEditor();
        applyEditorLayout();
    }
}
