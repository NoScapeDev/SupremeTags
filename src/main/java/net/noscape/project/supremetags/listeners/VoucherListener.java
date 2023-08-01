package net.noscape.project.supremetags.listeners;

import de.tr7zw.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatColor;
import net.noscape.project.supremetags.SupremeTagsPremium;
import net.noscape.project.supremetags.handlers.Tag;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

import static net.noscape.project.supremetags.utils.Utils.addPerm;
import static net.noscape.project.supremetags.utils.Utils.msgPlayer;

public class VoucherListener implements Listener {

    private final NamespacedKey identifierKey = new NamespacedKey(SupremeTagsPremium.getInstance(), "identifier");

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand(); // Use the main hand item

        if (item != null && item.getType() == Material.NAME_TAG) {
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta == null) {
                return;
            }

            // Get the PersistentDataContainer from the ItemMeta
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();

            // Check if the item has the custom data with the specified key
            if (dataContainer.has(identifierKey, PersistentDataType.STRING)) {

                if (!SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.tag-vouchers")) {
                    msgPlayer(player, "&8[&6&lTags&8] &7Tags vouchers are disabled!");
                    return;
                }

                // Get the custom data from the PersistentDataContainer
                String identifier = dataContainer.get(identifierKey, PersistentDataType.STRING);

                Tag tag = SupremeTagsPremium.getInstance().getTagManager().getTag(identifier);

                if (tag == null) {
                    msgPlayer(player, "&8[&6&lTag&8] &7This tag doesn't exist: &f" + identifier);
                    return;
                }

                String permission = tag.getPermission();

                addPerm(player, permission);

                item.setAmount(item.getAmount() - 1);

                msgPlayer(player, "&8[&6&lTag&8] &7You have received the tag: " + tag.getTag().get(0));
            }
        }
    }
}
