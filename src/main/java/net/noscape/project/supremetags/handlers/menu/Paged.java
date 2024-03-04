package net.noscape.project.supremetags.handlers.menu;

import de.tr7zw.nbtapi.NBTItem;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.noscape.project.supremetags.*;
import net.noscape.project.supremetags.handlers.Tag;
import net.noscape.project.supremetags.storage.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

        Map<String, Tag> tags = SupremeTags.getInstance().getTagManager().getTags();
        ArrayList<Tag> tag = new ArrayList<>(tags.values());

        tagsCount = tag.size();
    }

    public void applyEditorLayout() {

        String back = SupremeTags.getInstance().getConfig().getString("gui.strings.back-item");
        String close = SupremeTags.getInstance().getConfig().getString("gui.strings.close-item");
        String next = SupremeTags.getInstance().getConfig().getString("gui.strings.next-item");

        inventory.setItem(48, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.back-next-material")).toUpperCase()), back));

        inventory.setItem(49, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.close-menu-material")).toUpperCase()), close));

        if (getCurrentItemsOnPage() == 36 && tagsCount > 36) {
            inventory.setItem(50, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.back-next-material")).toUpperCase()), next));
        }

        for (int i = 36; i <= 44; i++) {
            inventory.setItem(i, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.glass-material")).toUpperCase()), " "));
        }
    }

    public void applyLayout() {
        String back = SupremeTags.getInstance().getConfig().getString("gui.strings.back-item");
        String close = SupremeTags.getInstance().getConfig().getString("gui.strings.close-item");
        String next = SupremeTags.getInstance().getConfig().getString("gui.strings.next-item");
        String refresh = SupremeTags.getInstance().getConfig().getString("gui.strings.refresh-item");
        String reset = SupremeTags.getInstance().getConfig().getString("gui.strings.reset-item");
        String active = SupremeTags.getInstance().getConfig().getString("gui.strings.active-item");

        if (SupremeTags.getInstance().getConfig().getBoolean("gui.items.back-item")) {
            inventory.setItem(48, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.back-next-material")).toUpperCase()), back));
        }

        if (SupremeTags.getInstance().getConfig().getBoolean("gui.items.close-item")) {
            inventory.setItem(49, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.close-menu-material")).toUpperCase()), close));
        }

        if (getCurrentItemsOnPage() == 36 && SupremeTags.getInstance().getConfig().getBoolean("gui.items.next-item")) {
            inventory.setItem(50, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.back-next-material")).toUpperCase()), next));
        }

        if (!SupremeTags.getInstance().getConfig().getBoolean("settings.forced-tag") && SupremeTags.getInstance().getConfig().getBoolean("gui.items.reset-item")) {
            inventory.setItem(46, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.reset-tag-material")).toUpperCase()), reset));
        }

        if (SupremeTags.getInstance().getConfig().getBoolean("gui.items.refresh-item")) {
            inventory.setItem(45, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.refresh-material")).toUpperCase()), refresh));
        }

        if (SupremeTags.getInstance().getConfig().getBoolean("gui.items.active-item")) {
            active = active.replaceAll("%identifier%", UserData.getActive(menuUtil.getOwner().getUniqueId()));

            if (SupremeTags.getInstance().getTagManager().getTag(UserData.getActive(menuUtil.getOwner().getUniqueId())) != null) {
                active = active.replaceAll("%tag%", SupremeTags.getInstance().getTagManager().getTag(UserData.getActive(menuUtil.getOwner().getUniqueId())).getTag());
            } else {
                active = active.replaceAll("%tag%", Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("settings.none-output")));
            }

            if (Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
                active = replacePlaceholders(menuUtil.getOwner(), active);
            }

            inventory.setItem(52, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.active-tag-material")).toUpperCase()), format(active)));
        }

        if (SupremeTags.getInstance().getConfig().getBoolean("gui.items.glass-item")) {
            for (int i = 36; i <= 44; i++) {
                inventory.setItem(i, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.glass-material")).toUpperCase()), " "));
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
        Map<String, Tag> tags = SupremeTags.getInstance().getTagManager().getTags();

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

                String permission = SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".permission");

                if (permission != null && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system")  && !menuUtil.getOwner().hasPermission(permission)) continue;

                String displayname;

                if (SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".displayname") != null) {
                    displayname = Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".displayname")).replace("%tag%", t.getTag());
                } else {
                    displayname = format("&7Tag: " + t.getTag());
                }

                if (SupremeTags.getInstance().isPlaceholderAPI()) {
                    displayname = replacePlaceholders(menuUtil.getOwner(), displayname);
                }

                displayname = displayname.replaceAll("\\$", "\\\\\\$");
                permission = permission.replaceAll("\\$", "\\\\\\$");

                String material;

                if (SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".display-item") != null) {
                    material = SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".display-item");
                } else {
                    material = "NAME_TAG";
                }

                // toggle if they don't have permission
                if (menuUtil.getOwner().hasPermission("supremetags.tag.*") || (menuUtil.getOwner().hasPermission(permission) && !permission.equalsIgnoreCase("none"))) {
                    if (material.contains("hdb-")) {
                        int id = Integer.parseInt(material.replaceAll("hdb-", ""));

                        HeadDatabaseAPI api = new HeadDatabaseAPI();

                        ItemStack tagItem = api.getItemHead(String.valueOf(id));
                        ItemMeta tagMeta = tagItem.getItemMeta();
                        assert tagMeta != null;

                        NBTItem nbt = new NBTItem(tagItem);

                        nbt.setString("identifier", t.getIdentifier());

                        if (UserData.getActive(menuUtil.getOwner().getUniqueId()).equalsIgnoreCase(t.getIdentifier())) {
                            if (SupremeTags.getInstance().getConfig().getBoolean("settings.active-tag-glow")) {
                                nbt.getItem().addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                            }
                        }

                        tagMeta.setDisplayName(format(displayname));
                        tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

                        List<String> lore;

                        if (SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-permission");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if(menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        }

                        String descriptionPlaceholder = "%description%";
                        String identifierPlaceholder = "%identifier%";
                        String tagPlaceholder = "%tag%";
                        String costPlaceholder = "%cost%";

                        for (int l = 0; l < lore.size(); l++) {
                            String line = lore.get(l);
                            line = ChatColor.translateAlternateColorCodes('&', line);
                            line = line.replaceAll(descriptionPlaceholder, format(t.getDescription()));
                            line = line.replaceAll(identifierPlaceholder, t.getIdentifier());
                            line = line.replaceAll(tagPlaceholder, t.getTag());
                            line = line.replaceAll(costPlaceholder, String.valueOf(t.getCost()));
                            lore.set(l, line);
                        }

                        lore = lore.stream()
                                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                                .collect(Collectors.toList());

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

                        if (UserData.getActive(menuUtil.getOwner().getUniqueId()).equalsIgnoreCase(t.getIdentifier())) {
                            if (SupremeTags.getInstance().getConfig().getBoolean("settings.active-tag-glow")) {
                                nbt.getItem().addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                            }
                        }

                        nbt.setString("identifier", t.getIdentifier());

                        tagMeta.setDisplayName(format(displayname));
                        tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

                        List<String> lore;

                        if (SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-permission");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if(menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        }

                        String descriptionPlaceholder = "%description%";
                        String identifierPlaceholder = "%identifier%";
                        String tagPlaceholder = "%tag%";
                        String costPlaceholder = "%cost%";

                        for (int l = 0; l < lore.size(); l++) {
                            String line = lore.get(l);
                            line = ChatColor.translateAlternateColorCodes('&', line);
                            line = line.replaceAll(descriptionPlaceholder, format(t.getDescription()));
                            line = line.replaceAll(identifierPlaceholder, t.getIdentifier());
                            line = line.replaceAll(tagPlaceholder, t.getTag());
                            line = line.replaceAll(costPlaceholder, String.valueOf(t.getCost()));
                            lore.set(l, line);
                        }

                        lore = lore.stream()
                                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                                .collect(Collectors.toList());

                        tagMeta.setLore(color(lore));

                        nbt.getItem().setItemMeta(tagMeta);

                        nbt.setString("identifier", t.getIdentifier());
                        inventory.addItem(nbt.getItem());
                    } else {
                        ItemStack tagItem = new ItemStack(Material.valueOf(material.toUpperCase()), 1);
                        ItemMeta tagMeta = tagItem.getItemMeta();
                        assert tagMeta != null;

                        NBTItem nbt = new NBTItem(tagItem);

                        if (UserData.getActive(menuUtil.getOwner().getUniqueId()).equalsIgnoreCase(t.getIdentifier())) {
                            if (SupremeTags.getInstance().getConfig().getBoolean("settings.active-tag-glow")) {
                                nbt.getItem().addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                            }
                        }

                        nbt.setString("identifier", t.getIdentifier());

                        tagMeta.setDisplayName(format(displayname));
                        tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

                        List<String> lore;

                        if (SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-permission");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if(menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        }

                        String descriptionPlaceholder = "%description%";
                        String identifierPlaceholder = "%identifier%";
                        String tagPlaceholder = "%tag%";
                        String costPlaceholder = "%cost%";

                        for (int l = 0; l < lore.size(); l++) {
                            String line = lore.get(l);
                            line = ChatColor.translateAlternateColorCodes('&', line);
                            line = line.replaceAll(descriptionPlaceholder, format(t.getDescription()));
                            line = line.replaceAll(identifierPlaceholder, t.getIdentifier());
                            line = line.replaceAll(tagPlaceholder, t.getTag());
                            line = line.replaceAll(costPlaceholder, String.valueOf(t.getCost()));
                            lore.set(l, line);
                        }

                        lore = lore.stream()
                                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                                .collect(Collectors.toList());

                        tagMeta.setLore(color(lore));

                        nbt.getItem().setItemMeta(tagMeta);

                        nbt.setString("identifier", t.getIdentifier());
                        inventory.addItem(nbt.getItem());
                    }
                    // if permission == none
                } else if (menuUtil.getOwner().hasPermission("supremetags.tag.*") && !menuUtil.getOwner().hasPermission(permission) && permission.equalsIgnoreCase("none")) {
                    if (material.contains("hdb-")) {

                        int id = Integer.parseInt(material.replaceAll("hdb-", ""));

                        HeadDatabaseAPI api = new HeadDatabaseAPI();

                        ItemStack tagItem = api.getItemHead(String.valueOf(id));
                        ItemMeta tagMeta = tagItem.getItemMeta();
                        assert tagMeta != null;

                        NBTItem nbt = new NBTItem(tagItem);

                        nbt.setString("identifier", t.getIdentifier());

                        if (UserData.getActive(menuUtil.getOwner().getUniqueId()).equalsIgnoreCase(t.getIdentifier())) {
                            if (SupremeTags.getInstance().getConfig().getBoolean("settings.active-tag-glow")) {
                                nbt.getItem().addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                            }
                        }

                        tagMeta.setDisplayName(format(displayname));
                        tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

                        List<String> lore;

                        if (SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-permission");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if(menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        }

                        String descriptionPlaceholder = "%description%";
                        String identifierPlaceholder = "%identifier%";
                        String tagPlaceholder = "%tag%";
                        String costPlaceholder = "%cost%";

                        for (int l = 0; l < lore.size(); l++) {
                            String line = lore.get(l);
                            line = ChatColor.translateAlternateColorCodes('&', line);
                            line = line.replaceAll(descriptionPlaceholder, format(t.getDescription()));
                            line = line.replaceAll(identifierPlaceholder, t.getIdentifier());
                            line = line.replaceAll(tagPlaceholder, t.getTag());
                            line = line.replaceAll(costPlaceholder, String.valueOf(t.getCost()));
                            lore.set(l, line);
                        }

                        lore = lore.stream()
                                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                                .collect(Collectors.toList());

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

                        if (UserData.getActive(menuUtil.getOwner().getUniqueId()).equalsIgnoreCase(t.getIdentifier())) {
                            if (SupremeTags.getInstance().getConfig().getBoolean("settings.active-tag-glow")) {
                                nbt.getItem().addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                            }
                        }

                        nbt.setString("identifier", t.getIdentifier());

                        tagMeta.setDisplayName(format(displayname));
                        tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

                        List<String> lore;

                        if (SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-permission");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if(menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        }

                        String descriptionPlaceholder = "%description%";
                        String identifierPlaceholder = "%identifier%";
                        String tagPlaceholder = "%tag%";
                        String costPlaceholder = "%cost%";

                        for (int l = 0; l < lore.size(); l++) {
                            String line = lore.get(l);
                            line = ChatColor.translateAlternateColorCodes('&', line);
                            line = line.replaceAll(descriptionPlaceholder, format(t.getDescription()));
                            line = line.replaceAll(identifierPlaceholder, t.getIdentifier());
                            line = line.replaceAll(tagPlaceholder, t.getTag());
                            line = line.replaceAll(costPlaceholder, String.valueOf(t.getCost()));
                            lore.set(l, line);
                        }

                        lore = lore.stream()
                                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                                .collect(Collectors.toList());

                        tagMeta.setLore(color(lore));

                        nbt.getItem().setItemMeta(tagMeta);

                        nbt.setString("identifier", t.getIdentifier());
                        inventory.addItem(nbt.getItem());
                    } else {
                        ItemStack tagItem = new ItemStack(Material.valueOf(material.toUpperCase()), 1);
                        ItemMeta tagMeta = tagItem.getItemMeta();
                        assert tagMeta != null;

                        NBTItem nbt = new NBTItem(tagItem);

                        if (UserData.getActive(menuUtil.getOwner().getUniqueId()).equalsIgnoreCase(t.getIdentifier())) {
                            if (SupremeTags.getInstance().getConfig().getBoolean("settings.active-tag-glow")) {
                                nbt.getItem().addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                            }
                        }

                        nbt.setString("identifier", t.getIdentifier());

                        tagMeta.setDisplayName(format(displayname));
                        tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

                        List<String> lore;

                        if (SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-permission");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if(menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        }

                        String descriptionPlaceholder = "%description%";
                        String identifierPlaceholder = "%identifier%";
                        String tagPlaceholder = "%tag%";
                        String costPlaceholder = "%cost%";

                        for (int l = 0; l < lore.size(); l++) {
                            String line = lore.get(l);
                            line = ChatColor.translateAlternateColorCodes('&', line);
                            line = line.replaceAll(descriptionPlaceholder, format(t.getDescription()));
                            line = line.replaceAll(identifierPlaceholder, t.getIdentifier());
                            line = line.replaceAll(tagPlaceholder, t.getTag());
                            line = line.replaceAll(costPlaceholder, String.valueOf(t.getCost()));
                            lore.set(l, line);
                        }

                        lore = lore.stream()
                                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                                .collect(Collectors.toList());

                        tagMeta.setLore(color(lore));

                        nbt.getItem().setItemMeta(tagMeta);

                        nbt.setString("identifier", t.getIdentifier());
                        inventory.addItem(nbt.getItem());
                    }
                } else if (!menuUtil.getOwner().hasPermission("supremetags.tag.*") && !menuUtil.getOwner().hasPermission(permission)) {
                    if (material.contains("hdb-")) {

                        int id = Integer.parseInt(material.replaceAll("hdb-", ""));

                        HeadDatabaseAPI api = new HeadDatabaseAPI();

                        ItemStack tagItem = api.getItemHead(String.valueOf(id));
                        ItemMeta tagMeta = tagItem.getItemMeta();
                        assert tagMeta != null;

                        NBTItem nbt = new NBTItem(tagItem);

                        nbt.setString("identifier", t.getIdentifier());

                        tagMeta.setDisplayName(format(displayname));
                        tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

                        if (UserData.getActive(menuUtil.getOwner().getUniqueId()).equalsIgnoreCase(t.getIdentifier())) {
                            if (SupremeTags.getInstance().getConfig().getBoolean("settings.active-tag-glow")) {
                                nbt.getItem().addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                            }
                        }

                        List<String> lore;

                        if (SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-permission");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if(menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        }

                        String descriptionPlaceholder = "%description%";
                        String identifierPlaceholder = "%identifier%";
                        String tagPlaceholder = "%tag%";
                        String costPlaceholder = "%cost%";

                        for (int l = 0; l < lore.size(); l++) {
                            String line = lore.get(l);
                            line = ChatColor.translateAlternateColorCodes('&', line);
                            line = line.replaceAll(descriptionPlaceholder, format(t.getDescription()));
                            line = line.replaceAll(identifierPlaceholder, t.getIdentifier());
                            line = line.replaceAll(tagPlaceholder, t.getTag());
                            line = line.replaceAll(costPlaceholder, String.valueOf(t.getCost()));
                            lore.set(l, line);
                        }

                        lore = lore.stream()
                                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                                .collect(Collectors.toList());

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

                        if (UserData.getActive(menuUtil.getOwner().getUniqueId()).equalsIgnoreCase(t.getIdentifier())) {
                            if (SupremeTags.getInstance().getConfig().getBoolean("settings.active-tag-glow")) {
                                nbt.getItem().addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                            }
                        }

                        tagMeta.setDisplayName(format(displayname));
                        tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

                        List<String> lore;

                        if (SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-permission");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if(menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        }

                        String descriptionPlaceholder = "%description%";
                        String identifierPlaceholder = "%identifier%";
                        String tagPlaceholder = "%tag%";
                        String costPlaceholder = "%cost%";

                        for (int l = 0; l < lore.size(); l++) {
                            String line = lore.get(l);
                            line = ChatColor.translateAlternateColorCodes('&', line);
                            line = line.replaceAll(descriptionPlaceholder, format(t.getDescription()));
                            line = line.replaceAll(identifierPlaceholder, t.getIdentifier());
                            line = line.replaceAll(tagPlaceholder, t.getTag());
                            line = line.replaceAll(costPlaceholder, String.valueOf(t.getCost()));
                            lore.set(l, line);
                        }
                        lore = lore.stream()
                                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                                .collect(Collectors.toList());

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

                        if (UserData.getActive(menuUtil.getOwner().getUniqueId()).equalsIgnoreCase(t.getIdentifier())) {
                            if (SupremeTags.getInstance().getConfig().getBoolean("settings.active-tag-glow")) {
                                nbt.getItem().addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                            }
                        }

                        tagMeta.setDisplayName(format(displayname));
                        tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                        List<String> lore;

                        if (SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-permission");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if(menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        }

                        String descriptionPlaceholder = "%description%";
                        String identifierPlaceholder = "%identifier%";
                        String tagPlaceholder = "%tag%";
                        String costPlaceholder = "%cost%";

                        for (int l = 0; l < lore.size(); l++) {
                            String line = lore.get(l);
                            line = ChatColor.translateAlternateColorCodes('&', line);
                            line = line.replaceAll(descriptionPlaceholder, format(t.getDescription()));
                            line = line.replaceAll(identifierPlaceholder, t.getIdentifier());
                            line = line.replaceAll(tagPlaceholder, t.getTag());
                            line = line.replaceAll(costPlaceholder, String.valueOf(t.getCost()));
                            lore.set(l, line);
                        }

                        lore = lore.stream()
                                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                                .collect(Collectors.toList());

                        tagMeta.setLore(color(lore));

                        nbt.getItem().setItemMeta(tagMeta);

                        nbt.setString("identifier", t.getIdentifier());
                        inventory.addItem(nbt.getItem());
                    }
                }
                currentItemsOnPage++;
            }
        }
    }

    // ===========================================================

    public void getTagItemsCategory() {
        Map<String, Tag> tags = SupremeTags.getInstance().getTagManager().getTags();

        ArrayList<Tag> tag = tags.values().stream()
                .filter(t -> t.getCategory().equalsIgnoreCase(menuUtil.getCategory()))
                .collect(Collectors.toCollection(ArrayList::new));

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

                String permission = SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".permission");

                if (permission != null && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system")  && !menuUtil.getOwner().hasPermission(permission)) continue;

                String displayname;

                if (SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".displayname") != null) {
                    displayname = Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".displayname")).replace("%tag%", t.getTag());
                } else {
                    displayname = format("&7Tag: " + t.getTag());
                }

                if (SupremeTags.getInstance().isPlaceholderAPI()) {
                    displayname = replacePlaceholders(menuUtil.getOwner(), displayname);
                }

                String material;

                if (SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".display-item") != null) {
                    material = SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".display-item");
                } else {
                    material = "NAME_TAG";
                }

                assert permission != null;

                // toggle if they don't have permission
                if (menuUtil.getOwner().hasPermission("supremetags.tag.*") || (menuUtil.getOwner().hasPermission(permission) && !permission.equalsIgnoreCase("none"))) {
                    if (material.contains("hdb-")) {
                        int id = Integer.parseInt(material.replaceAll("hdb-", ""));

                        HeadDatabaseAPI api = new HeadDatabaseAPI();

                        ItemStack tagItem = api.getItemHead(String.valueOf(id));
                        ItemMeta tagMeta = tagItem.getItemMeta();
                        assert tagMeta != null;

                        NBTItem nbt = new NBTItem(tagItem);

                        nbt.setString("identifier", t.getIdentifier());

                        if (UserData.getActive(menuUtil.getOwner().getUniqueId()).equalsIgnoreCase(t.getIdentifier())) {
                            if (SupremeTags.getInstance().getConfig().getBoolean("settings.active-tag-glow")) {
                                nbt.getItem().addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                            }
                        }

                        tagMeta.setDisplayName(format(displayname));
                        tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

                        List<String> lore;

                        if (SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-permission");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if(menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        }

                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%description%", format(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".description")))));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%identifier%", t.getIdentifier()));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tag%", t.getTag()));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%cost%", String.valueOf(t.getCost())));

                        lore = lore.stream()
                                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                                .collect(Collectors.toList());

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

                        if (UserData.getActive(menuUtil.getOwner().getUniqueId()).equalsIgnoreCase(t.getIdentifier())) {
                            if (SupremeTags.getInstance().getConfig().getBoolean("settings.active-tag-glow")) {
                                nbt.getItem().addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                            }
                        }

                        nbt.setString("identifier", t.getIdentifier());

                        tagMeta.setDisplayName(format(displayname));
                        tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

                        List<String> lore;

                        if (SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-permission");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if(menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        }

                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%description%", format(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".description")))));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%identifier%", t.getIdentifier()));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tag%", t.getTag()));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%cost%", String.valueOf(t.getCost())));

                        lore = lore.stream()
                                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                                .collect(Collectors.toList());

                        tagMeta.setLore(color(lore));

                        nbt.getItem().setItemMeta(tagMeta);

                        nbt.setString("identifier", t.getIdentifier());
                        inventory.addItem(nbt.getItem());
                    } else {
                        ItemStack tagItem = new ItemStack(Material.valueOf(material.toUpperCase()), 1);
                        ItemMeta tagMeta = tagItem.getItemMeta();
                        assert tagMeta != null;

                        NBTItem nbt = new NBTItem(tagItem);

                        if (UserData.getActive(menuUtil.getOwner().getUniqueId()).equalsIgnoreCase(t.getIdentifier())) {
                            if (SupremeTags.getInstance().getConfig().getBoolean("settings.active-tag-glow")) {
                                nbt.getItem().addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                            }
                        }

                        nbt.setString("identifier", t.getIdentifier());

                        tagMeta.setDisplayName(format(displayname));
                        tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

                        // set lore
                        List<String> lore;

                        if (SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-permission");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if(menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        }

                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%description%", format(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".description")))));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%identifier%", t.getIdentifier()));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tag%", t.getTag()));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%cost%", String.valueOf(t.getCost())));

                        lore = lore.stream()
                                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                                .collect(Collectors.toList());

                        tagMeta.setLore(color(lore));

                        nbt.getItem().setItemMeta(tagMeta);

                        nbt.setString("identifier", t.getIdentifier());
                        inventory.addItem(nbt.getItem());
                    }
                    // if permission == none
                } else if (menuUtil.getOwner().hasPermission("supremetags.tag.*") && !menuUtil.getOwner().hasPermission(permission) && permission.equalsIgnoreCase("none")) {
                    if (material.contains("hdb-")) {

                        int id = Integer.parseInt(material.replaceAll("hdb-", ""));

                        HeadDatabaseAPI api = new HeadDatabaseAPI();

                        ItemStack tagItem = api.getItemHead(String.valueOf(id));
                        ItemMeta tagMeta = tagItem.getItemMeta();
                        assert tagMeta != null;

                        NBTItem nbt = new NBTItem(tagItem);

                        nbt.setString("identifier", t.getIdentifier());

                        if (UserData.getActive(menuUtil.getOwner().getUniqueId()).equalsIgnoreCase(t.getIdentifier())) {
                            if (SupremeTags.getInstance().getConfig().getBoolean("settings.active-tag-glow")) {
                                nbt.getItem().addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                            }
                        }

                        tagMeta.setDisplayName(format(displayname));
                        tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

                        List<String> lore;

                        if (SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-permission");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if(menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        }

                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%description%", format(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".description")))));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%identifier%", t.getIdentifier()));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tag%", t.getTag()));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%cost%", String.valueOf(t.getCost())));

                        lore = lore.stream()
                                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                                .collect(Collectors.toList());

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

                        if (UserData.getActive(menuUtil.getOwner().getUniqueId()).equalsIgnoreCase(t.getIdentifier())) {
                            if (SupremeTags.getInstance().getConfig().getBoolean("settings.active-tag-glow")) {
                                nbt.getItem().addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                            }
                        }

                        tagMeta.setDisplayName(format(displayname));
                        tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

                        List<String> lore;

                        if (SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-permission");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if(menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        }

                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%description%", format(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".description")))));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%identifier%", t.getIdentifier()));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tag%", t.getTag()));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%cost%", String.valueOf(t.getCost())));

                        lore = lore.stream()
                                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                                .collect(Collectors.toList());

                        tagMeta.setLore(color(lore));

                        nbt.getItem().setItemMeta(tagMeta);

                        nbt.setString("identifier", t.getIdentifier());
                        inventory.addItem(nbt.getItem());
                    } else {
                        ItemStack tagItem = new ItemStack(Material.valueOf(material.toUpperCase()), 1);
                        ItemMeta tagMeta = tagItem.getItemMeta();
                        assert tagMeta != null;

                        NBTItem nbt = new NBTItem(tagItem);

                        if (UserData.getActive(menuUtil.getOwner().getUniqueId()).equalsIgnoreCase(t.getIdentifier())) {
                            if (SupremeTags.getInstance().getConfig().getBoolean("settings.active-tag-glow")) {
                                nbt.getItem().addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                            }
                        }

                        nbt.setString("identifier", t.getIdentifier());

                        tagMeta.setDisplayName(format(displayname));
                        tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

                        List<String> lore;

                        if (SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-permission");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if(menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        }

                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%description%", format(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".description")))));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%identifier%", t.getIdentifier()));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tag%", t.getTag()));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%cost%", String.valueOf(t.getCost())));

                        lore = lore.stream()
                                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                                .collect(Collectors.toList());

                        tagMeta.setLore(color(lore));

                        nbt.getItem().setItemMeta(tagMeta);

                        nbt.setString("identifier", t.getIdentifier());
                        inventory.addItem(nbt.getItem());
                    }
                } else if (!menuUtil.getOwner().hasPermission("supremetags.tag.*") && !menuUtil.getOwner().hasPermission(permission)) {
                    if (material.contains("hdb-")) {

                        int id = Integer.parseInt(material.replaceAll("hdb-", ""));

                        HeadDatabaseAPI api = new HeadDatabaseAPI();

                        ItemStack tagItem = api.getItemHead(String.valueOf(id));
                        ItemMeta tagMeta = tagItem.getItemMeta();
                        assert tagMeta != null;

                        NBTItem nbt = new NBTItem(tagItem);

                        nbt.setString("identifier", t.getIdentifier());

                        tagMeta.setDisplayName(format(displayname));
                        tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

                        if (UserData.getActive(menuUtil.getOwner().getUniqueId()).equalsIgnoreCase(t.getIdentifier())) {
                            if (SupremeTags.getInstance().getConfig().getBoolean("settings.active-tag-glow")) {
                                nbt.getItem().addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                            }
                        }

                        List<String> lore;

                        if (SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-permission");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if(menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        }
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%description%", format(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".description")))));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%identifier%", t.getIdentifier()));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tag%", t.getTag()));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%cost%", String.valueOf(t.getCost())));

                        lore = lore.stream()
                                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                                .collect(Collectors.toList());

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

                        if (UserData.getActive(menuUtil.getOwner().getUniqueId()).equalsIgnoreCase(t.getIdentifier())) {
                            if (SupremeTags.getInstance().getConfig().getBoolean("settings.active-tag-glow")) {
                                nbt.getItem().addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                            }
                        }

                        tagMeta.setDisplayName(format(displayname));
                        tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

                        List<String> lore;

                        if (SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-permission");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if(menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        }

                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%description%", format(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".description")))));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%identifier%", t.getIdentifier()));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tag%", t.getTag()));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%cost%", String.valueOf(t.getCost())));

                        lore = lore.stream()
                                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                                .collect(Collectors.toList());

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

                        if (UserData.getActive(menuUtil.getOwner().getUniqueId()).equalsIgnoreCase(t.getIdentifier())) {
                            if (SupremeTags.getInstance().getConfig().getBoolean("settings.active-tag-glow")) {
                                nbt.getItem().addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                            }
                        }

                        tagMeta.setDisplayName(format(displayname));
                        tagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        tagMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

                        List<String> lore;

                        if (SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-permission");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view") && menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else if (SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system") && !menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.locked-lore");
                        } else if(menuUtil.getOwner().hasPermission(permission)) {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        } else {
                            lore = SupremeTags.getInstance().getConfig().getStringList("gui.tag-menu-none-categories.tag-item.unlocked-lore");
                        }

                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%description%", format(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".description")))));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%identifier%", t.getIdentifier()));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tag%", t.getTag()));
                        lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%cost%", String.valueOf(t.getCost())));

                        lore = lore.stream()
                                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                                .collect(Collectors.toList());

                        tagMeta.setLore(color(lore));

                        nbt.getItem().setItemMeta(tagMeta);

                        nbt.setString("identifier", t.getIdentifier());
                        inventory.addItem(nbt.getItem());
                    }
                }
                currentItemsOnPage++;
            }
        }
    }

    // ================================================================

    public void getTagItemsEditor() {
        Map<String, Tag> tags = SupremeTags.getInstance().getTagManager().getTags();

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

                String permission = SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".permission");

                String displayname;

                if (SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".displayname") != null) {
                    displayname = Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".displayname")).replace("%tag%", t.getTag());
                } else {
                    displayname = format("&7Tag: " + t.getTag());
                }

                if (SupremeTags.getInstance().isPlaceholderAPI()) {
                    displayname = replacePlaceholders(menuUtil.getOwner(), displayname);
                }

                String material;

                if (SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".display-item") != null) {
                    material = SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".display-item");
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
                    tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                    tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    tagMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

                    // set lore
                    ArrayList<String> lore = (ArrayList<String>) SupremeTags.getInstance().getConfig().getStringList("gui.tag-editor-menu.tag-item.lore");
                    lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%description%", format(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".description")))));
                    lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%identifier%", t.getIdentifier()));
                    lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tag%", t.getTag()));

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
                    tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                    tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    tagMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

                    // set lore
                    ArrayList<String> lore = (ArrayList<String>) SupremeTags.getInstance().getConfig().getStringList("gui.tag-editor-menu.tag-item.lore");
                    lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%description%", format(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".description")))));
                    lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%identifier%", t.getIdentifier()));
                    lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tag%", t.getTag()));

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
                    tagMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                    tagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    tagMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

                    // set lore
                    ArrayList<String> lore = (ArrayList<String>) SupremeTags.getInstance().getConfig().getStringList("gui.tag-editor-menu.tag-item.lore");
                    lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%description%", format(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + t.getIdentifier() + ".description")))));
                    lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%identifier%", t.getIdentifier()));
                    lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s).replaceAll("%tag%", t.getTag()));

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