package net.noscape.project.supremetags.handlers.menu;

import de.tr7zw.nbtapi.NBTItem;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import net.noscape.project.supremetags.*;
import net.noscape.project.supremetags.handlers.Tag;
import net.noscape.project.supremetags.storage.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

import static net.noscape.project.supremetags.utils.Utils.*;

public abstract class Paged extends Menu {

    protected int page = 0;
    protected int maxItems = 35;
    protected int index = 0;

    private final int tagsCount;

    private int currentItemsOnPage = 0;

    public Paged(MenuUtil menuUtil) {
        super(menuUtil);

        Map<String, Tag> tags = SupremeTagsPremium.getInstance().getTagManager().getTags();
        ArrayList<Tag> tag = new ArrayList<>(tags.values());

        tagsCount = tag.size();
    }

    public void applyEditorLayout() {

        String back = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.back.displayname");
        String close = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.close.displayname");
        String next = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.next.displayname");

        inventory.setItem(48, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTagsPremium.getInstance().getConfig().getString("gui.items.back.material")).toUpperCase()), back, 0));

        inventory.setItem(49, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTagsPremium.getInstance().getConfig().getString("gui.items.close.material")).toUpperCase()), close, 0));

        if (getCurrentItemsOnPage() == 36 && tagsCount > 36) {
            inventory.setItem(50, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTagsPremium.getInstance().getConfig().getString("gui.items.next.material")).toUpperCase()), next, 0));
        }

        for (int i = 36; i <= 44; i++) {
            inventory.setItem(i, makeItem(Material.GRAY_STAINED_GLASS_PANE, "&6", 0));
        }
    }

    public void applyLayout() {
        if (SupremeTagsPremium.getInstance().getLayout().equalsIgnoreCase("FULL")) {
            if (SupremeTagsPremium.getInstance().getConfig().getBoolean("gui.items.glass.enable")) {
                for (int i = 36; i <= 44; i++) {
                    String item_material = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.glass.material");
                    String item_displayname = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.glass.displayname");
                    int item_custom_model_data = SupremeTagsPremium.getInstance().getConfig().getInt("gui.items.glass.custom-model-data");

                    assert item_material != null;
                    inventory.setItem(i, makeItem(Material.valueOf(item_material.toUpperCase()), item_displayname, item_custom_model_data));
                }
            }
        } else if (SupremeTagsPremium.getInstance().getLayout().equalsIgnoreCase("BORDER")) {
            for (int i = 0; i < 54; i++) {
                if (inventory.getItem(i) == null) {
                    if (i < 9 || i >= 45 || i % 9 == 0 || (i + 1) % 9 == 0) {
                        String item_material = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.glass.material");
                        String item_displayname = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.glass.displayname");
                        int item_custom_model_data = SupremeTagsPremium.getInstance().getConfig().getInt("gui.items.glass.custom-model-data");

                        assert item_material != null;
                        inventory.setItem(i, makeItem(Material.valueOf(item_material.toUpperCase()), item_displayname, item_custom_model_data));
                    }
                }
            }
        }

        for (String str : SupremeTagsPremium.getInstance().getConfig().getConfigurationSection("gui.items").getKeys(false)) {
            boolean enabled = SupremeTagsPremium.getInstance().getConfig().getBoolean("gui.items." + str + ".enable");
            if (enabled && !str.equalsIgnoreCase("glass") && !str.equalsIgnoreCase("create-tag")) {

                if (!SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.personal-tags.enable") && str.equalsIgnoreCase("personal-tags")) continue;

                if (str.equalsIgnoreCase("next") && currentItemsOnPage < 36 && SupremeTagsPremium.getInstance().getConfig().getString("settings.layout-type").equalsIgnoreCase("FULL")) continue;

                if (str.equalsIgnoreCase("next") && currentItemsOnPage < 28 && SupremeTagsPremium.getInstance().getConfig().getString("settings.layout-type").equalsIgnoreCase("BORDER")) continue;

                String item_material = SupremeTagsPremium.getInstance().getConfig().getString("gui.items." + str + ".material");
                String item_displayname = SupremeTagsPremium.getInstance().getConfig().getString("gui.items." + str + ".displayname");
                int item_custom_model_data = SupremeTagsPremium.getInstance().getConfig().getInt("gui.items." + str + ".custom-model-data");
                int item_slot = SupremeTagsPremium.getInstance().getConfig().getInt("gui.items." + str + ".slot");
                List<String> item_lore = SupremeTagsPremium.getInstance().getConfig().getStringList("gui.items." + str + ".lore");

                ItemStack item;
                ItemMeta itemMeta;

                if (item_material.contains("hdb-")) {
                    int id = Integer.parseInt(item_material.replaceAll("hdb-", ""));
                    HeadDatabaseAPI api = new HeadDatabaseAPI();
                    item = api.getItemHead(String.valueOf(id));
                    itemMeta = item.getItemMeta();
                } else if (item_material.contains("basehead-")) {
                    String id = item_material.replaceAll("basehead-", "");
                    item = createSkull(id);
                    itemMeta = item.getItemMeta();
                } else {
                    item = new ItemStack(Material.valueOf(item_material.toUpperCase()), 1);
                    itemMeta = item.getItemMeta();
                }

                if (item_custom_model_data > 0) {
                    if (itemMeta != null)
                        itemMeta.setCustomModelData(item_custom_model_data);
                }

                item_displayname = item_displayname.replace("%player%", menuUtil.getOwner().getName());
                item_displayname = item_displayname.replace("%identifier%", UserData.getActive(menuUtil.getOwner().getUniqueId()));
                if (SupremeTagsPremium.getInstance().getTagManager().getTag(UserData.getActive(menuUtil.getOwner().getUniqueId())) != null) {
                    if (SupremeTagsPremium.getInstance().getTagManager().getTag(UserData.getActive(menuUtil.getOwner().getUniqueId())).getCurrentTag() != null) {
                        item_displayname = item_displayname.replace("%tag%", SupremeTagsPremium.getInstance().getTagManager().getTag(UserData.getActive(menuUtil.getOwner().getUniqueId())).getCurrentTag());
                    } else {
                        item_displayname = item_displayname.replace("%tag%", SupremeTagsPremium.getInstance().getTagManager().getTag(UserData.getActive(menuUtil.getOwner().getUniqueId())).getTag().get(0));
                    }
                } else {
                    item_displayname = item_displayname.replace("%tag%", "");
                }

                item_displayname = PlaceholderAPI.setPlaceholders(menuUtil.getOwner(), item_displayname);
                assert item_displayname != null;

                if (!item_lore.isEmpty()) {
                    item_lore.replaceAll(s -> s.replace("%identifier%", UserData.getActive(menuUtil.getOwner().getUniqueId())));
                    if (SupremeTagsPremium.getInstance().getTagManager().getTag(UserData.getActive(menuUtil.getOwner().getUniqueId())) != null) {
                        if (SupremeTagsPremium.getInstance().getTagManager().getTag(UserData.getActive(menuUtil.getOwner().getUniqueId())).getCurrentTag() != null) {
                            item_lore.replaceAll(s -> s.replace("%tag%", SupremeTagsPremium.getInstance().getTagManager().getTag(UserData.getActive(menuUtil.getOwner().getUniqueId())).getCurrentTag()));
                        } else {
                            item_lore.replaceAll(s -> s.replace("%tag%", SupremeTagsPremium.getInstance().getTagManager().getTag(UserData.getActive(menuUtil.getOwner().getUniqueId())).getTag().get(0)));
                        }
                    } else {
                        item_lore.replaceAll(s -> s.replace("%tag%", ""));
                    }

                    item_lore.replaceAll(s -> PlaceholderAPI.setPlaceholders(menuUtil.getOwner(), s));
                }

                itemMeta.setLore(color(item_lore));

                itemMeta.setDisplayName(format(item_displayname));

                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS);

                item.setItemMeta(itemMeta);
                inventory.setItem(item_slot, item);
            }
        }
    }

    public void applyPTLayout() {

        String back = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.back.displayname");
        String close = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.close.displayname");
        String next = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.next.displayname");
        String reset = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.reset.displayname");
        String active = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.active.displayname");
        String createtag = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.create-tag.displayname");

        inventory.setItem(SupremeTagsPremium.getInstance().getConfig().getInt("gui.items.back.slot"), makeItem(Material.valueOf(Objects.requireNonNull(SupremeTagsPremium.getInstance().getConfig().getString("gui.items.back.material")).toUpperCase()), back, 0));

        if (SupremeTagsPremium.getInstance().getConfig().getBoolean("gui.items.close.enable")) {
            inventory.setItem(SupremeTagsPremium.getInstance().getConfig().getInt("gui.items.close.slot"), makeItem(Material.valueOf(Objects.requireNonNull(SupremeTagsPremium.getInstance().getConfig().getString("gui.items.close.material")).toUpperCase()), close, 0));
        }

        if (currentItemsOnPage > 36) {
            inventory.setItem(SupremeTagsPremium.getInstance().getConfig().getInt("gui.items.next.slot"), makeItem(Material.valueOf(Objects.requireNonNull(SupremeTagsPremium.getInstance().getConfig().getString("gui.items.next.material")).toUpperCase()), next, 0));
        }

        if (!SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.forced-tag") && SupremeTagsPremium.getInstance().getConfig().getBoolean("gui.items.back.enable")) {
            inventory.setItem(SupremeTagsPremium.getInstance().getConfig().getInt("gui.items.reset.slot"), makeItem(Material.valueOf(Objects.requireNonNull(SupremeTagsPremium.getInstance().getConfig().getString("gui.items.reset.material")).toUpperCase()), reset, 0));
        }

        if (SupremeTagsPremium.getInstance().getConfig().getBoolean("gui.items.close.enable")) {
            inventory.setItem(SupremeTagsPremium.getInstance().getConfig().getInt("gui.items.close.slot"), makeItem(Material.valueOf(Objects.requireNonNull(SupremeTagsPremium.getInstance().getConfig().getString("gui.items.close.material")).toUpperCase()), close, 0));
        }

        if (SupremeTagsPremium.getInstance().getConfig().getBoolean("gui.items.create-tag.enable")) {
            inventory.setItem(SupremeTagsPremium.getInstance().getConfig().getInt("gui.items.create-tag.slot"), makeItem(Material.valueOf(Objects.requireNonNull(SupremeTagsPremium.getInstance().getConfig().getString("gui.items.create-tag.material")).toUpperCase()), createtag, 0));
        }

        //if (SupremeTags.getInstance().getConfig().getBoolean("gui.items.active-item")) {
        //    active = active.replaceAll("%identifier%", UserData.getActive(menuUtil.getOwner().getUniqueId()));
        //    active = active.replaceAll("%tag%", SupremeTags.getInstance().getTagManager().getTag(UserData.getActive(menuUtil.getOwner().getUniqueId())).getTag());
        //    active = replacePlaceholders(menuUtil.getOwner(), active);
        //    inventory.setItem(52, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.active-tag-material")).toUpperCase()), format(active)));
        //}

        if (SupremeTagsPremium.getInstance().getLayout().equalsIgnoreCase("FULL")) {
            if (SupremeTagsPremium.getInstance().getConfig().getBoolean("gui.items.glass.enable")) {
                for (int i = 36; i <= 44; i++) {
                    String item_material = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.glass.material");
                    String item_displayname = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.glass.displayname");
                    int item_custom_model_data = SupremeTagsPremium.getInstance().getConfig().getInt("gui.items.glass.custom-model-data");

                    assert item_material != null;
                    inventory.setItem(i, makeItem(Material.valueOf(item_material.toUpperCase()), item_displayname, item_custom_model_data));
                }
            }
        } else if (SupremeTagsPremium.getInstance().getLayout().equalsIgnoreCase("BORDER")) {
            for (int i = 0; i < 54; i++) {
                if (inventory.getItem(i) == null) {
                    if (i < 9 || i >= 45 || i % 9 == 0 || (i + 1) % 9 == 0) {
                        String item_material = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.glass.material");
                        String item_displayname = SupremeTagsPremium.getInstance().getConfig().getString("gui.items.glass.displayname");
                        int item_custom_model_data = SupremeTagsPremium.getInstance().getConfig().getInt("gui.items.glass.custom-model-data");

                        assert item_material != null;
                        inventory.setItem(i, makeItem(Material.valueOf(item_material.toUpperCase()), item_displayname, item_custom_model_data));
                    }
                }
            }
        }
    }

    protected int getPage() {
        return page + 1;
    }

    public int getMaxItems() {
        return maxItems;
    }

    // ===================================================================================

    public void getTagItems() {
        Map<String, Tag> tags = SupremeTagsPremium.getInstance().getTagManager().getTags();

        ArrayList<Tag> tag = new ArrayList<>(tags.values());

        if (!tag.isEmpty()) {
            int maxItemsPerPage = 0;

            if (SupremeTagsPremium.getInstance().getLayout().equalsIgnoreCase("FULL")) {
                maxItemsPerPage = 36;
            } else if (SupremeTagsPremium.getInstance().getLayout().equalsIgnoreCase("BORDER")) {
                maxItemsPerPage = 28;
            }

            int startIndex = page * maxItemsPerPage;
            int endIndex = Math.min(startIndex + maxItemsPerPage, tag.size());

            tag.sort((tag1, tag2) -> {
                boolean hasPermission1 = menuUtil.getOwner().hasPermission(tag1.getPermission());
                boolean hasPermission2 = menuUtil.getOwner().hasPermission(tag2.getPermission());

                if (hasPermission1 && !hasPermission2) {
                    return -1; // tag1 comes before tag2
                } else if (!hasPermission1 && hasPermission2) {
                    return 1; // tag2 comes before tag1
                } else {
                    // Sort based on the order
                    int orderComparison = Integer.compare(tag1.getOrder(), tag2.getOrder());
                    if (orderComparison != 0) {
                        return orderComparison;
                    } else {
                        // Sort alphabetically if both tags have the same permission and order
                        return tag1.getIdentifier().compareTo(tag2.getIdentifier());
                    }
                }
            });

            currentItemsOnPage = 0;

            for (int i = startIndex; i < endIndex; i++) {
                Tag t = tag.get(i);
                if (t == null) continue;

                String permission = SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getString("tags." + t.getIdentifier() + ".permission");

                if (permission != null && !SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.locked-view") && !SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission))
                    continue;

                String displayname;

                if (SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getString("tags." + t.getIdentifier() + ".displayname") != null) {
                    if (t.getCurrentTag() != null) {
                        displayname = Objects.requireNonNull(SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getString("tags." + t.getIdentifier() + ".displayname")).replace("%tag%", t.getCurrentTag());
                    } else {
                        displayname = Objects.requireNonNull(SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getString("tags." + t.getIdentifier() + ".displayname")).replace("%tag%", t.getTag().get(0));
                    }
                } else {
                    displayname = format("&7Tag: " + t.getCurrentTag());
                }

                if (SupremeTagsPremium.getInstance().isPlaceholderAPI()) {
                    displayname = replacePlaceholders(menuUtil.getOwner(), displayname);
                }

                String material;

                if (SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getString("tags." + t.getIdentifier() + ".display-item") != null) {
                    material = SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getString("tags." + t.getIdentifier() + ".display-item");
                } else {
                    material = "NAME_TAG";
                }

                assert permission != null;

                // toggle if they don't have permission
                ItemStack tagItem;
                ItemMeta tagMeta;
                NBTItem nbt;

                if (material.contains("hdb-")) {
                    int id = Integer.parseInt(material.replaceAll("hdb-", ""));
                    HeadDatabaseAPI api = new HeadDatabaseAPI();
                    tagItem = api.getItemHead(String.valueOf(id));
                    tagMeta = tagItem.getItemMeta();
                    nbt = new NBTItem(tagItem);
                } else if (material.contains("basehead-")) {
                    String id = material.replaceAll("basehead-", "");
                    tagItem = createSkull(id);
                    tagMeta = tagItem.getItemMeta();
                    nbt = new NBTItem(tagItem);
                } else {
                    tagItem = new ItemStack(Material.valueOf(material.toUpperCase()), 1);
                    tagMeta = tagItem.getItemMeta();
                    nbt = new NBTItem(tagItem);
                }

                if (SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getInt("tags." + t.getIdentifier() + ".custom-model-data") > 0) {
                    int modelData = SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getInt("tags." + t.getIdentifier() + ".custom-model-data");
                    if (tagMeta != null)
                        tagMeta.setCustomModelData(modelData);
                }

                nbt.setString("identifier", t.getIdentifier());

                if (UserData.getActive(menuUtil.getOwner().getUniqueId()).equalsIgnoreCase(t.getIdentifier()) && SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.active-tag-glow")) {
                    nbt.getItem().addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                }

                assert tagMeta != null;
                tagMeta.setDisplayName(format(displayname));

                tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS);

                List<String> lore;

                if (menuUtil.getOwner().hasPermission(permission)) {
                    lore = SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getStringList("tags." + t.getIdentifier() + ".unlocked-lore");
                } else if (SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getBoolean("settings.locked-view") && !SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.cost-system")) {
                    lore = SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getStringList("tags." + t.getIdentifier() + ".locked-permission");
                } else if (SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getBoolean("settings.cost-system")) {
                    if (menuUtil.getOwner().hasPermission(permission)) {
                        lore = SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getStringList("tags." + t.getIdentifier() + ".unlocked-lore");
                    } else {
                        lore = SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getStringList("tags." + t.getIdentifier() + ".locked-lore");
                    }
                } else {
                    lore = SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getStringList("tags." + t.getIdentifier() + ".unlocked-lore");
                }

                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s)
                        .replaceAll("%description%", format(Objects.requireNonNull(SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getString("tags." + t.getIdentifier() + ".description"))))
                        .replaceAll("%identifier%", t.getIdentifier())
                        .replaceAll("%tag%", t.getCurrentTag())
                        .replaceAll("%cost%", String.valueOf(t.getCost())));

                tagMeta.setLore(color(lore));

                nbt.getItem().setItemMeta(tagMeta);
                nbt.setString("identifier", t.getIdentifier());
                inventory.addItem(nbt.getItem());

                currentItemsOnPage++;
            }
        }
    }

    // ===========================================================

    public void getTagItemsCategory() {
        Map<String, Tag> tags = SupremeTagsPremium.getInstance().getTagManager().getTags();

        ArrayList<Tag> tag = tags.values().stream()
                .filter(t -> t.getCategory().equalsIgnoreCase(menuUtil.getCategory()))
                .collect(Collectors.toCollection(ArrayList::new));

        if (!tag.isEmpty()) {
            int maxItemsPerPage = 0;

            if (SupremeTagsPremium.getInstance().getLayout().equalsIgnoreCase("FULL")) {
                maxItemsPerPage = 36;
            } else if (SupremeTagsPremium.getInstance().getLayout().equalsIgnoreCase("BORDER")) {
                maxItemsPerPage = 28;
            }

            boolean isCostCategory = SupremeTagsPremium.getInstance().getCategoryManager().getCatConfig().getBoolean("categories." + menuUtil.getCategory() + ".cost-category");

            int startIndex = page * maxItemsPerPage;
            int endIndex = Math.min(startIndex + maxItemsPerPage, tag.size());

            tag.sort((tag1, tag2) -> {
                boolean hasPermission1 = menuUtil.getOwner().hasPermission(tag1.getPermission());
                boolean hasPermission2 = menuUtil.getOwner().hasPermission(tag2.getPermission());

                if (hasPermission1 && !hasPermission2) {
                    return -1;
                } else if (!hasPermission1 && hasPermission2) {
                    return 1;
                } else {
                    int orderComparison = Integer.compare(tag1.getOrder(), tag2.getOrder());
                    if (orderComparison != 0) {
                        return orderComparison;
                    } else {
                        return tag1.getIdentifier().compareTo(tag2.getIdentifier());
                    }
                }
            });

            currentItemsOnPage = 0;

            for (int i = startIndex; i < endIndex; i++) {
                Tag t = tag.get(i);
                if (t == null) continue;

                String permission = SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getString("tags." + t.getIdentifier() + ".permission");

                if (permission != null && !SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.locked-view") && !SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission))
                    continue;

                String displayname;

                if (SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getString("tags." + t.getIdentifier() + ".displayname") != null) {
                    if (t.getCurrentTag() != null) {
                        displayname = Objects.requireNonNull(SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getString("tags." + t.getIdentifier() + ".displayname")).replace("%tag%", t.getCurrentTag());
                    } else {
                        displayname = Objects.requireNonNull(SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getString("tags." + t.getIdentifier() + ".displayname")).replace("%tag%", t.getTag().get(0));
                    }
                } else {
                    displayname = format("&7Tag: " + t.getCurrentTag());
                }

                if (SupremeTagsPremium.getInstance().isPlaceholderAPI()) {
                    displayname = replacePlaceholders(menuUtil.getOwner(), displayname);
                }

                String material;

                if (SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getString("tags." + t.getIdentifier() + ".display-item") != null) {
                    material = SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getString("tags." + t.getIdentifier() + ".display-item");
                } else {
                    material = "NAME_TAG";
                }

                assert permission != null;

                // toggle if they don't have permission
                ItemStack tagItem;
                ItemMeta tagMeta;
                NBTItem nbt;

                if (material.contains("hdb-")) {
                    int id = Integer.parseInt(material.replaceAll("hdb-", ""));
                    HeadDatabaseAPI api = new HeadDatabaseAPI();
                    tagItem = api.getItemHead(String.valueOf(id));
                    tagMeta = tagItem.getItemMeta();
                    nbt = new NBTItem(tagItem);
                } else if (material.contains("basehead-")) {
                    String id = material.replaceAll("basehead-", "");
                    tagItem = createSkull(id);
                    tagMeta = tagItem.getItemMeta();
                    nbt = new NBTItem(tagItem);
                } else {
                    tagItem = new ItemStack(Material.valueOf(material.toUpperCase()), 1);
                    tagMeta = tagItem.getItemMeta();
                    nbt = new NBTItem(tagItem);
                }

                nbt.setString("identifier", t.getIdentifier());

                if (UserData.getActive(menuUtil.getOwner().getUniqueId()).equalsIgnoreCase(t.getIdentifier()) && SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.active-tag-glow")) {
                    nbt.getItem().addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                }

                tagMeta.setDisplayName(format(displayname));
                tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS);

                List<String> lore;

                if (!isCostCategory) {
                    if (menuUtil.getOwner().hasPermission(permission)) {
                        lore = SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getStringList("tags." + t.getIdentifier() + ".unlocked-lore");
                    } else if (SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getBoolean("settings.locked-view") && !SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.cost-system")) {
                        lore = SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getStringList("tags." + t.getIdentifier() + ".locked-permission");
                    } else if (SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getBoolean("settings.cost-system")) {
                        if (menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getStringList("tags." + t.getIdentifier() + ".unlocked-lore");
                        } else {
                            lore = SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getStringList("tags." + t.getIdentifier() + ".locked-lore");
                        }
                    } else {
                        lore = SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getStringList("tags." + t.getIdentifier() + ".unlocked-lore");
                    }
                } else {
                    if (menuUtil.getOwner().hasPermission(permission)) {
                        lore = SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getStringList("tags." + t.getIdentifier() + ".unlocked-lore");
                    } else {
                        lore = SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getStringList("tags." + t.getIdentifier() + ".locked-permission");
                    }
                }

                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s)
                        .replaceAll("%description%", format(Objects.requireNonNull(SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getString("tags." + t.getIdentifier() + ".description"))))
                        .replaceAll("%identifier%", t.getIdentifier())
                        .replaceAll("%tag%", t.getCurrentTag())
                        .replaceAll("%cost%", String.valueOf(t.getCost())));

                tagMeta.setLore(color(lore));

                nbt.getItem().setItemMeta(tagMeta);
                nbt.setString("identifier", t.getIdentifier());
                inventory.addItem(nbt.getItem());
            }
            currentItemsOnPage++;
        }
    }

    // ================================================================

    public void getTagItemsEditor() {
        Map<String, Tag> tags = SupremeTagsPremium.getInstance().getTagManager().getTags();

        ArrayList<Tag> tag = new ArrayList<>(tags.values());

        if (!tag.isEmpty()) {
            int maxItemsPerPage = 36;
            int startIndex = page * maxItemsPerPage;
            int endIndex = Math.min(startIndex + maxItemsPerPage, tag.size());

            tag.sort((tag1, tag2) -> {
                boolean hasPermission1 = menuUtil.getOwner().hasPermission(tag1.getPermission());
                boolean hasPermission2 = menuUtil.getOwner().hasPermission(tag2.getPermission());

                if (hasPermission1 && !hasPermission2) {
                    return -1; // tag1 comes before tag2
                } else if (!hasPermission1 && hasPermission2) {
                    return 1; // tag2 comes before tag1
                } else {
                    // Sort alphabetically if both tags have permission or both don't
                    return tag1.getIdentifier().compareTo(tag2.getIdentifier());
                }
            });

            currentItemsOnPage = 0;

            for (int i = startIndex; i < endIndex; i++) {
                Tag t = tag.get(i);
                if (t == null) continue;

                String permission = SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getString("tags." + t.getIdentifier() + ".permission");

                if (permission != null && !SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.locked-view") && !SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.cost-system")  && !menuUtil.getOwner().hasPermission(permission)) continue;

                String displayname;

                if (SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getString("tags." + t.getIdentifier() + ".displayname") != null) {
                    if (t.getCurrentTag() != null) {
                        displayname = Objects.requireNonNull(SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getString("tags." + t.getIdentifier() + ".displayname")).replace("%tag%", t.getCurrentTag());
                    } else {
                        displayname = Objects.requireNonNull(SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getString("tags." + t.getIdentifier() + ".displayname")).replace("%tag%", t.getTag().get(0));
                    }
                } else {
                    displayname = format("&7Tag: " + t.getTag().get(0));
                }

                if (SupremeTagsPremium.getInstance().isPlaceholderAPI()) {
                    displayname = replacePlaceholders(menuUtil.getOwner(), displayname);
                }

                String material;

                if (SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getString("tags." + t.getIdentifier() + ".display-item") != null) {
                    material = SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getString("tags." + t.getIdentifier() + ".display-item");
                } else {
                    material = "NAME_TAG";
                }

                assert permission != null;

                if (material.contains("hdb-")) {

                    HeadDatabaseAPI api = new HeadDatabaseAPI();

                    int id = Integer.parseInt(material.replaceAll("hdb-", ""));

                    ItemStack tagItem = api.getItemHead(String.valueOf(id));
                    ItemMeta tagMeta = tagItem.getItemMeta();
                    assert tagMeta != null;

                    NBTItem nbt = new NBTItem(tagItem);

                    nbt.setString("identifier", t.getIdentifier());

                    tagMeta.setDisplayName(format(displayname));

                    tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    tagMeta.addItemFlags(ItemFlag.HIDE_DYE);
                    tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                    tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                    // set lore
                    ArrayList<String> lore = (ArrayList<String>) SupremeTagsPremium.getInstance().getConfig().getStringList("gui.tag-editor-menu.tag-item.lore");
                    lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%description%", format(t.getDescription())));
                    lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%identifier%", t.getIdentifier()));
                    lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tag%", t.getCurrentTag()));

                    tagMeta.setLore(color(lore));

                    nbt.getItem().setItemMeta(tagMeta);

                    nbt.setString("identifier", t.getIdentifier());
                    inventory.addItem(nbt.getItem());
                } else if (material.contains("basehead-")) {

                    String id = material.replaceAll("basehead-", "");

                    ItemStack tagItem = createSkull(id);
                    ItemMeta tagMeta = tagItem.getItemMeta();
                    assert tagMeta != null;

                    NBTItem nbt = new NBTItem(tagItem);

                    nbt.setString("identifier", t.getIdentifier());

                    tagMeta.setDisplayName(format(displayname));
                    tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    tagMeta.addItemFlags(ItemFlag.HIDE_DYE);
                    tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                    tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                    // set lore
                    ArrayList<String> lore = (ArrayList<String>) SupremeTagsPremium.getInstance().getConfig().getStringList("gui.tag-editor-menu.tag-item.lore");
                    lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%description%", format(t.getDescription())));
                    lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%identifier%", t.getIdentifier()));
                    lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tag%", t.getCurrentTag()));

                    tagMeta.setLore(color(lore));
                    nbt.getItem().setItemMeta(tagMeta);

                    nbt.setString("identifier", t.getIdentifier());
                    inventory.addItem(nbt.getItem());
                } else {
                    ItemStack tagItem = new ItemStack(Material.valueOf(material.toUpperCase()), 1);
                    ItemMeta tagMeta = tagItem.getItemMeta();
                    assert tagMeta != null;

                    NBTItem nbt = new NBTItem(tagItem);

                    nbt.setString("identifier", t.getIdentifier());

                    tagMeta.setDisplayName(format(displayname));
                    tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    tagMeta.addItemFlags(ItemFlag.HIDE_DYE);
                    tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                    tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                    // set lore
                    ArrayList<String> lore = (ArrayList<String>) SupremeTagsPremium.getInstance().getConfig().getStringList("gui.tag-editor-menu.tag-item.lore");
                    lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%description%", format(t.getDescription())));
                    lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%identifier%", t.getIdentifier()));
                    lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tag%", t.getCurrentTag()));

                    tagMeta.setLore(color(lore));
                    nbt.getItem().setItemMeta(tagMeta);

                    nbt.setString("identifier", t.getIdentifier());
                    inventory.addItem(nbt.getItem());
                }
                currentItemsOnPage++;
            }
        }
    }

    public int getCurrentItemsOnPage() {
        return currentItemsOnPage;
    }
}