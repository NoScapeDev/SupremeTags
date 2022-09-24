package net.noscape.project.supremetags.utils;

import net.md_5.bungee.api.ChatColor;
import net.noscape.project.supremetags.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;

import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

public class Utils {

    public static String format(String message) {
        if (SupremeTags.getInstance().isLegacyFormat()) {
            message = message.replace(">>", "").replace("<<", "");
            Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]){6}");
            Matcher matcher = hexPattern.matcher(message);
            while (matcher.find()) {
                ChatColor hexColor = ChatColor.of(matcher.group().substring(1));
                String before = message.substring(0, matcher.start());
                String after = message.substring(matcher.end());
                message = before + hexColor + after;
                matcher = hexPattern.matcher(message);
            }
            return ChatColor.translateAlternateColorCodes('&', message);
        } else {
            message = message.replace(">>", "").replace("<<", "");
            Pattern hexPattern = Pattern.compile("#([A-Fa-f0-9]){6}");
            Matcher matcher = hexPattern.matcher(message);
            while (matcher.find()) {
                ChatColor hexColor = ChatColor.of(matcher.group().substring(1));
                String before = message.substring(0, matcher.start());
                String after = message.substring(matcher.end());
                message = before + hexColor + after;
                matcher = hexPattern.matcher(message);
            }
            return ChatColor.translateAlternateColorCodes('&', message);
        }
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

}
