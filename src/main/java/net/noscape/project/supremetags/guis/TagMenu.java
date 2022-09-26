package net.noscape.project.supremetags.guis;

import de.tr7zw.nbtapi.*;
import me.arcaniax.hdb.api.*;
import net.noscape.project.supremetags.*;
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

        ArrayList<String> tag = new ArrayList<>(tags.keySet());

        if (Objects.requireNonNull(e.getCurrentItem()).getType().equals(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.tag-material")).toUpperCase()))) {
            if (!ChatColor.stripColor(Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName()).startsWith("Active")) {
                NBTItem nbt = new NBTItem(e.getCurrentItem());
                String identifier = nbt.getString("identifier");
                if (!UserData.getActive(player.getUniqueId()).equalsIgnoreCase(identifier) && identifier != null) {
                    UserData.setActive(player, identifier);
                    player.closeInventory();
                    super.open();
                    menuUtil.setIdentifier(identifier);
                }
            }
        } else if (e.getCurrentItem().getType().equals(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.close-menu-material")).toUpperCase()))) {
            player.closeInventory();
        } else if (e.getCurrentItem().getType().equals(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.reset-tag-material")).toUpperCase()))) {
            if (!SupremeTags.getInstance().getConfig().getBoolean("settings.forced-tag")) {
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

        ArrayList<String> tag = new ArrayList<>(tags.keySet());

        if (!tag.isEmpty()) {
            for (int i = 0; i < getMaxItems(); i++) {
                index = getMaxItems() * page + i;
                if (index >= tag.size()) break;
                if (tag.get(index) != null) {

                    String permission = SupremeTags.getInstance().getConfig().getString("tags." + tag.get(index) + ".permission");

                    String displayname;

                    if (SupremeTags.getInstance().getConfig().getString("tags." + tag.get(index) + ".displayname") != null) {
                        displayname = SupremeTags.getInstance().getConfig().getString("tags." + tag.get(index) + ".displayname").replaceAll("%tag%", tags.get(tag.get(index)).getTag());
                    } else {
                        displayname = format("&7Tag: " + tags.get(tag.get(index)).getTag());
                    }

                    String material;

                    if (SupremeTags.getInstance().getConfig().getString("tags." + tag.get(index) + ".display-item") != null) {
                        material = SupremeTags.getInstance().getConfig().getString("tags." + tag.get(index) + ".display-item");
                    } else {
                        material = "NAME_TAG";
                    }

                    HeadDatabaseAPI api = new HeadDatabaseAPI();

                    assert permission != null;

                    // toggle if they don't have permission
                    if (menuUtil.getOwner().hasPermission(permission) && !permission.equalsIgnoreCase("none")) {
                        if (UserData.getActive(menuUtil.getOwner().getUniqueId()).equalsIgnoreCase(tag.get(index))) {

                            if (material.contains("hdb-")) {

                                int id = Integer.parseInt(material.replaceAll("hdb-", ""));

                                ItemStack tagItem = api.getItemHead(String.valueOf(id));
                                ItemMeta tagMeta = tagItem.getItemMeta();
                                assert tagMeta != null;

                                NBTItem nbt = new NBTItem(tagItem);

                                nbt.setString("identifier", tags.get(tag.get(index)).getIdentifier());

                                tagMeta.setDisplayName(format(displayname));

                                if (SupremeTags.getInstance().getConfig().getBoolean("settings.active-tag-glow")) {
                                    nbt.getItem().addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                                }

                                tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                                tagMeta.addItemFlags(ItemFlag.HIDE_DYE);
                                tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                                tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                                // set lore
                                ArrayList<String> lore = (ArrayList<String>) SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.lore");
                                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%description%", format(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + tag.get(index) + ".description")))));
                                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%identifier%", tags.get(tag.get(index)).getIdentifier()));
                                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tag%", tags.get(tag.get(index)).getTag()));

                                tagMeta.setLore(color(lore));

                                nbt.getItem().setItemMeta(tagMeta);

                                nbt.setString("identifier", tags.get(tag.get(index)).getIdentifier());
                                inventory.addItem(nbt.getItem());
                            } else {
                                ItemStack tagItem = new ItemStack(Material.valueOf(material.toUpperCase()), 1);
                                ItemMeta tagMeta = tagItem.getItemMeta();
                                assert tagMeta != null;

                                NBTItem nbt = new NBTItem(tagItem);

                                nbt.setString("identifier", tags.get(tag.get(index)).getIdentifier());

                                tagMeta.setDisplayName(format(displayname));
                                tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                                tagMeta.addItemFlags(ItemFlag.HIDE_DYE);
                                tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                                tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                                // set lore
                                ArrayList<String> lore = (ArrayList<String>) SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.lore");
                                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%description%", format(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + tag.get(index) + ".description")))));
                                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%identifier%", tags.get(tag.get(index)).getIdentifier()));
                                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tag%", tags.get(tag.get(index)).getTag()));

                                tagMeta.setLore(color(lore));

                                nbt.getItem().setItemMeta(tagMeta);

                                nbt.setString("identifier", tags.get(tag.get(index)).getIdentifier());
                                inventory.addItem(nbt.getItem());
                            }
                        } else {
                            if (material.contains("hdb-")) {

                                int id = Integer.parseInt(material.replaceAll("hdb-", ""));

                                ItemStack tagItem = api.getItemHead(String.valueOf(id));
                                ItemMeta tagMeta = tagItem.getItemMeta();
                                assert tagMeta != null;

                                NBTItem nbt = new NBTItem(tagItem);

                                nbt.setString("identifier", tags.get(tag.get(index)).getIdentifier());

                                tagMeta.setDisplayName(format(displayname));

                                tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                                tagMeta.addItemFlags(ItemFlag.HIDE_DYE);
                                tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                                tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                                // set lore
                                ArrayList<String> lore = (ArrayList<String>) SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.lore");
                                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%description%", format(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + tag.get(index) + ".description")))));
                                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%identifier%", tags.get(tag.get(index)).getIdentifier()));
                                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tag%", tags.get(tag.get(index)).getTag()));

                                tagMeta.setLore(color(lore));

                                nbt.getItem().setItemMeta(tagMeta);

                                nbt.setString("identifier", tags.get(tag.get(index)).getIdentifier());
                                inventory.addItem(nbt.getItem());
                            } else {
                                ItemStack tagItem = new ItemStack(Material.valueOf(material.toUpperCase()), 1);
                                ItemMeta tagMeta = tagItem.getItemMeta();
                                assert tagMeta != null;

                                NBTItem nbt = new NBTItem(tagItem);

                                nbt.setString("identifier", tags.get(tag.get(index)).getIdentifier());

                                tagMeta.setDisplayName(format(displayname));
                                tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                                tagMeta.addItemFlags(ItemFlag.HIDE_DYE);
                                tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                                tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                                // set lore
                                ArrayList<String> lore = (ArrayList<String>) SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.lore");
                                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%description%", format(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + tag.get(index) + ".description")))));
                                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%identifier%", tags.get(tag.get(index)).getIdentifier()));
                                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tag%", tags.get(tag.get(index)).getTag()));

                                tagMeta.setLore(color(lore));

                                nbt.getItem().setItemMeta(tagMeta);

                                nbt.setString("identifier", tags.get(tag.get(index)).getIdentifier());
                                inventory.addItem(nbt.getItem());
                            }
                        }


                        // if permission == none
                    } else if (!menuUtil.getOwner().hasPermission(permission) && permission.equalsIgnoreCase("none")) {
                        if (UserData.getActive(menuUtil.getOwner().getUniqueId()).equalsIgnoreCase(tag.get(index))) {

                            if (material.contains("hdb-")) {

                                int id = Integer.parseInt(material.replaceAll("hdb-", ""));

                                ItemStack tagItem = api.getItemHead(String.valueOf(id));
                                ItemMeta tagMeta = tagItem.getItemMeta();
                                assert tagMeta != null;

                                NBTItem nbt = new NBTItem(tagItem);

                                nbt.setString("identifier", tags.get(tag.get(index)).getIdentifier());

                                tagMeta.setDisplayName(format(displayname));

                                if (SupremeTags.getInstance().getConfig().getBoolean("settings.active-tag-glow")) {
                                    nbt.getItem().addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                                }

                                tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                                tagMeta.addItemFlags(ItemFlag.HIDE_DYE);
                                tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                                tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                                // set lore
                                ArrayList<String> lore = (ArrayList<String>) SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.lore");
                                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%description%", format(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + tag.get(index) + ".description")))));
                                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%identifier%", tags.get(tag.get(index)).getIdentifier()));
                                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tag%", tags.get(tag.get(index)).getTag()));

                                tagMeta.setLore(color(lore));

                                nbt.getItem().setItemMeta(tagMeta);

                                nbt.setString("identifier", tags.get(tag.get(index)).getIdentifier());
                                inventory.addItem(nbt.getItem());
                            } else {
                                ItemStack tagItem = new ItemStack(Material.valueOf(material.toUpperCase()), 1);
                                ItemMeta tagMeta = tagItem.getItemMeta();
                                assert tagMeta != null;

                                NBTItem nbt = new NBTItem(tagItem);

                                nbt.setString("identifier", tags.get(tag.get(index)).getIdentifier());

                                tagMeta.setDisplayName(format(displayname));
                                tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                                tagMeta.addItemFlags(ItemFlag.HIDE_DYE);
                                tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                                tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                                // set lore
                                ArrayList<String> lore = (ArrayList<String>) SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.lore");
                                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%description%", format(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + tag.get(index) + ".description")))));
                                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%identifier%", tags.get(tag.get(index)).getIdentifier()));
                                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tag%", tags.get(tag.get(index)).getTag()));

                                tagMeta.setLore(color(lore));

                                nbt.getItem().setItemMeta(tagMeta);

                                nbt.setString("identifier", tags.get(tag.get(index)).getIdentifier());
                                inventory.addItem(nbt.getItem());
                            }
                        } else {
                            if (material.contains("hdb-")) {

                                int id = Integer.parseInt(material.replaceAll("hdb-", ""));

                                ItemStack tagItem = api.getItemHead(String.valueOf(id));
                                ItemMeta tagMeta = tagItem.getItemMeta();
                                assert tagMeta != null;

                                NBTItem nbt = new NBTItem(tagItem);

                                nbt.setString("identifier", tags.get(tag.get(index)).getIdentifier());

                                tagMeta.setDisplayName(format(displayname));

                                tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                                tagMeta.addItemFlags(ItemFlag.HIDE_DYE);
                                tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                                tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                                // set lore
                                ArrayList<String> lore = (ArrayList<String>) SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.lore");
                                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%description%", format(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + tag.get(index) + ".description")))));
                                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%identifier%", tags.get(tag.get(index)).getIdentifier()));
                                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tag%", tags.get(tag.get(index)).getTag()));

                                tagMeta.setLore(color(lore));

                                nbt.getItem().setItemMeta(tagMeta);

                                nbt.setString("identifier", tags.get(tag.get(index)).getIdentifier());
                                inventory.addItem(nbt.getItem());
                            } else {
                                ItemStack tagItem = new ItemStack(Material.valueOf(material.toUpperCase()), 1);
                                ItemMeta tagMeta = tagItem.getItemMeta();
                                assert tagMeta != null;

                                NBTItem nbt = new NBTItem(tagItem);

                                nbt.setString("identifier", tags.get(tag.get(index)).getIdentifier());

                                tagMeta.setDisplayName(format(displayname));
                                tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                                tagMeta.addItemFlags(ItemFlag.HIDE_DYE);
                                tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                                tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                                // set lore
                                ArrayList<String> lore = (ArrayList<String>) SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.lore");
                                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%description%", format(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + tag.get(index) + ".description")))));
                                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%identifier%", tags.get(tag.get(index)).getIdentifier()));
                                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tag%", tags.get(tag.get(index)).getTag()));

                                tagMeta.setLore(color(lore));

                                nbt.getItem().setItemMeta(tagMeta);

                                nbt.setString("identifier", tags.get(tag.get(index)).getIdentifier());
                                inventory.addItem(nbt.getItem());
                            }
                        }
                    }
                }
            }
        }
    }
}