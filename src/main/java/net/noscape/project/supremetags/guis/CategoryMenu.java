package net.noscape.project.supremetags.guis;

import de.tr7zw.nbtapi.*;
import net.noscape.project.supremetags.*;
import net.noscape.project.supremetags.api.events.TagAssignEvent;
import net.noscape.project.supremetags.api.events.TagBuyEvent;
import net.noscape.project.supremetags.api.events.TagResetEvent;
import net.noscape.project.supremetags.guis.personaltags.PersonalTagsMenu;
import net.noscape.project.supremetags.handlers.Tag;
import net.noscape.project.supremetags.handlers.menu.*;
import net.noscape.project.supremetags.storage.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;

import java.util.*;

import static net.noscape.project.supremetags.utils.Utils.*;
import static net.noscape.project.supremetags.utils.Utils.msgPlayer;

public class CategoryMenu extends Paged {

    private final Map<String, Tag> tags;
    private final Map<Integer, String> dataItem;

    public CategoryMenu(MenuUtil menuUtil) {
        super(menuUtil);
        tags = SupremeTagsPremium.getInstance().getTagManager().getTags();
        dataItem = SupremeTagsPremium.getInstance().getTagManager().getDataItem();
    }

    @Override
    public String getMenuName() {
        return format(Objects.requireNonNull(SupremeTagsPremium.getInstance().getCategoryManager().getCatConfig().getString("categories." + menuUtil.getCategory() + ".title")).replaceAll("%page%", String.valueOf(this.getPage())));
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {

        Player player = (Player) e.getWhoClicked();

        String back = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.back.displayname");
        String close = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.close.displayname");
        String next = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.next.displayname");
        String reset = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.reset.displayname");
        String ptags = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.personal-tags.displayname");

        String insufficient = SupremeTagsPremium.getInstance().getConfig().getString("messages.insufficient-funds");
        String unlocked = SupremeTagsPremium.getInstance().getConfig().getString("messages.tag-unlocked");

        if (e.getCurrentItem().getType().equals(Material.valueOf(Objects.requireNonNull(SupremeTagsPremium.getInstance().getConfig().getString("gui.items.glass.material")).toUpperCase()))) {
            e.setCancelled(true);
        }

        ArrayList<String> tag = new ArrayList<>(tags.keySet());

        NBTItem nbt = new NBTItem(e.getCurrentItem());

        if (nbt.hasTag("identifier")) {
            String identifier = nbt.getString("identifier");

            Tag t = SupremeTagsPremium.getInstance().getTagManager().getTag(identifier);
            boolean isCostCategory = SupremeTagsPremium.getInstance().getCategoryManager().getCatConfig().getBoolean("categories." + menuUtil.getCategory() + ".cost-category");

            if (!isCostCategory) {
                if (!UserData.getActive(player.getUniqueId()).equalsIgnoreCase(identifier) && identifier != null) {
                    if (player.hasPermission(t.getPermission())) {

                        TagAssignEvent tagevent = new TagAssignEvent(player, identifier, false);
                        Bukkit.getPluginManager().callEvent(tagevent);

                        if (tagevent.isCancelled()) return;

                        UserData.setActive(player, tagevent.getTag());

                        super.open();
                        menuUtil.setIdentifier(tagevent.getTag());

                        if (SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.gui-messages")) {
                            msgPlayer(player, SupremeTagsPremium.getInstance().getConfig().getString("messages.tag-select-message").replace("%identifier%", identifier));
                        }
                    } else {
                        msgPlayer(player, SupremeTagsPremium.getInstance().getConfig().getString("messages.locked-tag"));
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

                        if (SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.gui-messages")) {
                            msgPlayer(player, SupremeTagsPremium.getInstance().getConfig().getString("messages.tag-select-message").replace("%identifier%", identifier));
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
                        msgPlayer(player, unlocked.replaceAll("%identifier%", t.getIdentifier()));
                        super.open();
                    } else {
                        msgPlayer(player, insufficient.replaceAll("%cost%", String.valueOf(t.getCost())));
                    }
                }
            }
        }

        if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(format(close))) {
            player.closeInventory();
        }

        if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(format(ptags))) {
            new PersonalTagsMenu(SupremeTagsPremium.getMenuUtil(player)).open();
        }

        if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(format(reset))) {
            if (!SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.forced-tag")) {
                TagResetEvent tagEvent = new TagResetEvent(player, false);
                Bukkit.getPluginManager().callEvent(tagEvent);

                if (tagEvent.isCancelled()) return;

                UserData.setActive(player, "None");
                super.open();
                menuUtil.setIdentifier("None");

                if (SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.gui-messages")) {
                    msgPlayer(player, SupremeTagsPremium.getInstance().getConfig().getString("messages.reset-message"));
                }
            } else {
                TagResetEvent tagEvent = new TagResetEvent(player, false);
                Bukkit.getPluginManager().callEvent(tagEvent);

                if (tagEvent.isCancelled()) return;

                String defaultTag = SupremeTagsPremium.getInstance().getConfig().getString("settings.default-tag");

                UserData.setActive(player, defaultTag);
                super.open();
                menuUtil.setIdentifier(defaultTag);

                if (SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.gui-messages")) {
                    msgPlayer(player, SupremeTagsPremium.getInstance().getConfig().getString("messages.reset-message"));
                }
            }
        }

        if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(format(back))) {
            if (page != 0) {
                page = page - 1;
                super.open();
            } else {
                new MainMenu(SupremeTagsPremium.getMenuUtil(player)).open();
            }
        } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(format(next))) {
            if (!((index + 1) >= tag.size())) {
                page = page + 1;
                super.open();
            }
        }
    }

    @Override
    public void setMenuItems() {
        applyLayout();
        getTagItemsCategory();
    }
}