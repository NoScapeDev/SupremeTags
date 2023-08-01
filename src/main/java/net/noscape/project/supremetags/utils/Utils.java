package net.noscape.project.supremetags.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.noscape.project.supremetags.SupremeTags;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {

    private static Pattern p1 = Pattern.compile("\\{#([0-9A-Fa-f]{6})\\}");
    private static Pattern p2 = Pattern.compile("&#([A-Fa-f0-9]){6}");
    private static Pattern p3 = Pattern.compile("#([A-Fa-f0-9]){6}");

    public static String format(String message) {
        // Check if the server version is less than 1.16
        if (!isVersionAtLeast("1.16")) {
            // Now translate normal color codes
            return org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
        } else {
            // Server is 1.16 or above, so process both hex and normal color codes
            if (SupremeTags.getInstance().isCMIHex()) {
                Matcher match = p1.matcher(message);
                while (match.find()) {
                    getRGB(message);
                }
                return ChatColor.translateAlternateColorCodes('&', message);
            } else if (SupremeTags.getInstance().isLegacyFormat()) {
                message = message.replace(">>", "").replace("<<", "");
                Matcher matcher = p2.matcher(message);
                while (matcher.find()) {
                    ChatColor hexColor = ChatColor.of(matcher.group().substring(1));
                    String before = message.substring(0, matcher.start());
                    String after = message.substring(matcher.end());
                    message = before + hexColor + after;
                    matcher = p2.matcher(message);
                }
                return ChatColor.translateAlternateColorCodes('&', message);
            } else {
                message = message.replace(">>", "").replace("<<", "");
                Matcher matcher = p3.matcher(message);
                while (matcher.find()) {
                    ChatColor hexColor = ChatColor.of(matcher.group().substring(1));
                    String before = message.substring(0, matcher.start());
                    String after = message.substring(matcher.end());
                    message = before + hexColor + after;
                    matcher = p3.matcher(message);
                }
                return ChatColor.translateAlternateColorCodes('&', message);
            }
        }
    }

    public static boolean isVersionAtLeast(String version) {
        String serverVersion = Bukkit.getServer().getVersion();
        return serverVersion.contains(version);
    }

    public static String colorizeRGB(String input) {
        Matcher matcher = p1.matcher(input);
        String color;
        while (matcher.find()) {
            color = matcher.group(1);
            if (color == null) {
                color = matcher.group(2);
            }
            input = input.replace(matcher.group(), ChatColor.of(color) + "");
        }
        return input;
    }

    public static void addPerm(OfflinePlayer player, String permission) {
        for (World world : Bukkit.getWorlds())
            SupremeTags.getPermissions().playerAdd(world.getName(), player, permission);
    }

    public static void removePerm(OfflinePlayer player, String permission) {
        for (World world : Bukkit.getWorlds())
            SupremeTags.getPermissions().playerRemove(world.getName(), player, permission);
    }

    public static boolean hasAmount(Player player, double cost) {
        return SupremeTags.getEconomy().has(player, cost);
    }

    public static void take(Player player, double cost) {
        SupremeTags.getEconomy().withdrawPlayer(player, cost);
    }

    public static String deformat(String str) {
        return ChatColor.stripColor(format(str));
    }

    public static void msgPlayer(Player player, String... str) {
        for (String msg : str) {
            player.sendMessage(format(msg));
        }
    }

    public static void msgPlayer(CommandSender player, String... str) {
        for (String msg : str) {
            player.sendMessage(format(msg));
        }
    }

    public static void titlePlayer(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(format(title), format(subtitle), fadeIn, stay, fadeOut);
    }

    public static void soundPlayer(Player player, Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public static List<String> color(List<String> lore){
        return lore.stream().map(Utils::format).collect(Collectors.toList());
    }

    private static Pattern rgbPat = Pattern.compile("(?:#|0x)(?:[a-f0-9]{3}|[a-f0-9]{6})\\b|(?:rgb|hsl)a?\\([^\\)]*\\)");
    public static String getRGB(String msg) {
        String temp = msg;
        try {

            String status = "none";
            String r = "";
            String g = "";
            String b = "";
            Matcher match = rgbPat.matcher(msg);
            while (match.find()) {
                String color = msg.substring(match.start(), match.end());
                for (char character : msg.substring(match.start(), match.end()).toCharArray()) {
                    switch (character) {
                        case '(':
                            status = "r";
                            continue;
                        case ',':
                            switch (status) {
                                case "r":
                                    status = "g";
                                    continue;
                                case "g":
                                    status = "b";
                                    continue;
                                default:
                                    break;
                            }
                        default:
                            switch (status) {
                                case "r":
                                    r = r + character;
                                    continue;
                                case "g":
                                    g = g + character;
                                    continue;
                                case "b":
                                    b = b + character;
                                    continue;
                            }
                            break;
                    }


                }
                b = b.replace(")", "");
                Color col = new Color(Integer.parseInt(r), Integer.parseInt(g), Integer.parseInt(b));
                temp = temp.replaceFirst("(?:#|0x)(?:[a-f0-9]{3}|[a-f0-9]{6})\\b|(?:rgb|hsl)a?\\([^\\)]*\\)", ChatColor.of(col) + "");
                r = "";
                g = "";
                b = "";
                status = "none";
            }
        } catch (Exception e) {
            return msg;
        }
        return temp;
    }

    public static String replacePlaceholders(Player user, String base) {
        return PlaceholderAPI.setPlaceholders(user, base);
    }

    public static ItemStack createSkull(String baseheadtexture64) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);

        if (baseheadtexture64 == null || baseheadtexture64.isEmpty()) {
            return skull;
        }

        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        assert skullMeta != null;
        skullMeta.setOwningPlayer(null);

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", baseheadtexture64));

        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        skull.setItemMeta(skullMeta);
        return skull;
    }

}