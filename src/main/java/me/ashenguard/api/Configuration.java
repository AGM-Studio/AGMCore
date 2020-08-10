package me.ashenguard.api;

import me.ashenguard.api.messenger.Messenger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class Configuration extends YamlConfiguration {
    private final JavaPlugin plugin;
    public final File configFile;
    public final String path;

    public Configuration(JavaPlugin plugin, String path, boolean saveDefault) {
        this.plugin = plugin;
        this.path = path;
        this.configFile = new File(plugin.getDataFolder(), path);

        if (saveDefault && !configFile.exists()) plugin.saveResource(path, false);
        if (configFile.exists()) loadConfig();
    }
    public Configuration(JavaPlugin plugin, String configFile) {
        this(plugin, configFile, true);
    }

    public void loadConfig() {
        try {
            load(this.configFile);
        } catch (IOException | InvalidConfigurationException exception) {
            Messenger.handleException(exception);
        }
    }

    public Configuration getConfig() {
        return this;
    }

    public void saveConfig() {
        try {
            save(this.configFile);
        } catch (Exception exception) {
            Messenger.handleException(exception);
        }
    }

    public void saveDefaultConfig() {
        if (configFile.exists()) return;
        plugin.saveResource(this.path, false);
    }
}
