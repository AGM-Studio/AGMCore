package me.ashenguard.agmcore;

import me.ashenguard.api.SpigotUpdater;
import me.ashenguard.api.messenger.Messenger;
import me.ashenguard.api.placeholderapi.PAPI;
import me.ashenguard.api.utils.VersionUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class AGMCore extends JavaPlugin {
    public static final int pluginID = 8250;
    public static final int resourceID = 83245;
    public static SpigotUpdater spigotupdater;
    public static FileConfiguration config;

    public static me.ashenguard.api.placeholderapi.PAPI PAPI = null;
    public static me.ashenguard.api.messenger.Messenger Messenger = null;

    private static boolean legacy;
    private static JavaPlugin instance;

    // ---- Getters ---- //
    public static JavaPlugin getInstance() {
        return instance;
    }
    public static boolean isLegacy() {
        return legacy;
    }

    @Override
    public void onEnable() {
        // ---- Load config ---- //
        instance = this;
        loadConfig();

        Messenger = new Messenger(this, config);
        Messenger.Info("§5Config§r has been loaded");

        // ---- Development ---- //
        new Metrics(this, pluginID);
        spigotupdater = new SpigotUpdater(this, resourceID);
        Messenger.updateNotification(getServer().getConsoleSender(), spigotupdater);

        // ---- Check legacy ---- //
        legacy = VersionUtils.isLegacy(this);
        if (isLegacy()) Messenger.Debug("General", "Legacy version detected");

        // ---- Setup data ---- //
        setup();

        Messenger.Info("Plugin has been started");
    }

    public static void loadConfig() {
        // ---- Get configuration ---- //
        JavaPlugin plugin = getInstance();

        config = plugin.getConfig();

        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        // ---- Set other configs ---- //
        File pluginFolder = plugin.getDataFolder();
        if (!pluginFolder.exists()) pluginFolder.mkdirs();
    }

    public static void setup() {
        PAPI = new PAPI(getInstance());

        new Listeners();
    }

    @Override
    public void onDisable() {
        Messenger.Info("Plugin disabled");
    }
}
