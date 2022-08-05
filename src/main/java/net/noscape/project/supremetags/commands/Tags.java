package net.noscape.project.supremetags.commands;

import net.noscape.project.supremetags.*;
import net.noscape.project.supremetags.guis.*;
import net.noscape.project.supremetags.storage.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.configuration.file.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;

import static net.noscape.project.supremetags.utils.Utils.*;

public class Tags implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            if (cmd.getName().equalsIgnoreCase("tags")) {
                if (args.length == 0) {
                } else if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("create")) {
                        String name = args[1];
                        String tag = args[2];

                        SupremeTags.getInstance().getTagManager().createTag(sender, name, tag, "&7My tag is " + name, "supremetags.tag." + name);
                    } else if (args[0].equalsIgnoreCase("settag")) {
                        String name = args[1];
                        String tag = args[2];
                        SupremeTags.getInstance().getTagManager().setTag(sender, name, tag);
                    } else if (args[0].equalsIgnoreCase("set")) {
                        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                        String identifier = args[2];

                        if (SupremeTags.getInstance().getTagManager().getTags().containsKey(identifier)) {
                            UserData.setActive(target, identifier);
                            msgPlayer(sender, "&8[&b&lTag&8] &7Set &b" + target.getName() + "'s &7tag to &b" + identifier);
                        } else {
                            msgPlayer(sender, "&cThis tag does not exist.");
                        }
                    }
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("reload")) {

                        SupremeTags.getInstance().reloadConfig();

                        SupremeTags.getInstance().getTagManager().unloadTags();
                        SupremeTags.getInstance().getTagManager().loadTags();
                        msgPlayer(sender, "&6[TAG] &7Reloaded plugin.");
                    } else if (args[0].equalsIgnoreCase("help")) {
                        msgPlayer(sender, "",
                                "&eSupremeTags Admin Help:",
                                "",
                                "&6/tags &7- will open the tag menu.",
                                "&6/tags create <identifier> <tag> &7- creates a new tag.",
                                "&6/tags delete <identifier> &7- creates a new tag.",
                                "&6/tags settag <identifier> <tag> &7- sets tag style for the existing tag.",
                                "&6/tags set <player> <identifier> &7- sets a new tag for that player.",
                                "&6/tags reset <player> &7- resets the players tag to None.",
                                "&6/tags merge &7- merges deluxetags into supremetags.",
                                "&6/tags reload &7- reloads the config.yml & unloads/loads tags.",
                                "&6/tags help &7- displays this help message.",
                                "");
                    }
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("delete")) {
                        String name = args[1];
                        SupremeTags.getInstance().getTagManager().deleteTag(sender, name);
                    } else if (args[0].equalsIgnoreCase("reset")) {
                        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                        UserData.setActive(target, "None");
                        msgPlayer(sender, "&8[&b&lTag&8] &7Reset &b" + target.getName() + "'s &7tag back to None.");
                    } else {
                        msgPlayer(sender, "",
                                "&eSupremeTags Admin Help:",
                                "",
                                "&6/tags &7- will open the tag menu.",
                                "&6/tags create <identifier> <tag> &7- creates a new tag.",
                                "&6/tags delete <identifier> &7- creates a new tag.",
                                "&6/tags settag <identifier> <tag> &7- sets tag style for the existing tag.",
                                "&6/tags set <player> <identifier> &7- sets a new tag for that player.",
                                "&6/tags reset <player> &7- resets the players tag to None.",
                                "&6/tags merge &7- merges deluxetags into supremetags.",
                                "&6/tags reload &7- reloads the config.yml & unloads/loads tags.",
                                "&6/tags help &7- displays this help message.",
                                "");
                    }
                }
            }
            return true;
        }

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("tags")) {
            if (args.length == 0) {
                if (player.hasPermission("supremetags.menu")) {
                    if (hasTags(player)) {
                        new TagMenu(SupremeTags.getMenuUtil(player)).open();
                    } else {
                        msgPlayer(player, "&cYou have no tags yet.");
                    }
                } else {
                    msgPlayer(player, "&cNo Permission, required permission: &7'supremetags.menu'");
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("create")) {
                    if (player.hasPermission("supremetags.admin")) {
                        String name = args[1];
                        String tag = args[2];

                        SupremeTags.getInstance().getTagManager().createTag(player, name, tag, "&7My tag is " + name, "supremetags.tag." + name);
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
                        SupremeTags.getInstance().reloadConfig();

                        SupremeTags.getInstance().getTagManager().unloadTags();
                        SupremeTags.getInstance().getTagManager().loadTags();
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
                                "&6/tags settag <identifier> <tag> &7- sets tag style for the existing tag.",
                                "&6/tags set <player> <identifier> &7- sets a new tag for that player.",
                                "&6/tags reset <player> &7- resets the players tag to None.",
                                "&6/tags merge &7- merges deluxetags into supremetags.",
                                "&6/tags reload &7- reloads the config.yml & unloads/loads tags.",
                                "&6/tags help &7- displays this help message.",
                                "");
                    } else {
                        msgPlayer(player, "&cNo Permission.");
                    }
                } else if (args[0].equalsIgnoreCase("merge")) {
                    if (player.hasPermission("supremetags.admin")) {
                        File configFile = new File(Bukkit.getServer().getWorldContainer().getAbsolutePath() + "/plugins/DeluxeTags/config.yml"); // First we will load the file.
                        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile); // Now we will load the file into a FileConfiguration.

                        //deluxetags:
                        //example:
                        //order: 1
                        //tag: '&8[&bDeluxeTags&8]'
                        //description: '&cAwarded for using DeluxeTags!'
                        //permission: deluxetags.tag.example

                        if (config.getConfigurationSection("deluxetags") != null) {
                            for (String identifier : Objects.requireNonNull(config.getConfigurationSection("deluxetags")).getKeys(false)) {
                                if (!SupremeTags.getInstance().getTagManager().getTags().containsKey(identifier)) {

                                    String tag = config.getString("deluxetags." + identifier + ".tag");
                                    String description = config.getString("deluxetags." + identifier + ".description");
                                    String permission = config.getString("deluxetags." + identifier + ".permission");

                                    SupremeTags.getInstance().getTagManager().createTag(player, identifier, tag, description, permission);
                                }

                                msgPlayer(player, "&6Merger: &7Added all new tags from &6DeluxeTags&7 were added, any existing tags with the same name won't be added.");
                            }
                        } else {
                            msgPlayer(player, "&6Error: &7DeluxeTags tag config area is empty.");
                        }
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
                            "&6/tags settag <identifier> <tag> &7- sets tag style for the existing tag.",
                            "&6/tags set <player> <identifier> &7- sets a new tag for that player.",
                            "&6/tags reset <player> &7- resets the players tag to None.",
                            "&6/tags merge &7- merges deluxetags into supremetags.",
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

    public boolean hasTags(Player player) {
        for (String tag : SupremeTags.getInstance().getTagManager().getTags().keySet())

            if (player.hasPermission(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + tag + ".permission"))))
                return true;
        return false;
    }
}
