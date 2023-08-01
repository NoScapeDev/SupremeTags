package net.noscape.project.supremetags;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.noscape.project.supremetags.api.SupremeTagsAPI;
import net.noscape.project.supremetags.checkers.*;
import net.noscape.project.supremetags.commands.*;
import net.noscape.project.supremetags.guis.tageditor.EditorListener;
import net.noscape.project.supremetags.handlers.Editor;
import net.noscape.project.supremetags.handlers.SetupTag;
import net.noscape.project.supremetags.handlers.hooks.*;
import net.noscape.project.supremetags.handlers.menu.*;
import net.noscape.project.supremetags.listeners.*;
import net.noscape.project.supremetags.managers.*;
import net.noscape.project.supremetags.storage.*;
import org.bukkit.*;
import org.bukkit.configuration.file.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;
import java.util.logging.*;

public final class SupremeTagsPremium extends JavaPlugin {

    private static SupremeTagsPremium instance;
    private TagManager tagManager;
    private CategoryManager categoryManager;
    private MergeManager mergeManager;
    private VoucherManager voucherManager;
    private BannedWords bannedWords;

    private static SupremeTagsAPI api;

    private static Economy econ = null;
    private static Permission perms = null;

    private static MySQLDatabase mysql;
    private static MariaDatabase maria;
    private static H2Database h2;
    private static SQLiteDatabase sqlite;
    private final MariaUserData mariaUser = new MariaUserData();
    private final SQLiteUserData sqLiteUser = new SQLiteUserData();
    private final H2UserData h2user = new H2UserData();
    private static String connectionURL;
    private final MySQLUserData user = new MySQLUserData();

    private static final HashMap<Player, MenuUtil> menuUtilMap = new HashMap<>();
    private final HashMap<Player, Editor> editorList = new HashMap<>();
    private final HashMap<Player, SetupTag> setupList = new HashMap<>();

    private boolean legacy_format;
    private boolean cmi_hex;
    private boolean disabledWorldsTag;

    private PlayerManager playerManager;
    private PlayerConfig playerConfig;

    public static File latestConfigFile;
    public static FileConfiguration latestConfigConfig;

    private final String host = getConfig().getString("data.address");
    private final int port = getConfig().getInt("data.port");
    private final String database = getConfig().getString("data.database");
    private final String username = getConfig().getString("data.username");
    private final String password = getConfig().getString("data.password");
    private final String options = getConfig().getString("data.options");

    private String layout = getConfig().getString("setting.layout-type");

    @Override
    public void onEnable() {
        init();
    }

    @Override
    public void onDisable() {
        tagManager.unloadTags();
        editorList.clear();
        setupList.clear();

        if (isMySQL()) {
            mysql.disconnected();
        }

        if (isMaria()) {
            maria.disconnect();
        }

        if (isSQLite()) {
            sqlite.disconnect();
        }
    }

    private void init() {
        instance = this;

        Logger logger = Bukkit.getLogger();

        this.saveDefaultConfig();
        this.callMetrics();

        loadDatabases();

        sendConsoleLog();

        tagManager = new TagManager(getConfig().getBoolean("settings.cost-system"));
        categoryManager = new CategoryManager();
        playerManager = new PlayerManager();
        voucherManager = new VoucherManager();
        mergeManager = new MergeManager();
        playerConfig = new PlayerConfig();
        bannedWords = new BannedWords();

        tagManager.loadFile();
        categoryManager.loadFile();
        bannedWords.loadFile();

        Objects.requireNonNull(getCommand("tags")).setExecutor(new Tags());
        Objects.requireNonNull(getCommand("tags")).setTabCompleter(new TagsComplete());
        Objects.requireNonNull(getCommand("mytags")).setExecutor(new MyTags());

        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
        getServer().getPluginManager().registerEvents(new EditorListener(), this);
        getServer().getPluginManager().registerEvents(new UpdateChecker(this), this);
        getServer().getPluginManager().registerEvents(new SetupListener(), this);
        getServer().getPluginManager().registerEvents(new VoucherListener(), this);

        legacy_format = getConfig().getBoolean("settings.legacy-hex-format");
        cmi_hex = getConfig().getBoolean("settings.cmi-color-support");
        disabledWorldsTag = getConfig().getBoolean("settings.tag-command-in-disabled-worlds");
        layout = getConfig().getString("settings.layout-type");

        merge(logger);

        if (isPlaceholderAPI()) {
            logger.info("> PlaceholderAPI: Found");
            new PAPI(this).register();
        } else {
            logger.info("> PlaceholderAPI: Not Found!");
        }

        tagManager.loadTags();
        categoryManager.initCategories();
        tagManager.getDataItem().clear();

        deleteCurrentLatestConfig();

        latestConfigFile = new File(getDataFolder(), "DEFAULT-CONFIG-LATEST.yml");

        if (!latestConfigFile.exists()) {
            saveResource("DEFAULT-CONFIG-LATEST.yml", true);
        }

        latestConfigConfig = new YamlConfiguration();
        try {
            latestConfigConfig.load(latestConfigFile);
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }

        api = new SupremeTagsAPI();


        // load tags again incase they did not load properly, on first installment.
        if (tagManager.getTags().size() == 0) {
            tagManager.loadTags();
        }
    }

    public static SupremeTagsPremium getInstance() { return instance; }

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

    public static MenuUtil getMenuUtilIdentifier(Player player, String identifier) {
        MenuUtil menuUtil;

        if (menuUtilMap.containsKey(player)) {
            return menuUtilMap.get(player);
        } else {
            menuUtil = new MenuUtil(player, identifier);
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

    public HashMap<Player, MenuUtil> getMenuUtil() {
        return menuUtilMap;
    }

    public static String getConnectionURL() {
        return connectionURL;
    }

    public H2UserData getUserData() { return h2user; }

    public static H2Database getDatabase() { return h2; }

    public MySQLUserData getUser() {
        return instance.user;
    }

    public static MySQLDatabase getMysql() {
        return mysql;
    }

    public void reload() {
        super.reloadConfig();
        getTagManager().saveTagConfig();
        categoryManager.saveCategoryConfig();
        bannedWords.saveBannedConfig();

        legacy_format = getConfig().getBoolean("settings.legacy-hex-format");
        cmi_hex = getConfig().getBoolean("settings.cmi-color-support");
        disabledWorldsTag = getConfig().getBoolean("settings.tag-command-in-disabled-worlds");
        layout = getConfig().getString("settings.layout-type");

        getTagManager().unloadTags();
        getTagManager().loadTags();

        getTagManager().getDataItem().clear();

        getTagManager().setCost(SupremeTagsPremium.getInstance().getConfig().getBoolean("settings.cost-system"));

        getCategoryManager().initCategories();
    }

    private void loadDatabases() {
        if (isH2()) {
            connectionURL = "jdbc:h2:" + getDataFolder().getAbsolutePath() + "/database";
            h2 = new H2Database(connectionURL);
        }

        if (isSQLite()) {
            connectionURL = "jdbc:sqlite:" + getDataFolder().getAbsolutePath() + "/database.db";
            sqlite = new SQLiteDatabase(connectionURL);
        }

        if (isMySQL()) {
            mysql = new MySQLDatabase(host, port, database, username, password, options);
        }

        if (isMaria()) {
            maria = new MariaDatabase(host, port, database, username, password, options);
        }
    }

    public boolean isLegacyFormat() {
        return legacy_format;
    }

    public void merge(Logger log) {
        mergeManager.merge(log);
    }

    private void sendConsoleLog() {
        Logger logger = Bukkit.getLogger();

        logger.info("");
        logger.info("  ____  _   _ ____  ____  _____ __  __ _____ _____  _    ____ ____  ");
        logger.info(" / ___|| | | |  _ \\|  _ \\| ____|  \\/  | ____|_   _|/ \\  / ___/ ___| ");
        logger.info(" \\___ \\| | | | |_) | |_) |  _| | |\\/| |  _|   | | / _ \\| |  _\\___ \\ ");
        logger.info("  ___) | |_| |  __/|  _ <| |___| |  | | |___  | |/ ___ \\ |_| |___) |");
        logger.info(" |____/ \\___/|_|   |_| \\_\\_____|_|  |_|_____| |_/_/   \\_\\____|____/ ");
        logger.info(" Allow players to show off their supreme tags!");
        logger.info("");
        logger.info("[PREMIUM] Premium features activated!");
        logger.info("[PREMIUM] Thanks for getting premium version of SupremeTags! Much love <33");
        logger.info("");
        logger.info("> Version: " + getDescription().getVersion());
        logger.info("> Author: DevScape");

        if (getServer().getPluginManager().getPlugin("NBTAPI") == null) {
            logger.warning("> NBTAPI: Supremetags requires NBTAPI to run, disabling plugin....");
            getServer().getPluginManager().disablePlugin(this);
        } else {
            logger.info("> NBTAPI: Found!");
        }

        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            setupEconomy();
            setupPermissions();
            logger.info("> Vault: Found!");
        } else {
            logger.info("> Vault: Not Found!");
        }

        if (isH2()) {
            logger.info("> Database: H2!");
        } else if (isMySQL()) {
            logger.info("> Database: MySQL!");
        } else if (isMaria()) {
            logger.info("> Database: MariaDB!");
        } else if (isSQLite()) {
            logger.info("> Database: SQLite!");
        }

        if (getConfig().getBoolean("settings.update-check")) {
            new UpdateChecker(this).getVersion(version -> {
                if (!this.getDescription().getVersion().equals(version)) {
                    logger.info("> Updater: An update is available! " + version);
                    logger.info("Download at https://www.spigotmc.org/resources/111481/");
                } else {
                    logger.info("> Updater: Plugin up to date!");
                }
            });
        }
    }

    public Boolean isH2() {
        return Objects.requireNonNull(getConfig().getString("data.type")).equalsIgnoreCase("H2");
    }

    public Boolean isMaria() {
        return Objects.requireNonNull(getConfig().getString("data.type")).equalsIgnoreCase("MARIADB");
    }

    public Boolean isMySQL() {
        return Objects.requireNonNull(getConfig().getString("data.type")).equalsIgnoreCase("MYSQL");
    }

    public boolean isSQLite() {
        return Objects.requireNonNull(getConfig().getString("data.type")).equalsIgnoreCase("SQLite");
    }

    private void callMetrics() {
        int pluginId = 18038;
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

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static Permission getPermissions() {
        return perms;
    }

    public static SupremeTagsAPI getTagAPI() {
        return api;
    }

    private void deleteCurrentLatestConfig() {
        latestConfigFile = new File(getDataFolder(), "DEFAULT-CONFIG-LATEST.yml");

        if (latestConfigFile.exists()) {
            boolean deletionSuccessful = latestConfigFile.delete();
            if (!deletionSuccessful) {
                Bukkit.getLogger().warning("Failed to reset DEFAULT-CONFIG-LATEST.yml");
            }
        }
    }

    public HashMap<Player, Editor> getEditorList() {
        return editorList;
    }

    public boolean isCMIHex() {
        return cmi_hex;
    }

    public boolean isDisabledWorldsTag() {
        return disabledWorldsTag;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    public MergeManager getMergeManager() {
        return mergeManager;
    }
    public MariaUserData getMariaUser() {
        return mariaUser;
    }

    public PlayerConfig getPlayerConfig() {
        return playerConfig;
    }

    public HashMap<Player, SetupTag> getSetupList() {
        return setupList;
    }

    public boolean isPlaceholderAPI() {
        return getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    public String getLayout() {
        return layout;
    }

    public VoucherManager getVoucherManager() {
        return voucherManager;
    }

    public static MariaDatabase getMaria() {
        return maria;
    }

    public static SQLiteDatabase getSQLite() {
        return sqlite;
    }

    public SQLiteUserData getSQLiteUser() {
        return sqLiteUser;
    }

    public BannedWords getBannedWords() { return bannedWords; }
}