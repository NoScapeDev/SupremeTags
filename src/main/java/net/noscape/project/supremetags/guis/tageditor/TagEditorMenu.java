package net.noscape.project.supremetags.guis.tageditor;

import de.tr7zw.nbtapi.NBTItem;
import net.noscape.project.supremetags.SupremeTags;
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
        tags = SupremeTags.getInstance().getTagManager().getTags();
    }

    @Override
    public String getMenuName() {
        return format(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.tag-editor-menu.title")).replaceAll("%page%", String.valueOf(this.getPage())));
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {

        Player player = (Player) e.getWhoClicked();

        ArrayList<String> tag = new ArrayList<>(tags.keySet());

        String back = SupremeTags.getInstance().getConfig().getString("gui.strings.back-item");
        String close = SupremeTags.getInstance().getConfig().getString("gui.strings.close-item");
        String next = SupremeTags.getInstance().getConfig().getString("gui.strings.next-item");
        String refresh = SupremeTags.getInstance().getConfig().getString("gui.strings.refresh-item");
        String reset = SupremeTags.getInstance().getConfig().getString("gui.strings.reset-item");
        String active = SupremeTags.getInstance().getConfig().getString("gui.strings.active-item");

        if (e.getCurrentItem() == null) return;

        NBTItem nbt = new NBTItem(e.getCurrentItem());

        if (nbt.hasCustomNbtData() && nbt.hasTag("identifier")) {
            String identifier = nbt.getString("identifier");
            menuUtil.setIdentifier(identifier);
            new SpecificTagMenu(SupremeTags.getMenuUtilIdentifier(player, identifier)).open();
        }

        if (e.getCurrentItem().getType().equals(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.close-menu-material")).toUpperCase()))) {
            player.closeInventory();
        }

        if (e.getCurrentItem().getType().equals(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.back-next-material")).toUpperCase()))) {
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
    }


    @Override
    public void setMenuItems() {
        getTagItemsEditor();
        applyEditorLayout();
    }
}
