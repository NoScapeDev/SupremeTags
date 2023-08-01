package net.noscape.project.supremetags.managers;

import com.google.common.base.Charsets;
import net.noscape.project.supremetags.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class CategoryManager {

    private final List<String> catorgies = new ArrayList<>();
    private final Map<String, Integer> catorgiesTags = new HashMap<>();

    private File catFile;
    private FileConfiguration catConfig;

    public CategoryManager() {}

    public void initCategories() {
        catorgies.clear();
        catorgies.addAll(Objects.requireNonNull(getCatConfig().getConfigurationSection("categories")).getKeys(false));
        loadCategoriesTags();
    }

    public void loadCategoriesTags() {
        catorgiesTags.clear();

        for (String cats : getCatorgies()) {
            int value = 0;
            for (String tags : Objects.requireNonNull(SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getConfigurationSection("tags")).getKeys(false)) {
                String cat = SupremeTagsPremium.getInstance().getTagManager().getTagConfig().getString("tags." + tags + ".category");
                if (cats.equalsIgnoreCase(cat)) {
                    value++;
                }
            }
            catorgiesTags.put(cats, value);
        }
    }

    public List<String> getCatorgies() {
        return catorgies;
    }

    public Map<String, Integer> getCatorgiesTags() {
        return catorgiesTags;
    }

    public void saveCategoryConfig() {
        if (catFile != null && catConfig != null) {
            this.catConfig = YamlConfiguration.loadConfiguration(this.catFile);
            InputStream defConfigStream = SupremeTagsPremium.getInstance().getResource("categories.yml");
            if (defConfigStream != null) {
                this.catConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
            }
        } else {
            loadFile();
            saveCategoryConfig();
        }
    }

    public FileConfiguration getCatConfig() {
        return catConfig;
    }

    public void loadFile() {
        this.catFile = new File(SupremeTagsPremium.getInstance().getDataFolder(), "categories.yml");

        if (!catFile.exists()) {
            SupremeTagsPremium.getInstance().saveResource("categories.yml", true);
        }

        this.catConfig = YamlConfiguration.loadConfiguration(catFile);
    }
}
