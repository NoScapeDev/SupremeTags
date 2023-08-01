package net.noscape.project.supremetags.managers;

import de.tr7zw.nbtapi.NBTItem;
import me.arcaniax.hdb.api.PlayerClickHeadEvent;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.noscape.project.supremetags.SupremeTagsPremium;
import net.noscape.project.supremetags.handlers.Tag;
import net.noscape.project.supremetags.storage.UserData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.noscape.project.supremetags.utils.Utils.*;

public class VoucherManager {

    public VoucherManager() {}

    public void withdrawTag(Player player, String tag_name) {
        if (isInventoryFull(player)) {
            msgPlayer(player, "&8[&6&lTags&8] &7Inventory full, remove some items.");
            return;
        }

        if (!SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.tag-vouchers")) {
            msgPlayer(player, "&8[&6&lTags&8] &7Tags vouchers are disabled!");
            return;
        }

        if (UserData.getActive(player.getUniqueId()).equalsIgnoreCase(tag_name)) {
            msgPlayer(player, "&8[&6&lTags&8] &7You can't withdraw a tag you've selected!");
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) {
            msgPlayer(player, "&8[&6&lTags&8] &7Luckperms not found, cancelling voucher!");
            return;
        }

        Tag t = SupremeTagsPremium.getInstance().getTagManager().getTag(tag_name);

        if (t == null) {
            msgPlayer(player, "&8[&6&lTags&8] &7This tag does not exist!");
            return;
        }

        if (!player.hasPermission(t.getPermission())) {
            msgPlayer(player, "&8[&6&lTags&8] &7Tou don't own this tag!");
            return;
        }

        if (!t.isWithdrawable()) {
            msgPlayer(player, "&8[&6&lTags&8] &7This tag is not withdrawable!");
            return;
        }

        /*
         * REMOVE THE PERMISSION FROM THE PLAYER.
         */
        removePerm(player, t.getPermission());

        ItemStack tag = new ItemStack(Material.NAME_TAG, 1);
        ItemMeta tagMeta = tag.getItemMeta();

        assert tagMeta != null;

        // Create a unique NamespacedKey to identify your custom data
        NamespacedKey identifierKey = new NamespacedKey(SupremeTagsPremium.getInstance(), "identifier");

        // Get the PersistentDataContainer from the ItemMeta
        PersistentDataContainer dataContainer = tagMeta.getPersistentDataContainer();

        // Save the custom data to the PersistentDataContainer
        dataContainer.set(identifierKey, PersistentDataType.STRING, t.getIdentifier());

        tagMeta.setDisplayName(format("&7Tag: " + t.getTag().get(0)));

        List<String> lore = new ArrayList<>();
        lore.add("&7Right-Click to assign tag: &f" + t.getIdentifier());
        tagMeta.setLore(color(lore));

        // Apply the modified ItemMeta back to the ItemStack
        tag.setItemMeta(tagMeta);

        player.getInventory().addItem(tag);

        msgPlayer(player, "&8[&6&lTags&8] &7Tag &f" + t.getIdentifier() + " &7Withdrawn!");
    }

    public boolean isInventoryFull(Player player) {
        PlayerInventory inventory = player.getInventory();
        int firstEmptySlot = inventory.firstEmpty();
        return firstEmptySlot == -1;
    }
}
