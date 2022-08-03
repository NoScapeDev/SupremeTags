package net.noscape.project.supremetags;

import net.noscape.project.supremetags.commands.*;
import net.noscape.project.supremetags.handlers.hooks.*;
import net.noscape.project.supremetags.handlers.menu.*;
import net.noscape.project.supremetags.listeners.*;
import net.noscape.project.supremetags.managers.*;
import net.noscape.project.supremetags.storage.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class SupremeTags extends JavaPlugin {

    private static SupremeTags instance;
    private final TagManager tagManager = new TagManager();

    private static String connectionURL;
    private static Database data;
    private final UserData userData = new UserData();

    private static final HashMap<Player, MenuUtil> menuUtilMap = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        connectionURL = "jdbc:h2:" + getDataFolder().getAbsolutePath() + "/database";
        data = new Database(connectionURL);

        reloadConfig();

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PAPI(this).register();
        }

        Objects.requireNonNull(getCommand("tags")).setExecutor(new Tags());
        Objects.requireNonNull(getCommand("mytag")).setExecutor(new Tags());

        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);

        tagManager.loadTags();
    }

    @Override
    public void onDisable() {
        tagManager.unloadTags();
    }

    public static SupremeTags getInstance() { return instance; }

    public TagManager getTagManager() { return tagManager; }

    public static MenuUtil getMenuUtil(Player player) {
        MenuUtil menuUtil;

        if (menuUtilMap.containsKey(player)) {
            return menuUtilMap.get(player);
        } else {
            menuUtil = new MenuUtil(player, UserData.getActive(player));
            menuUtilMap.put(player, menuUtil);
        }

        return menuUtil;
    }

    public static String getConnectionURL() {
        return connectionURL;
    }

    public UserData getUserData() { return userData; }

    public static Database getDatabase() { return data; }

    @Override
    public void reloadConfig() {
        super.reloadConfig();

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

}
