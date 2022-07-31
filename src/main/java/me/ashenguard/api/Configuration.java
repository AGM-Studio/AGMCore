package me.ashenguard.api;

import me.ashenguard.api.placeholder.Placeholder;
import me.ashenguard.api.spigot.SpigotPlugin;
import me.ashenguard.api.utils.FileUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.Function;

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

    public Configuration(SpigotPlugin plugin, String path, String resource) {
        this(plugin, path, resource, (string -> string));
    }

    public Configuration(SpigotPlugin plugin, String path, String resource, Function<String, String> defaultEditor) {
        this(plugin, path, plugin.getResource(resource), defaultEditor);
    }

    public Configuration(SpigotPlugin plugin, String path, String resource, Placeholder... placeholders) {
        this(plugin, path, plugin.getResource(resource), s -> Placeholder.apply(s, null, placeholders));
    }

    public Configuration(SpigotPlugin plugin, String path, InputStream resource) {
        this(plugin, path, resource, (string -> string));
    }

    public Configuration(SpigotPlugin plugin, String path, InputStream resource, Function<String, String> defaultEditor) {
        this(plugin, path, FileUtils.readStream(resource), defaultEditor, 0);
    }

    public Configuration(SpigotPlugin plugin, String path, InputStream resource, Placeholder... placeholders) {
        this(plugin, path, FileUtils.readStream(resource), s -> Placeholder.apply(s, null, placeholders), 0);
    }

    private Configuration(SpigotPlugin plugin, String path, String data, Function<String, String> defaultEditor, int ignored) {
        this.plugin = plugin;
        this.path = path;
        this.configFile = new File(plugin.getDataFolder(), path);

        if (!configFile.exists()) FileUtils.writeFile(configFile, defaultEditor.apply(data));
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

    public String getString(Collection<String> paths, String def) {
        for (String path: paths) if (contains(path)) return getString(path, def);
        return def;
    }
    public String getString(Collection<String> paths) {
        return getString(paths, null);
    }

    public int getInt(Collection<String> paths, int def) {
        for (String path: paths) if (contains(path)) return getInt(path, def);
        return def;
    }
    public int getInt(Collection<String> paths) {
        return getInt(paths, 0);
    }

    public double getDouble(Collection<String> paths, double def) {
        for (String path: paths) if (contains(path)) return getDouble(path, def);
        return def;
    }
    public double getDouble(Collection<String> paths) {
        return getDouble(paths, 0);
    }

    public boolean getBoolean(Collection<String> paths, boolean def) {
        for (String path: paths) if (contains(path)) return getBoolean(path, def);
        return def;
    }
    public boolean getBoolean(Collection<String> paths) {
        return getBoolean(paths, false);
    }

    public static String getString(ConfigurationSection section, Collection<String> paths, String def) {
        for (String path: paths) if (section.contains(path)) return section.getString(path, def);
        return def;
    }
    public static String getString(ConfigurationSection section, Collection<String> paths) {
        return getString(section, paths, null);
    }

    public static int getInt(ConfigurationSection section, Collection<String> paths, int def) {
        for (String path: paths) if (section.contains(path)) return section.getInt(path, def);
        return def;
    }
    public static int getInt(ConfigurationSection section, Collection<String> paths) {
        return getInt(section, paths, 0);
    }

    public static double getDouble(ConfigurationSection section, Collection<String> paths, double def) {
        for (String path: paths) if (section.contains(path)) return section.getDouble(path, def);
        return def;
    }
    public static double getDouble(ConfigurationSection section, Collection<String> paths) {
        return getDouble(section, paths, 0);
    }

    public static boolean getBoolean(ConfigurationSection section, Collection<String> paths, boolean def) {
        for (String path: paths) if (section.contains(path)) return section.getBoolean(path, def);
        return def;
    }
    public static boolean getBoolean(ConfigurationSection section, Collection<String> paths) {
        return getBoolean(section, paths, false);
    }
}
