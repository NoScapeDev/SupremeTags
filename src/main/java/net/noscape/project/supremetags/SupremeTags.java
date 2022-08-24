package net.noscape.project.supremetags;

import net.noscape.project.supremetags.checkers.*;
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
    private final CategoryManager categoryManager = new CategoryManager();

    private static MySQL mysql;
    private static H2Database h2;
    private final H2UserData h2user = new H2UserData();
    private static String connectionURL;
    private final MySQLUserData user = new MySQLUserData();

    private static final HashMap<Player, MenuUtil> menuUtilMap = new HashMap<>();

    private final String host = getConfig().getString("data.address");
    private final int port = getConfig().getInt("data.port");
    private final String database = getConfig().getString("data.database");
    private final String username = getConfig().getString("data.username");
    private final String password = getConfig().getString("data.password");
    private final String options = getConfig().getString("data.options");

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();
        this.callMetrics();

        if (isH2()) {
            connectionURL = "jdbc:h2:" + getDataFolder().getAbsolutePath() + "/database";
            h2 = new H2Database(connectionURL);
        }

        if (isMySQL()) {
            mysql = new MySQL(host, port, database, username, password, options);
        }

        if (getConfig().getBoolean("settings.update-check")) {
            UpdateChecker updater = new UpdateChecker(this);
            updater.fetch();
            if (updater.hasUpdateAvailable()) {
                getLogger().info("------------------------------");
                getLogger().info("SupremeTags-Checker");
                getLogger().info(" ");
                getLogger().info("An update for SupremeTags has been found!");
                getLogger().info("SupremeTags-" + updater.getSpigotVersion());
                getLogger().info("You are running " + getDescription().getVersion());
                getLogger().info(" ");
                getLogger().info("Download at https://www.spigotmc.org/resources/%E2%9C%85-supremetags-%E2%9C%85-1-8-1-19-placeholderapi-support-unlimited-tags-%E2%9C%85.103140/");
                getLogger().info("------------------------------");
            } else {
                getLogger().info("------------------------------");
                getLogger().info("Running latest version of SupremeTags-" + getDescription().getVersion());
                getLogger().info("------------------------------");
            }
        }

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PAPI(this).register();
            getLogger().info("------------------------------");
            getLogger().info("PlaceholderAPI found SupremeTags! Automatically downloaded cloud.");
            getLogger().info("------------------------------");
        }

        Objects.requireNonNull(getCommand("tags")).setExecutor(new Tags());

        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);

        tagManager.loadTags();
        categoryManager.loadCategories();
    }

    @Override
    public void onDisable() {
        tagManager.unloadTags();
    }

    public static SupremeTags getInstance() { return instance; }

    public TagManager getTagManager() { return tagManager; }

    public CategoryManager getCategoryManager() { return categoryManager; }

    public static MenuUtil getMenuUtil(Player player) {
        MenuUtil menuUtil;

        if (menuUtilMap.containsKey(player)) {
            return menuUtilMap.get(player);
        } else {
            menuUtil = new MenuUtil(player, UserData.getActive(player.getUniqueId()));
            menuUtilMap.put(player, menuUtil);
        }

        return menuUtil;
    }
 
    public static MenuUtil getMenuUtil(Player player, String category) {
        MenuUtil menuUtil;

        if (menuUtilMap.containsKey(player)) {
            return menuUtilMap.get(player);
        } else {
            menuUtil = new MenuUtil(player, UserData.getActive(player.getUniqueId()), category);
            menuUtilMap.put(player, menuUtil);
        }

        return menuUtil;
    }

    public static String getConnectionURL() {
        return connectionURL;
    }

    public H2UserData getUserData() { return h2user; }

    public static H2Database getDatabase() { return h2; }

    public MySQLUserData getUser() {
        return instance.user;
    }

    public static MySQL getMysql() {
        return mysql;
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
    }

    public Boolean isH2() {
        return Objects.requireNonNull(getConfig().getString("data.type")).equalsIgnoreCase("H2");
    }

    public Boolean isMySQL() {
        return Objects.requireNonNull(getConfig().getString("data.type")).equalsIgnoreCase("MYSQL");
    }

    private void callMetrics() {
        int pluginId = 103140;
        Metrics metrics = new Metrics(this, pluginId);

        metrics.addCustomChart(new Metrics.SimplePie("used_language", () -> getConfig().getString("language", "en")));

        metrics.addCustomChart(new Metrics.DrilldownPie("java_version", () -> {
            Map<String, Map<String, Integer>> map = new HashMap<>();
            String javaVersion = System.getProperty("java.version");
            Map<String, Integer> entry = new HashMap<>();
            entry.put(javaVersion, 1);
            if (javaVersion.startsWith("1.7")) {
                map.put("Java 1.7", entry);
            } else if (javaVersion.startsWith("1.8")) {
                map.put("Java 1.8", entry);
            } else if (javaVersion.startsWith("1.9")) {
                map.put("Java 1.9", entry);
            } else {
                map.put("Other", entry);
            }
            return map;
        }));
    }

}
