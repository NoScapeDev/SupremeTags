package net.noscape.project.supremetags.commands;

import net.noscape.project.supremetags.*;
import net.noscape.project.supremetags.guis.*;
import net.noscape.project.supremetags.storage.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;

import static net.noscape.project.supremetags.utils.Utils.*;

public class Tags implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("tags")) {
            if (args.length == 0) {
                if (player.hasPermission("supremetags.menu")) {
                    new TagMenu(SupremeTags.getMenuUtil(player)).open();
                } else {
                    msgPlayer(player, "&cNo Permission, required permission: &7'supremetags.menu'");
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("create")) {
                    if (player.hasPermission("supremetags.admin")) {
                        String name = args[1];
                        String tag = args[2];

                        SupremeTags.getInstance().getTagManager().createTag(player, name, tag);
                    } else {
                        msgPlayer(player, "&cNo Permission.");
                    }
                } else if (args[0].equalsIgnoreCase("settag")) {
                    if (player.hasPermission("supremetags.admin")) {
                        String name = args[1];
                        String tag = args[2];

                        SupremeTags.getInstance().getTagManager().setTag(player, name, tag);
                    } else {
                        msgPlayer(player, "&cNo Permission.");
                    }
                } else if (args[0].equalsIgnoreCase("set")) {
                    if (player.hasPermission("supremetags.admin")) {
                        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                        String identifier = args[2];

                        if (SupremeTags.getInstance().getTagManager().getTags().containsKey(identifier)) {
                            UserData.setActive(target, identifier);
                            msgPlayer(player, "&8[&b&lTag&8] &7Set &b" + target.getName() + "'s &7tag to &b" + identifier);
                        } else {
                            msgPlayer(player, "&cThis tag does not exist.");
                        }
                    } else {
                        msgPlayer(player, "&cNo Permission.");
                    }
                }
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    if (player.hasPermission("supremetags.admin")) {
                        SupremeTags.getInstance().getTagManager().unloadTags();
                        SupremeTags.getInstance().getTagManager().loadTags();

                        SupremeTags.getInstance().saveConfig();
                        SupremeTags.getInstance().reloadConfig();
                        msgPlayer(player, "&6[TAG] &7Reloaded plugin.");
                    } else {
                        msgPlayer(player, "&cNo Permission.");
                    }
                } else if (args[0].equalsIgnoreCase("help")) {
                    if (player.hasPermission("supremetags.admin")) {
                        msgPlayer(player, "",
                                "&eSupremeTags Admin Help:",
                                "",
                                "&6/tags &7- will open the tag menu.",
                                "&6/tags create <identifier> <tag> &7- creates a new tag.",
                                "&6/tags delete <identifier> &7- creates a new tag.",
                                "&6/tags set <player> <identifier> &7- sets a new tag for that player.",
                                "&6/tags reset <player> &7- resets the players tag to None.",
                                "&6/tags reload &7- reloads the config.yml & unloads/loads tags.",
                                "&6/tags help &7- displays this help message.",
                                "");
                    } else {
                        msgPlayer(player, "&cNo Permission.");
                    }
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("delete")) {
                    if (player.hasPermission("supremetags.admin")) {
                        String name = args[1];

                        SupremeTags.getInstance().getTagManager().deleteTag(player, name);
                    } else {
                        msgPlayer(player, "&cNo Permission.");
                    }
                } else if (args[0].equalsIgnoreCase("reset")) {
                    if (player.hasPermission("supremetags.admin")) {
                        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                        UserData.setActive(target, "None");
                        msgPlayer(player, "&8[&b&lTag&8] &7Reset &b" + target.getName() + "'s &7tag back to None.");
                    } else {
                        msgPlayer(player, "&cNo Permission.");
                    }
                } else {
                    msgPlayer(player, "",
                            "&eSupremeTags Admin Help:",
                            "",
                            "&6/tags &7- will open the tag menu.",
                            "&6/tags create <identifier> <tag> &7- creates a new tag.",
                            "&6/tags delete <identifier> &7- creates a new tag.",
                            "&6/tags set <player> <identifier> &7- sets a new tag for that player.",
                            "&6/tags reset <player> &7- resets the players tag to None.",
                            "&6/tags reload &7- reloads the config.yml & unloads/loads tags.",
                            "&6/tags help &7- displays this help message.",
                            "");
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("mytag")) {
            if (args.length == 0) {
                msgPlayer(player, "&7Your tag: &e" + UserData.getActive(player));
            }
        }
        return false;
    }
}
