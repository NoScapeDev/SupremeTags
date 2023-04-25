package net.noscape.project.supremetags.checkers;

import net.md_5.bungee.api.chat.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.plugin.*;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;

import static net.noscape.project.supremetags.utils.Utils.*;

public class UpdateChecker implements Listener {

    private final int RESOURCE_ID = 103140;

    private final Plugin plugin;

    private String spigotVersion;

    private final String pluginVersion;

    private boolean updateAvailable;

    public UpdateChecker(Plugin instance) {
        this.plugin = instance;
        this.pluginVersion = instance.getDescription().getVersion();
    }

    public boolean hasUpdateAvailable() {
        return this.updateAvailable;
    }

    public String getSpigotVersion() {
        return this.spigotVersion;
    }

    public void fetch() {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                HttpsURLConnection con = (HttpsURLConnection)(new URL("https://api.spigotmc.org/legacy/update.php?resource=" + RESOURCE_ID)).openConnection();
                con.setRequestMethod("GET");
                this.spigotVersion = (new BufferedReader(new InputStreamReader(con.getInputStream()))).readLine();
            } catch (Exception ex) {
                this.plugin.getLogger().info("Failed to check for updates on spigot.");
                return;
            }
            if (this.spigotVersion == null || this.spigotVersion.isEmpty())
                return;
            this.updateAvailable = spigotIsNewer();
            if (!this.updateAvailable) return;
        });
    }

    private boolean spigotIsNewer() {
        if (this.spigotVersion == null || this.spigotVersion.isEmpty())
            return false;
        String plV = toReadable(this.pluginVersion);
        String spV = toReadable(this.spigotVersion);
        return (plV.compareTo(spV) < 0);
    }

    private String toReadable(String version) {
        if (version.contains("-DEV-"))
            version = version.split("-DEV-")[0];
        return version.replaceAll("\\.", "");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        if (e.getPlayer().hasPermission("supremetags.updates")) {
            if (hasUpdateAvailable()) {
                TextComponent message = new TextComponent(TextComponent.fromLegacyText(format("&7Click &a[HERE] &7download it!")));
                setClickBroadcastEvent(message, "/");
                e.getPlayer().spigot().sendMessage(message);

                msgPlayer(e.getPlayer(), "&8[&aSupremeTags&8] &7Update found for &a&lST &8(&f&nSupremeTags-" + getSpigotVersion() + "&r&8)");
            }
        }
    }

    public void setClickBroadcastEvent(TextComponent component, String click) {
        if (click == null || click.length() == 0)
            return;
        switch (click.charAt(0)) {
            case '/':
                component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, click));
                return;
            case '*':
                component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, click.substring(1)));
                return;
        }
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, click));
    }
}