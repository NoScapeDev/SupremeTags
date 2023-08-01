package net.noscape.project.supremetags.guis.tageditor;

import net.noscape.project.supremetags.SupremeTagsPremium;
import net.noscape.project.supremetags.enums.EditingType;
import net.noscape.project.supremetags.guis.personaltags.PersonalTagEditorMenu;
import net.noscape.project.supremetags.handlers.Editor;
import net.noscape.project.supremetags.handlers.Tag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

import static net.noscape.project.supremetags.utils.Utils.msgPlayer;

public class EditorListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent e) {
        edit(e);
    }

    public void edit(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (!SupremeTagsPremium.getInstance().getEditorList().containsKey(player)) return;

        String message = e.getMessage();

        Editor editor = SupremeTagsPremium.getInstance().getEditorList().get(player);

        EditingType type = editor.getType();

        e.setCancelled(true);

        if (!editor.isPersonalEdit()) {
            Tag tag = SupremeTagsPremium.getInstance().getTagManager().getTag(editor.getIdentifier());
            switch (type) {
                case CHANGING_TAG:
                    List<String> tagList = tag.getTag();
                    tagList.add(message);

                    tag.setTag(tagList);
                    break;
                case CHANGING_PERMISSION:
                    tag.setPermission(message);
                    break;
                case CHANGING_CATEGORY:
                    tag.setCategory(message);
                    break;
                case CHANGING_COST:
                    tag.setCost(Double.parseDouble(message));
                    break;
                case CHANGING_DESCRIPTION:
                    tag.setDescription(message);
                    break;
                case CHANGING_ORDER:
                    tag.setOrder(Integer.parseInt(message));
                    break;
            }

            SupremeTagsPremium.getInstance().getTagManager().saveTag(tag);

            Bukkit.getScheduler().runTaskLater(SupremeTagsPremium.getInstance(), () -> {
                new SpecificTagMenu(SupremeTagsPremium.getMenuUtilIdentifier(player, editor.getIdentifier())).open();
            }, 1L);
        } else {
            Tag tag = SupremeTagsPremium.getInstance().getPlayerManager().getTag(player.getUniqueId(), editor.getIdentifier());
            switch (type) {
                case CHANGING_TAG:
                    List<String> tagList = new ArrayList<>();
                    tagList.add(message);

                    tag.setTag(tagList);
                    break;
                case CHANGING_DESCRIPTION:
                    tag.setDescription(message);
                    break;
            }

            SupremeTagsPremium.getInstance().getPlayerManager().save(tag, player);

            Bukkit.getScheduler().runTaskLater(SupremeTagsPremium.getInstance(), () -> {
                new PersonalTagEditorMenu(SupremeTagsPremium.getMenuUtilIdentifier(player, editor.getIdentifier())).open();
            }, 1L);
        }

        SupremeTagsPremium.getInstance().getEditorList().remove(player);
        msgPlayer(player, "&8[&6&lTags&8] &7Tag has been updated.");
    }
}
