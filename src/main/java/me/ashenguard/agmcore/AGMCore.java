package me.ashenguard.agmcore;

import me.ashenguard.agmcore.extension.CoreExtension;
import me.ashenguard.agmcore.extension.ExtensionLoader;
import me.ashenguard.api.messenger.Messenger;
import me.ashenguard.api.messenger.PHManager;
import me.ashenguard.api.spigot.SpigotPlugin;
import me.ashenguard.lib.PlaytimeManager;

import java.io.File;
import java.util.HashMap;

@SuppressWarnings("unused")
public final class AGMCore extends SpigotPlugin {
    private static AGMCore instance;
    public static AGMCore getInstance() {
        return instance;
    }
    public static Messenger getMessenger() {
        return instance.messenger;
    }
    public static PlaytimeManager getPlaytimeManager() {
        return instance.playtimeManager;
    }

    @Override
    public int getBStatsID() {
        return 8250;
    }

    @Override
    public int getSpigotID() {
        return 83245;
    }

    private HashMap<String, CoreExtension> extensions = new HashMap<>();
    public HashMap<String, CoreExtension> getExtensions() {
        return new HashMap<>(extensions);
    }
    private PlaytimeManager playtimeManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        reloadConfig();

        updateNotification = getConfig().getBoolean("Check.PluginUpdates", true);

        File pluginFolder = getDataFolder();
        if (!pluginFolder.exists() && pluginFolder.mkdirs()) messenger.Debug("General", "Plugin folder wasn't found, A new one created");

        if (PHManager.enable) new Placeholders();
        ExtensionLoader extensionLoader = new ExtensionLoader();
        extensions = extensionLoader.registerAllExtensions();

        EventCaller.start();
        messenger.Info("Plugin has been enabled successfully");
    }

    @Override
    public void onDisable() {
        for (CoreExtension extension: extensions.values()) extension.onDisable();
        EventCaller.stop(true);
        messenger.Info("Plugin has been disabled");
    }
}