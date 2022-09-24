package net.noscape.project.supremetags;

import net.noscape.project.supremetags.checkers.*;
import net.noscape.project.supremetags.commands.*;
import net.noscape.project.supremetags.handlers.hooks.*;
import net.noscape.project.supremetags.handlers.menu.*;
import net.noscape.project.supremetags.listeners.*;
import net.noscape.project.supremetags.managers.*;
import net.noscape.project.supremetags.storage.*;
import org.bukkit.*;
import org.bukkit.configuration.file.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;
import java.util.logging.*;

public final class SupremeTags extends JavaPlugin {

    private static SupremeTags instance;
    private final TagManager tagManager = new TagManager();
    private final CategoryManager categoryManager = new CategoryManager();

    private static MySQL mysql;
    private static H2Database h2;
    private final H2UserData h2user = new H2UserData();
    private static String connectionURL;
    private static String layout;
    private final MySQLUserData user = new MySQLUserData();

    private static final HashMap<Player, MenuUtil> menuUtilMap = new HashMap<>();

    private boolean legacy_format;

    private final String host = getConfig().getString("data.address");
    private final int port = getConfig().getInt("data.port");
    private final String database = getConfig().getString("data.database");
    private final String username = getConfig().getString("data.username");
    private final String password = getConfig().getString("data.password");
    private final String options = getConfig().getString("data.options");

    @Override
    public void onEnable() {
        instance = this;

        Logger log = getLogger();

        if (getServer().getPluginManager().getPlugin("NBTAPI") == null) {
            log.warning("------------------------------");
            log.warning("[NBTAPI] NBTAPI is required for SupremeTags-" + getDescription().getVersion() + " to work!");
            log.warning("------------------------------");
            getServer().getPluginManager().disablePlugin(this);
        } else {
            log.fine("------------------------------");
            log.fine("[NBTAPI] NBTAPI has been found for SupremeTags-" + getDescription().getVersion() + "!");
            log.fine("------------------------------");
        }

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
                log.warning("------------------------------");
                log.warning("SupremeTags-Checker");
                log.warning(" ");
                log.warning("An update for SupremeTags has been found!");
                log.warning("SupremeTags-" + updater.getSpigotVersion());
                log.warning("You are running " + getDescription().getVersion());
                log.warning(" ");
                log.warning("Download at https://www.spigotmc.org/resources/%E2%9C%85-supremetags-%E2%9C%85-1-8-1-19-placeholderapi-support-unlimited-tags-%E2%9C%85.103140/");
                log.warning("------------------------------");
            } else {
                log.fine("------------------------------");
                log.fine("Running latest version of SupremeTags-" + getDescription().getVersion());
                log.fine("------------------------------");
            }
        }

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PAPI(this).register();
            log.fine("------------------------------");
            log.fine("PlaceholderAPI found SupremeTags! Automatically downloaded cloud.");
            log.fine("------------------------------");
        }

        Objects.requireNonNull(getCommand("tags")).setExecutor(new Tags());

        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);

        if (Objects.requireNonNull(getConfig().getString("settings.layout")).equalsIgnoreCase("layout1")) {
            layout = "layout1";
        } else if (Objects.requireNonNull(getConfig().getString("settings.layout")).equalsIgnoreCase("layout2")) {
            layout = "layout2";
        }

        legacy_format = getConfig().getBoolean("settings.legacy-hex-format");

        merge(log);

        tagManager.loadTags();
        categoryManager.loadCategories();
        categoryManager.loadCategoriesTags();
        tagManager.getDataItem().clear();
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

    public static String getLayout() {
        return layout;
    }

    public static void setLayout(String layout) {
        SupremeTags.layout = layout;
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

    public boolean isLegacyFormat() {
        return legacy_format;
    }

    public void merge(Logger log) {
        if (getConfig().getBoolean("settings.auto-merge")) {
            File configFile = new File(Bukkit.getServer().getWorldContainer().getAbsolutePath() + "/plugins/DeluxeTags/config.yml"); // First we will load the file.
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile); // Now we will load the file into a FileConfiguration.

            //deluxetags:
            //example:
            //order: 1
            //tag: '&8[&bDeluxeTags&8]'
            //description: '&cAwarded for using DeluxeTags!'
            //permission: deluxetags.tag.example

            if (configFile.exists()) {
                if (config.getConfigurationSection("deluxetags") != null) {
                    for (String identifier : Objects.requireNonNull(config.getConfigurationSection("deluxetags")).getKeys(false)) {
                        if (!SupremeTags.getInstance().getTagManager().getTags().containsKey(identifier)) {

                            String tag = config.getString("deluxetags." + identifier + ".tag");
                            String description = config.getString("deluxetags." + identifier + ".description");
                            String permission = config.getString("deluxetags." + identifier + ".permission");

                            SupremeTags.getInstance().getTagManager().createTag(identifier, tag, description, permission);
                        }

                        log.fine("Merger: &7Added all new tags from DeluxeTags were added, any existing tags with the same name won't be added.");
                    }
                } else {
                    log.warning("Error: DeluxeTags tag config area is empty.");
                }
            } else {
                log.warning("Error: DeluxeTags can not be found.");
            }
        }
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
