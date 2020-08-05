package me.ashenguard.api;

import me.ashenguard.api.messenger.Messenger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Configuration extends YamlConfiguration {
    private final JavaPlugin plugin;
    private File configFile;
    private final String resource;

    public Configuration(JavaPlugin plugin, File configFile, String resource) {
        this.plugin = plugin;
        this.configFile = configFile;
        this.resource = resource;
    }

    public Configuration getConfig() {
        return this;
    }

    public void reloadConfig() {
        try {
            this.load(configFile);
        } catch (Exception exception) {
            Messenger.handleException(exception);
        }
    }

    public void saveConfig() {
        try {
            save(configFile);
        } catch (Exception exception) {
            Messenger.handleException(exception);
        }
    }

    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            saveResource(resource, false);
        }
    }

    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = plugin.getResource(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + plugin.getName());
        }

        File outFile = configFile;
        int lastIndex = configFile.getPath().lastIndexOf('/');
        File outDir = new File(plugin.getDataFolder(), configFile.getPath().substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists()) outDir.mkdirs();

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
                Messenger.Warning("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (Exception exception) {
            Messenger.handleException(exception);
        }
    }
}
