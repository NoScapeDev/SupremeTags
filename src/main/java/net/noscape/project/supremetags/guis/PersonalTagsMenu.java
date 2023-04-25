package net.noscape.project.supremetags.guis;

import de.tr7zw.nbtapi.NBTItem;
import net.noscape.project.supremetags.SupremeTags;
import net.noscape.project.supremetags.api.events.TagAssignEvent;
import net.noscape.project.supremetags.api.events.TagBuyEvent;
import net.noscape.project.supremetags.api.events.TagResetEvent;
import net.noscape.project.supremetags.handlers.SetupTag;
import net.noscape.project.supremetags.handlers.Tag;
import net.noscape.project.supremetags.handlers.menu.MenuUtil;
import net.noscape.project.supremetags.handlers.menu.Paged;
import net.noscape.project.supremetags.storage.UserData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static net.noscape.project.supremetags.utils.Utils.*;
import static net.noscape.project.supremetags.utils.Utils.msgPlayer;

public class PersonalTagsMenu extends Paged {

    private final List<Tag> tags;

    public PersonalTagsMenu(MenuUtil menuUtil) {
        super(menuUtil);
        tags = SupremeTags.getInstance().getPlayerManager().getPlayerTags(menuUtil.getOwner().getUniqueId());
    }

    @Override
    public String getMenuName() {
        return "Personal Tags";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        if (e.getCurrentItem().getType().equals(Material.NAME_TAG)) {
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
        } else if (e.getCurrentItem().getType().equals(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.back-next-material")).toUpperCase()))) {
            if (ChatColor.stripColor(Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName()).equalsIgnoreCase("Back")) {
                if (page != 0) {
                    page = page - 1;
                    super.open();
                }
            } else if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Next")) {
                if (!((index + 1) >= tags.size())) {
                    page = page + 1;
                    super.open();
                }
            }
        } else if (e.getCurrentItem().getType().equals(Material.BOOK)) {
            if (ChatColor.stripColor(Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName()).equalsIgnoreCase("Create a Tag")) {
                // create a tag

                if (tags.size() < SupremeTags.getInstance().getConfig().getInt("settings.personal-limit")) {
                    if (!SupremeTags.getInstance().getSetupList().containsKey(player)) {
                        player.closeInventory();

                        SetupTag setup = new SetupTag(1);
                        SupremeTags.getInstance().getSetupList().put(player, setup);

                        msgPlayer(player, "&6&lStage 1: &7Enter a name for the tag. &6&o(type in normal chat)");
                    }
                } else {
                    msgPlayer(player, "&8[&6&lTags&8] &7The tag limit has been reached!");
                }
            }
        }
    }

    @Override
    public void setMenuItems() {
        applyPTLayout();

        if (!tags.isEmpty()) {
            int maxItemsPerPage = 36;
            int startIndex = page * maxItemsPerPage;
            int endIndex = Math.min(startIndex + maxItemsPerPage, tags.size());

            for (int i = startIndex; i < endIndex; i++) {
                Tag t = tags.get(i);
                if (t == null) continue;

                ItemStack tagItem = new ItemStack(Material.NAME_TAG, 1);
                ItemMeta tagMeta = tagItem.getItemMeta();
                assert tagMeta != null;

                NBTItem nbt = new NBTItem(tagItem);

                nbt.setString("identifier", t.getIdentifier());

                tagMeta.setDisplayName(format("&7Tag: " + t.getTag()));
                tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                tagMeta.addItemFlags(ItemFlag.HIDE_DYE);
                tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                // set lore
                ArrayList<String> lore = (ArrayList<String>) SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.lore");
                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%description%", t.getDescription()));
                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%identifier%", t.getIdentifier()));
                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tag%", t.getTag()));

                tagMeta.setLore(color(lore));

                nbt.getItem().setItemMeta(tagMeta);

                nbt.setString("identifier", t.getIdentifier());
                inventory.addItem(nbt.getItem());
            }
        } else {
            inventory.setItem(22, makeItem(Material.ANVIL, "&cYou don't have any personal tags!"));
        }
    }

    public List<Tag> getTags() {
        return tags;
    }
}
