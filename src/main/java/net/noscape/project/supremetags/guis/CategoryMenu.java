package net.noscape.project.supremetags.guis;

import de.tr7zw.nbtapi.*;
import me.arcaniax.hdb.api.*;
import net.noscape.project.supremetags.*;
import net.noscape.project.supremetags.api.events.TagAssignEvent;
import net.noscape.project.supremetags.api.events.TagBuyEvent;
import net.noscape.project.supremetags.api.events.TagResetEvent;
import net.noscape.project.supremetags.handlers.Tag;
import net.noscape.project.supremetags.handlers.menu.*;
import net.noscape.project.supremetags.storage.*;
import org.bukkit.*;
import org.bukkit.enchantments.*;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

import java.util.*;

import static net.noscape.project.supremetags.utils.Utils.*;
import static net.noscape.project.supremetags.utils.Utils.msgPlayer;

public class CategoryMenu extends Paged {

    private final Map<String, Tag> tags;
    private final Map<Integer, String> dataItem;

    public CategoryMenu(MenuUtil menuUtil) {
        super(menuUtil);
        tags = SupremeTags.getInstance().getTagManager().getTags();
        dataItem = SupremeTags.getInstance().getTagManager().getDataItem();
    }

    @Override
    public String getMenuName() {
        return format(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("categories." + menuUtil.getCategory() + ".title")).replaceAll("%page%", String.valueOf(this.getPage())));
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
                NBTItem nbt = new NBTItem(e.getCurrentItem());
                String identifier = nbt.getString("identifier");

                Tag t = SupremeTags.getInstance().getTagManager().getTag(identifier);

                if (!SupremeTags.getInstance().getTagManager().isCost()) {
                    if (!UserData.getActive(player.getUniqueId()).equalsIgnoreCase(identifier) && identifier != null) {

                        TagAssignEvent tagevent = new TagAssignEvent(player, identifier,false);
                        Bukkit.getPluginManager().callEvent(tagevent);

                        if (tagevent.isCancelled()) return;

                        UserData.setActive(player, tagevent.getTag());
                        player.closeInventory();
                        super.open();
                        menuUtil.setIdentifier(tagevent.getTag());
                    }
                } else {
                    if (player.hasPermission(t.getPermission())) {
                        if (!UserData.getActive(player.getUniqueId()).equalsIgnoreCase(identifier) && identifier != null) {
                            TagAssignEvent tagevent = new TagAssignEvent(player, identifier,false);
                            Bukkit.getPluginManager().callEvent(tagevent);

                            if (tagevent.isCancelled()) return;


                            UserData.setActive(player, tagevent.getTag());
                            player.closeInventory();
                            super.open();
                            menuUtil.setIdentifier(tagevent.getTag());
                        }
                    } else {
                        double cost = t.getCost();

                        // check if they have the right amount of money to buy etc....
                        if (hasAmount(player, cost)) {
                            // give them the tag

                            TagBuyEvent tagevent = new TagBuyEvent(player, identifier, cost, false);
                            Bukkit.getPluginManager().callEvent(tagevent);

                            if (tagevent.isCancelled()) return;

                            take(player, cost);
                            addPerm(player, t.getPermission());
                            msgPlayer(player, "&8[&6Tags&8] &7You have unlocked the tag: &6" + t.getIdentifier());
                            player.closeInventory();
                            super.open();
                        } else {
                            msgPlayer(player, "&cInsufficient funds. &7You need &c$" + t.getCost() + " &7to get this tag.");
                        }
                    }
                }
            }
        } else if (e.getCurrentItem().getType().equals(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.close-menu-material")).toUpperCase()))) {
            player.closeInventory();
        } else if (e.getCurrentItem().getType().equals(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.reset-tag-material")).toUpperCase()))) {
            if (!SupremeTags.getInstance().getConfig().getBoolean("settings.forced-tag")) {
                TagResetEvent tagEvent = new TagResetEvent(player, false);
                Bukkit.getPluginManager().callEvent(tagEvent);

                if (tagEvent.isCancelled()) return;

                UserData.setActive(player, "None");
                player.closeInventory();
                super.open();
                menuUtil.setIdentifier("None");
            }
        } else if (e.getCurrentItem().getType().equals(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.refresh-material")).toUpperCase()))) {
                player.closeInventory();
                super.open();
        } else if (e.getCurrentItem().getType().equals(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.back-next-material")).toUpperCase()))) {
            if (ChatColor.stripColor(Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName()).equalsIgnoreCase("Back")) {
                if (page != 0) {
                    page = page - 1;
                    super.open();
                } else {
                    player.closeInventory();
                    new MainMenu(SupremeTags.getMenuUtil(player)).open();
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

        applyLayout();

        if (SupremeTags.getInstance().getTagManager().isCost()) {
            getTagItemsCostCategory();
        } else {
            getTagItemsNoneCostCategory();
        }
    }
}