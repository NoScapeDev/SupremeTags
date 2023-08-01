package net.noscape.project.supremetags.guis.personaltags;

import de.tr7zw.nbtapi.NBTItem;
import net.noscape.project.supremetags.SupremeTagsPremium;
import net.noscape.project.supremetags.api.events.TagAssignEvent;
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
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.noscape.project.supremetags.utils.Utils.*;
import static net.noscape.project.supremetags.utils.Utils.msgPlayer;

public class PersonalTagsMenu extends Paged {

    private final List<Tag> tags;

    public PersonalTagsMenu(MenuUtil menuUtil) {
        super(menuUtil);
        tags = SupremeTagsPremium.getInstance().getPlayerManager().getPlayerTags(menuUtil.getOwner().getUniqueId());
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

        String back = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.back.displayname");
        String close = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.close.displayname");
        String next = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.next.displayname");
        String reset = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.reset.displayname");
        String createtag = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.create-tag.displayname");

        String stage_one = SupremeTagsPremium.getInstance().getConfig().getString("messages.stages.stage-1");

        if (e.getCurrentItem().getType().equals(Material.NAME_TAG)) {
            if (e.getClick() == ClickType.LEFT) {
                NBTItem nbt = new NBTItem(e.getCurrentItem());
                String identifier = nbt.getString("identifier");

                if (!UserData.getActive(player.getUniqueId()).equalsIgnoreCase(identifier) && identifier != null) {
                    TagAssignEvent tagevent = new TagAssignEvent(player, identifier, false);
                    Bukkit.getPluginManager().callEvent(tagevent);

                    if (tagevent.isCancelled()) return;

                    UserData.setActive(player, tagevent.getTag());
                    player.closeInventory();
                    super.open();
                    menuUtil.setIdentifier(tagevent.getTag());
                }
            } else if (e.getClick() == ClickType.RIGHT) {
                NBTItem nbt = new NBTItem(e.getCurrentItem());
                String identifier = nbt.getString("identifier");
                menuUtil.setIdentifier(identifier);
                new PersonalTagEditorMenu(SupremeTagsPremium.getMenuUtilIdentifier(menuUtil.getOwner(), identifier)).open();
            }
        }

        if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equalsIgnoreCase(format(close))) {
            player.closeInventory();
        }

        if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equalsIgnoreCase(format(reset))) {
            TagResetEvent tagEvent = new TagResetEvent(player, false);
            Bukkit.getPluginManager().callEvent(tagEvent);

            if (tagEvent.isCancelled()) return;

            UserData.setActive(player, "None");
            player.closeInventory();
            super.open();
            menuUtil.setIdentifier("None");
        }


        if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(format(back))) {
            if (page != 0) {
                page = page - 1;
                super.open();
            }
        } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(format(next))) {
            if (!((index + 1) >= tags.size())) {
                page = page + 1;
                super.open();
            }
        }

        if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equalsIgnoreCase(format(createtag))) {
            boolean reachedLimit = false;
            int limit;

            for (String str : SupremeTagsPremium.getInstance().getConfig().getConfigurationSection("settings.personal-tags.limits").getKeys(false)) {
                if (player.hasPermission("supremetags.mytags.limit." + str)) {
                    limit = SupremeTagsPremium.getInstance().getConfig().getInt("settings.personal-tags.limits." + str);

                    if (tags.size() >= limit) {
                        reachedLimit = true;
                    }

                } else {
                    reachedLimit = true;
                }
                break;
            }

            if (!reachedLimit) {
                if (!SupremeTagsPremium.getInstance().getSetupList().containsKey(player)) {
                    player.closeInventory();

                    SetupTag setup = new SetupTag(1);
                    SupremeTagsPremium.getInstance().getSetupList().put(player, setup);

                    msgPlayer(player, stage_one);
                }
            } else {
                msgPlayer(player, "&8[&6&lTags&8] &7The tag limit has been reached!");
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

                tagMeta.setDisplayName(format("&7Tag: " + t.getTag().get(0)));
                tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                tagMeta.addItemFlags(ItemFlag.HIDE_DYE);
                tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                // set lore
                ArrayList<String> lore = new ArrayList<>();
                if (UserData.getActive(menuUtil.getOwner().getUniqueId()).equalsIgnoreCase(t.getIdentifier())) {
                    lore.add("&7Right-Click to edit this tag.");
                    lore.add("&7");
                    lore.add("&cYou have this tag selected!");
                } else {
                    lore.add("&7Right-Click to edit this tag.");
                    lore.add("&7");
                    lore.add("&eLeft-Click to Assign!");
                }

                tagMeta.setLore(color(lore));

                nbt.getItem().setItemMeta(tagMeta);

                nbt.setString("identifier", t.getIdentifier());
                inventory.addItem(nbt.getItem());
            }
        } else {
            inventory.setItem(22, makeItem(Material.ANVIL, "&cYou don't have any personal tags!", 0));
        }
    }

    public List<Tag> getTags() {
        return tags;
    }
}
