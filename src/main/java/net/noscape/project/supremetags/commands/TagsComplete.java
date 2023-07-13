package net.noscape.project.supremetags.commands;

import net.noscape.project.supremetags.SupremeTags;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TagsComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (cmd.getName().equalsIgnoreCase("tags")) {
            if (args.length == 1) {
                completions.addAll(Arrays.asList("create", "settag", "setcategory", "set", "reload", "help", "list", "editor", "merge", "delete", "reset"));
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("settag") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("reset")) {
                    // Retrieve available tag names
                    completions.addAll(SupremeTags.getInstance().getTagManager().getTags().keySet());
                } else if (args[0].equalsIgnoreCase("set")) {
                    // Add code here to retrieve available player names
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        completions.add(player.getName());
                    }
                } else if (args[0].equalsIgnoreCase("setcategory")) {
                    // Retrieve available category names
                    completions.addAll(SupremeTags.getInstance().getCategoryManager().getCatorgies());
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("settag")) {
                    // Add code here to retrieve available tag values
                    completions.addAll(SupremeTags.getInstance().getTagManager().getTags().keySet());
                } else if (args[0].equalsIgnoreCase("setcategory")) {
                    String categoryName = args[1];
                    completions.addAll(SupremeTags.getInstance().getCategoryManager().getCatorgies());
                } else if (args[0].equalsIgnoreCase("set")) {
                    // Add code here to retrieve available tag names
                    completions.addAll(SupremeTags.getInstance().getTagManager().getTags().keySet());
                }
            }
        }

        return completions;
    }
}
