package net.noscape.project.supremetags.listeners;

import net.noscape.project.supremetags.SupremeTags;
import net.noscape.project.supremetags.enums.EditingType;
import net.noscape.project.supremetags.handlers.Editor;
import net.noscape.project.supremetags.handlers.SetupTag;
import net.noscape.project.supremetags.handlers.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static net.noscape.project.supremetags.utils.Utils.*;

public class SetupListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (!SupremeTags.getInstance().getEditorList().containsKey(player) && SupremeTags.getInstance().getSetupList().containsKey(player)) {

            String message = e.getMessage();

            SetupTag setup = SupremeTags.getInstance().getSetupList().get(player);

            e.setCancelled(true);

            if (setup.getStage() == 1) {

                setup.setIdentifier(deformat(message));
                setup.setStage(2);

                msgPlayer(player,
                        "&6&lStage 2: &7What do you want the tag to look like? &6&o(type in normal chat)",
                        "&eUse normal or hex color formats (&#<code> or &-0-9/a-f) *without the -*");
            } else if (setup.getStage() == 2) {
                setup.setTag(message);

                Tag tag = new Tag(setup.getIdentifier(), setup.getTag(), "");
                SupremeTags.getInstance().getPlayerManager().getPlayerTags().put(player.getUniqueId(), tag);

                SupremeTags.getInstance().getPlayerManager().save(tag, player);
                SupremeTags.getInstance().getPlayerManager().load(player);

                SupremeTags.getInstance().getSetupList().remove(player);

                msgPlayer(player, "&6&lComplete: &7New Tag: &6" + setup.getIdentifier() + " &7- " + format(setup.getTag()) + " &7has been created.");
            }
        }
    }
}
