package net.noscape.project.supremetags.guis;

import net.noscape.project.supremetags.*;
import net.noscape.project.supremetags.handlers.Tag;
import net.noscape.project.supremetags.handlers.menu.*;
import net.noscape.project.supremetags.storage.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

import java.util.*;

import static net.noscape.project.supremetags.utils.Utils.*;

public class TagMenu extends Paged {

    private final Map<String, Tag> tags;
    private final Map<Integer, String> dataItem;

    public TagMenu(MenuUtil menuUtil) {
        super(menuUtil);
        tags = SupremeTags.getInstance().getTagManager().getTags();
        dataItem = SupremeTags.getInstance().getTagManager().getDataItem();
    }

    @Override
    public String getMenuName() {
        return "Tag Menu";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {

        Player player = (Player) e.getWhoClicked();

        ArrayList<String> tag = new ArrayList<>(tags.keySet());

        if (Objects.requireNonNull(e.getCurrentItem()).getType().equals(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.tag-material")).toUpperCase()))) {
            if (!ChatColor.stripColor(Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName()).startsWith("Active")) {
                String identifier = dataItem.get(e.getSlot());
                if (!UserData.getActive(player).equalsIgnoreCase(identifier) && identifier != null) {
                    UserData.setActive(player, identifier);
                    player.closeInventory();
                    super.open();
                    menuUtil.setIdentifier(identifier);
                }
            }
        } else if (e.getCurrentItem().getType().equals(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.close-menu-material")).toUpperCase()))) {
            player.closeInventory();
        } else if (e.getCurrentItem().getType().equals(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.reset-tag-material")).toUpperCase()))) {
            UserData.setActive(player, "None");
            player.closeInventory();
            super.open();
            menuUtil.setIdentifier("None");
        } else if (e.getCurrentItem().getType().equals(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.back-next-material")).toUpperCase()))) {
            if (ChatColor.stripColor(Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName()).equalsIgnoreCase("Back")) {
                if (page != 0) {
                    page = page - 1;
                    super.open();
                }
            } else if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Next")) {
                if (!((index + 1) >= tag.size())) {
                    page = page + 1;
                    super.open();
                }
            }
        }
    }

    @Override
    public void setMenuItems() {

        addBottom();

        ArrayList<String> tag = new ArrayList<>(tags.keySet());

        if (!tag.isEmpty()) {
            for(int i = 0; i < getMaxItems(); i++) {
                index = getMaxItems() * page + i;
                if(index >= tag.size()) break;
                if (tag.get(index) != null){

                    if (menuUtil.getOwner().hasPermission(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + tag.get(index) + ".permission")))) {

                        ItemStack tagItem = new ItemStack(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.tag-material")).toUpperCase()), 1);
                        ItemMeta tagMeta = tagItem.getItemMeta();
                        assert tagMeta != null;

                        tagMeta.setDisplayName(format("&7Tag: " + tags.get(tag.get(index)).getTag()));
                        tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        tagMeta.addItemFlags(ItemFlag.HIDE_DYE);
                        tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                        tagItem.setItemMeta(tagMeta);

                        inventory.addItem(tagItem);
                        dataItem.put(index, tags.get(tag.get(index)).getIdentifier());

                    }
                }
            }
        }
    }
}