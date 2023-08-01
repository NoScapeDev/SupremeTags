package net.noscape.project.supremetags.checkers;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker implements Listener {

    private final int RESOURCE_ID = 111481;

    private final JavaPlugin plugin;

    public UpdateChecker(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void getVersionAsync(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream is = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.RESOURCE_ID).openStream(); Scanner scann = new Scanner(is)) {
                if (scann.hasNext()) {
                    consumer.accept(scann.next());
                }
            } catch (IOException e) {
                plugin.getLogger().info("Unable to check for updates: " + e.getMessage());
            }
        });
    }

    public void getVersion(final Consumer<String> consumer) {
        try (InputStream is = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.RESOURCE_ID).openStream(); Scanner scann = new Scanner(is)) {
            if (scann.hasNext()) {
                consumer.accept(scann.next());
            }
        } catch (IOException e) {
            plugin.getLogger().info("Unable to check for updates: " + e.getMessage());
        }
    }
}