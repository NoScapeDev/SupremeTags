package net.noscape.project.supremetags.guis;

import de.tr7zw.nbtapi.*;
import net.noscape.project.supremetags.*;
import net.noscape.project.supremetags.api.events.TagAssignEvent;
import net.noscape.project.supremetags.api.events.TagBuyEvent;
import net.noscape.project.supremetags.api.events.TagResetEvent;
import net.noscape.project.supremetags.handlers.Tag;
import net.noscape.project.supremetags.handlers.menu.*;
import net.noscape.project.supremetags.storage.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;

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
        return format(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.tag-menu-none-categories.title")).replaceAll("%page%", String.valueOf(this.getPage())));
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {

        Player player = (Player) e.getWhoClicked();

        String back = SupremeTags.getInstance().getConfig().getString("gui.strings.back-item");
        String close = SupremeTags.getInstance().getConfig().getString("gui.strings.close-item");
        String next = SupremeTags.getInstance().getConfig().getString("gui.strings.next-item");
        String refresh = SupremeTags.getInstance().getConfig().getString("gui.strings.refresh-item");
        String reset = SupremeTags.getInstance().getConfig().getString("gui.strings.reset-item");
        String active = SupremeTags.getInstance().getConfig().getString("gui.strings.active-item");

        String insufficient = SupremeTags.getInstance().getConfig().getString("messages.insufficient-funds");
        String unlocked = SupremeTags.getInstance().getConfig().getString("messages.tag-unlocked");

        if (e.getCurrentItem().getType().equals(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.glass-material")).toUpperCase()))) {
            e.setCancelled(true);
        }

        if (e.getCurrentItem() == null) {
            e.setCancelled(true);
        }

        ArrayList<String> tag = new ArrayList<>(tags.keySet());

        NBTItem nbt = null;

        if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
            nbt = new NBTItem(e.getCurrentItem());
        }

        if (nbt != null && nbt.hasCustomNbtData() && nbt.hasTag("identifier")) {
            String identifier = nbt.getString("identifier");

            Tag t = SupremeTags.getInstance().getTagManager().getTag(identifier);

            if (t != null) {

                if (!SupremeTags.getInstance().getTagManager().isCost()) {
                    if (!UserData.getActive(player.getUniqueId()).equalsIgnoreCase(identifier) && identifier != null) {
                        if (player.hasPermission(t.getPermission())) {

                            TagAssignEvent tagevent = new TagAssignEvent(player, identifier, false);
                            Bukkit.getPluginManager().callEvent(tagevent);

                            if (tagevent.isCancelled()) return;

                            UserData.setActive(player, tagevent.getTag());

                            super.open();
                            menuUtil.setIdentifier(tagevent.getTag());

                            if (SupremeTags.getInstance().getConfig().getBoolean("settings.gui-messages")) {
                                msgPlayer(player, SupremeTags.getInstance().getConfig().getString("messages.tag-select-message").replaceAll("%identifier%", identifier).replaceAll("%tag%", SupremeTags.getInstance().getTagManager().getTag(identifier).getTag()));
                            }
                        } else {
                            msgPlayer(player, SupremeTags.getInstance().getConfig().getString("messages.locked-tag"));
                        }
                    }
                } else {
                    if (player.hasPermission(t.getPermission())) {
                        if (!UserData.getActive(player.getUniqueId()).equalsIgnoreCase(identifier) && identifier != null) {
                            TagAssignEvent tagevent = new TagAssignEvent(player, identifier, false);
                            Bukkit.getPluginManager().callEvent(tagevent);

                            if (tagevent.isCancelled()) return;

                            UserData.setActive(player, tagevent.getTag());
                            super.open();
                            menuUtil.setIdentifier(tagevent.getTag());

                            if (SupremeTags.getInstance().getConfig().getBoolean("settings.gui-messages")) {
                                msgPlayer(player, SupremeTags.getInstance().getConfig().getString("messages.tag-select-message").replace("%identifier%", identifier).replaceAll("%tag%", SupremeTags.getInstance().getTagManager().getTag(identifier).getTag()));
                            }
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
                            msgPlayer(player, unlocked.replaceAll("%identifier%", t.getIdentifier()).replaceAll("%tag%", SupremeTags.getInstance().getTagManager().getTag(identifier).getTag()));
                            super.open();
                        } else {
                            msgPlayer(player, insufficient.replaceAll("%cost%", String.valueOf(t.getCost())));
                        }
                    }
                }
            }
        }

        if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(format(close))) {
            player.closeInventory();
        }

        if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(format(reset))) {
            if (!SupremeTags.getInstance().getConfig().getBoolean("settings.forced-tag")) {
                TagResetEvent tagEvent = new TagResetEvent(player, false);
                Bukkit.getPluginManager().callEvent(tagEvent);

                if (tagEvent.isCancelled()) return;

                UserData.setActive(player, "None");
                super.open();
                menuUtil.setIdentifier("None");

                if (SupremeTags.getInstance().getConfig().getBoolean("settings.gui-messages")) {
                    msgPlayer(player, SupremeTags.getInstance().getConfig().getString("messages.reset-message"));
                }
            } else {
                TagResetEvent tagEvent = new TagResetEvent(player, false);
                Bukkit.getPluginManager().callEvent(tagEvent);

                if (tagEvent.isCancelled()) return;

                String defaultTag = SupremeTags.getInstance().getConfig().getString("settings.default-tag");

                UserData.setActive(player, defaultTag);
                super.open();
                menuUtil.setIdentifier(defaultTag);

                if (SupremeTags.getInstance().getConfig().getBoolean("settings.gui-messages")) {
                    msgPlayer(player, SupremeTags.getInstance().getConfig().getString("messages.reset-message"));
                }
            }
        }

        if (e.getCurrentItem().getType().equals(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.back-next-material")).toUpperCase()))) {
            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(format(back))) {
                if (page != 0) {
                    page = page - 1;
                    super.open();
                }
            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(format(next))) {
                if (!((index + 1) >= tag.size())) {
                    page = page + 1;
                    super.open();
                }
            }
        }
    }

    @Override
    public void setMenuItems() {
        getTagItems();
        applyLayout();
    }
}