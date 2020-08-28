package me.ashenguard.api;

import me.ashenguard.api.utils.Version;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class SpigotUpdater {
    private final int RESOURCE_ID;
    private final String URL;
    private final String RESOURCE_URL;
    private Version pluginVersion;
    private Version spigotVersion;
    private JavaPlugin plugin;

    public SpigotUpdater(@NotNull JavaPlugin plugin, int projectID) {
        this.plugin = plugin;

        this.RESOURCE_ID = projectID;
        this.URL = "https://api.spigotmc.org/legacy/update.php?resource=" + projectID;
        this.RESOURCE_URL = "https://www.spigotmc.org/resources/" + projectID;

        this.pluginVersion = new Version(plugin.getDescription().getVersion());
        this.spigotVersion = new Version(new WebReader(URL).read().replace(" ",""));
    }

    public int getProjectID() {
        return RESOURCE_ID;
    }
    public JavaPlugin getPlugin() {
        return plugin;
    }

    public String getLatestVersion() {
        updateLatestVersion();
        Version latest = spigotVersion.isHigher(pluginVersion) ? spigotVersion : pluginVersion;
        return latest.toString().substring(0, latest.toString().indexOf(" "));
    }

    public String getResourceURL() {
        return RESOURCE_URL;
    }

    public boolean newUpdate() {
        updateLatestVersion();
        return spigotVersion.isHigher(pluginVersion);
    }

    private void updateLatestVersion() {
        this.spigotVersion = new Version(new WebReader(URL).read().replace(" ",""));
    }
}