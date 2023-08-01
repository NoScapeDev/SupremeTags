package net.noscape.project.supremetags.managers;

import com.google.common.base.Charsets;
import net.noscape.project.supremetags.SupremeTagsPremium;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BannedWords {

    private File bannedFile;
    private FileConfiguration bannedConfig;

    public BannedWords() {}

    public void saveBannedConfig() {
        if (bannedFile != null && bannedConfig != null) {
            this.bannedConfig = YamlConfiguration.loadConfiguration(this.bannedFile);
            InputStream defConfigStream = SupremeTagsPremium.getInstance().getResource("banned-words.yml");
            if (defConfigStream != null) {
                this.bannedConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
            }
        } else {
            loadFile();
            saveBannedConfig();
        }
    }

    public FileConfiguration getBannedConfig() {
        return bannedConfig;
    }

    public void loadFile() {
        this.bannedFile = new File(SupremeTagsPremium.getInstance().getDataFolder(), "banned-words.yml");

        if (!bannedFile.exists()) {
            SupremeTagsPremium.getInstance().saveResource("banned-words.yml", true);
        }

        this.bannedConfig = YamlConfiguration.loadConfiguration(bannedFile);
    }
}