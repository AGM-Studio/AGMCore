package me.ashenguard.api;

import me.ashenguard.api.spigot.SpigotPlugin;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("unused")
public class Configuration extends YamlConfiguration {
    private final SpigotPlugin plugin;
    public final File configFile;
    public final String path;

    public Configuration(SpigotPlugin plugin, String path, boolean saveDefault) {
        this.plugin = plugin;
        this.path = path;
        this.configFile = new File(plugin.getDataFolder(), path);

        if (saveDefault) saveDefaultConfig();
        if (configFile.exists()) loadConfig();
    }
    public Configuration(SpigotPlugin plugin, String configFile) {
        this(plugin, configFile, true);
    }

    public void loadConfig() {
        try {
            load(this.configFile);
        } catch (IOException | InvalidConfigurationException exception) {
            plugin.messenger.handleException(exception);
        }
    }

    public void saveConfig() {
        try {
            save(this.configFile);
        } catch (Exception exception) {
            plugin.messenger.handleException(exception);
        }
    }

    public void saveDefaultConfig(boolean replace) {
        if (configFile.exists() && !replace) return;
        plugin.saveResource(path, replace);
    }

    public void saveDefaultConfig() {
        saveDefaultConfig(false);
    }
}
