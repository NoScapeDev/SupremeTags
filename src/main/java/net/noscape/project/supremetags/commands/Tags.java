package net.noscape.project.supremetags.commands;

import net.noscape.project.supremetags.*;
import net.noscape.project.supremetags.guis.*;
import net.noscape.project.supremetags.guis.tageditor.TagEditorMenu;
import net.noscape.project.supremetags.handlers.Tag;
import net.noscape.project.supremetags.storage.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
                    sendHelp(sender);
                } else if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("create")) {
                        String name = args[1];
                        String tag = args[2];

                        SupremeTags.getInstance().getTagManager().createTag(sender, name, tag, "&7My tag is " + name, "supremetags.tag." + name, 0);
                    } else if (args[0].equalsIgnoreCase("settag")) {
                        String name = args[1];
                        String tag = args[2];
                        SupremeTags.getInstance().getTagManager().setTag(sender, name, tag);
                    } else if (args[0].equalsIgnoreCase("set")) {
                        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                        String identifier = args[2];

                        if (SupremeTags.getInstance().getTagManager().getTags().containsKey(identifier)) {
                            UserData.setActive(target, identifier);
                            msgPlayer(sender, "&8[&6&lTag&8] &7Set &b" + target.getName() + "'s &7tag to &b" + identifier);
                        } else {
                            msgPlayer(sender, "&cThis tag does not exist.");
                        }
                    }
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("reload")) {

                        SupremeTags.getInstance().reloadConfig();

                        SupremeTags.getInstance().getTagManager().unloadTags();
                        SupremeTags.getInstance().getTagManager().loadTags();

                        SupremeTags.getInstance().getTagManager().getDataItem().clear();

                        SupremeTags.getInstance().getTagManager().setCost(SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system"));

                        if (Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("settings.layout")).equalsIgnoreCase("layout1")) {
                            SupremeTags.setLayout("layout1");
                        } else if (Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("settings.layout")).equalsIgnoreCase("layout2")) {
                            SupremeTags.setLayout("layout2");
                        }

                        SupremeTags.getInstance().getCategoryManager().loadCategories();
                        SupremeTags.getInstance().getCategoryManager().loadCategoriesTags();
                        msgPlayer(sender, "&8[&6&lTag&8] &7Reloaded plugin.");
                    } else if (args[0].equalsIgnoreCase("help")) {
                        sendHelp(sender);
                    }
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("delete")) {
                        String name = args[1];
                        SupremeTags.getInstance().getTagManager().deleteTag(sender, name);
                    } else if (args[0].equalsIgnoreCase("reset")) {
                        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                        if (SupremeTags.getInstance().getConfig().isBoolean("settings.forced-tag")) {
                            String defaultTag = SupremeTags.getInstance().getConfig().getString("settings.default-tag");

                            UserData.setActive(target, defaultTag);
                            msgPlayer(sender, "&8[&6&lTag&8] &7Reset &b" + target.getName() + "'s &7tag back to " + defaultTag);
                        } else {
                            UserData.setActive(target, "None");
                            msgPlayer(sender, "&8[&6&lTag&8] &7Reset &b" + target.getName() + "'s &7tag back to None.");
                        }
                    } else {
                        sendHelp(sender);
                    }
                }
            }
            return true;
        }

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("tags")) {
            if (args.length == 0) {
                if (player.hasPermission("supremetags.menu")) {
                    if (!SupremeTags.getInstance().isDisabledWorldsTag()) {
                        if (!SupremeTags.getInstance().getTagManager().isCost()) {
                            if (hasTags(player)) {
                                if (SupremeTags.getInstance().getConfig().getBoolean("settings.categories")) {
                                    new MainMenu(SupremeTags.getMenuUtil(player)).open();
                                } else {
                                    new TagMenu(SupremeTags.getMenuUtil(player)).open();
                                }
                            } else {
                                msgPlayer(player, "&cYou have no tags yet.");
                            }
                        } else {
                            if (SupremeTags.getInstance().getConfig().getBoolean("settings.categories")) {
                                new MainMenu(SupremeTags.getMenuUtil(player)).open();
                            } else {
                                new TagMenu(SupremeTags.getMenuUtil(player)).open();
                            }
                        }
                    } else {
                        for (String world : SupremeTags.getInstance().getConfig().getStringList("settings.disabled-worlds")) {
                            if (player.getWorld().getName().equalsIgnoreCase(world)) {
                                msgPlayer(player, "&cTag command is disabled in this world.");
                            } else {
                                if (!SupremeTags.getInstance().getTagManager().isCost()) {
                                    if (hasTags(player)) {
                                        if (SupremeTags.getInstance().getConfig().getBoolean("settings.categories")) {
                                            new MainMenu(SupremeTags.getMenuUtil(player)).open();
                                        } else {
                                            new TagMenu(SupremeTags.getMenuUtil(player)).open();
                                        }
                                    } else {
                                        msgPlayer(player, "&cYou have no tags yet.");
                                    }
                                } else {
                                    if (SupremeTags.getInstance().getConfig().getBoolean("settings.categories")) {
                                        new MainMenu(SupremeTags.getMenuUtil(player)).open();
                                    } else {
                                        new TagMenu(SupremeTags.getMenuUtil(player)).open();
                                    }
                                }
                            }
                            break;
                        }
                    }
                } else {
                    msgPlayer(player, "&cNo Permission, required permission: &7'supremetags.menu'");
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("create")) {
                    if (player.hasPermission("supremetags.admin")) {
                        String name = args[1];
                        String tag = args[2];

                        SupremeTags.getInstance().getTagManager().createTag(player, name, tag, "&7My tag is " + name, "supremetags.tag." + name, 0);
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
                } else if (args[0].equalsIgnoreCase("setcategory")) {
                        if (player.hasPermission("supremetags.admin")) {
                            String name = args[1];
                            String category = args[2];

                            SupremeTags.getInstance().getTagManager().setCategory(player, name, category);
                        } else {
                            msgPlayer(player, "&cNo Permission.");
                        }
                } else if (args[0].equalsIgnoreCase("set")) {
                    if (player.hasPermission("supremetags.admin")) {
                        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                        String identifier = args[2];

                        if (SupremeTags.getInstance().getTagManager().getTags().containsKey(identifier)) {
                            UserData.setActive(target, identifier);
                            msgPlayer(player, "&8[&6&lTag&8] &7Set &b" + target.getName() + "'s &7tag to &b" + identifier);
                        } else {
                            msgPlayer(player, "&cThis tag does not exist.");
                        }
                    } else {
                        msgPlayer(player, "&cNo Permission.");
                    }
                } else {
                    sendHelp(sender);
                }
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    if (player.hasPermission("supremetags.admin")) {
                        SupremeTags.getInstance().reloadConfig();

                        SupremeTags.getInstance().getTagManager().unloadTags();
                        SupremeTags.getInstance().getTagManager().loadTags();

                        SupremeTags.getInstance().getTagManager().getDataItem().clear();

                        SupremeTags.getInstance().getTagManager().setCost(SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system"));

                        if (Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("settings.layout")).equalsIgnoreCase("layout1")) {
                            SupremeTags.setLayout("layout1");
                        } else if (Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("settings.layout")).equalsIgnoreCase("layout2")) {
                            SupremeTags.setLayout("layout2");
                        }

                        SupremeTags.getInstance().getCategoryManager().loadCategories();
                        SupremeTags.getInstance().getCategoryManager().loadCategoriesTags();
                        msgPlayer(player, "&8[&6&lTag&8] &7Reloaded plugin.");
                    } else {
                        msgPlayer(player, "&cNo Permission.");
                    }
                } else if (args[0].equalsIgnoreCase("help")) {
                    if (player.hasPermission("supremetags.admin")) {
                        sendHelp(player);
                    } else {
                        msgPlayer(player, "&cNo Permission.");
                    }
                } else if (args[0].equalsIgnoreCase("editor")) {
                        if (player.hasPermission("supremetags.admin")) {
                            new TagEditorMenu(SupremeTags.getMenuUtil(player)).open();
                        } else {
                            msgPlayer(player, "&cNo Permission.");
                        }
                } else if (args[0].equalsIgnoreCase("merge")) {
                    if (player.hasPermission("supremetags.admin")) {
                        File configFile = new File(Bukkit.getServer().getWorldContainer().getAbsolutePath() + "/plugins/DeluxeTags/config.yml"); // First we will load the file.
                        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile); // Now we will load the file into a FileConfiguration.

                        if (configFile.exists()) {
                            if (config.getConfigurationSection("deluxetags") != null) {
                                for (String identifier : Objects.requireNonNull(config.getConfigurationSection("deluxetags")).getKeys(false)) {
                                    if (!SupremeTags.getInstance().getTagManager().getTags().containsKey(identifier)) {

                                        String tag = config.getString("deluxetags." + identifier + ".tag");
                                        String description = config.getString("deluxetags." + identifier + ".description");
                                        String permission = config.getString("deluxetags." + identifier + ".permission");

                                        SupremeTags.getInstance().getTagManager().createTag(player, identifier, tag, description, permission, 0);
                                    }

                                    msgPlayer(player, "&6Merger: &7Added all new tags from &6DeluxeTags&7 were added, any existing tags with the same name won't be added.");
                                }
                            } else {
                                msgPlayer(player, "&6Error: &7DeluxeTags tag config area is empty.");
                            }
                        } else {
                            msgPlayer(player, "&6Error: &7DeluxeTags can not be found.");
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

                        if (SupremeTags.getInstance().getConfig().isBoolean("settings.forced-tag")) {
                            String defaultTag = SupremeTags.getInstance().getConfig().getString("settings.default-tag");

                            UserData.setActive(target, defaultTag);
                            msgPlayer(player, "&8[&6&lTag&8] &7Reset &b" + target.getName() + "'s &7tag back to " + defaultTag);
                        } else {
                            UserData.setActive(target, "None");
                            msgPlayer(player, "&8[&6&lTag&8] &7Reset &b" + target.getName() + "'s &7tag back to None.");
                        }
                    } else {
                        msgPlayer(player, "&cNo Permission.");
                    }
                } else {
                    sendHelp(player);
                }
            } else {
                sendHelp(sender);
            }
        }
        return false;
    }

    public boolean hasTags(Player player) {
        for (Tag tag : SupremeTags.getInstance().getTagManager().getTags().values()) {
            if (player.hasPermission(tag.getPermission())) {
                return true;
            }
        }
        return false;
    }

    public void sendHelp(Player player) {
        msgPlayer(player, "",
                "&6&lSupremeTags Help:",
                "",
                "&e/tags &7- will open the tag menu.",
                "&e/tags create <identifier> <tag> &7- creates a new tag.",
                "&e/tags delete <identifier> &7- creates a new tag.",
                "&e/tags settag <identifier> <tag> &7- sets tag style for the existing tag.",
                "&e/tags set <player> <identifier> &7- sets a new tag for that player.",
                "&e/tags reset <player> &7- resets the players tag to None.",
                "&e/tags merge &7- merges deluxetags into supremetags.",
                "&e/tags reload &7- reloads the config.yml & unloads/loads tags.",
                "&e/tags help &7- displays this help message.",
                "");
    }

    public void sendHelp(CommandSender player) {
        msgPlayer(player, "",
                "&6&lSupremeTags Help:",
                "",
                "&e/tags &7- will open the tag menu.",
                "&e/tags create <identifier> <tag> &7- creates a new tag.",
                "&e/tags delete <identifier> &7- creates a new tag.",
                "&e/tags settag <identifier> <tag> &7- sets tag style for the existing tag.",
                "&e/tags set <player> <identifier> &7- sets a new tag for that player.",
                "&e/tags reset <player> &7- resets the players tag to None.",
                "&e/tags merge &7- merges deluxetags into supremetags.",
                "&e/tags reload &7- reloads the config.yml & unloads/loads tags.",
                "&e/tags help &7- displays this help message.",
                "");
    }
}