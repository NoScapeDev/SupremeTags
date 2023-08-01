package net.noscape.project.supremetags.commands;

import net.noscape.project.supremetags.SupremeTagsPremium;
import net.noscape.project.supremetags.guis.personaltags.PersonalTagsMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.noscape.project.supremetags.utils.Utils.msgPlayer;

public class MyTags implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        String noperm = SupremeTagsPremium.getInstance().getConfig().getString("messages.no-permission");

        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("mytags")) {
            if (player.hasPermission("supremetags.mytags")) {
                if (SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.personal-tags.enable")) {
                    new PersonalTagsMenu(SupremeTagsPremium.getMenuUtil(player)).open();
                } else {
                    msgPlayer(player, "&8[&6&lTags&8] &7Personal Tags are disabled!");
                }
            } else {
                msgPlayer(player, noperm);
            }
        }
        return false;
    }
}