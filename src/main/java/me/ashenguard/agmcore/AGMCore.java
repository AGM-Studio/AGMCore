package me.ashenguard.agmcore;

import me.ashenguard.api.spigot.SpigotPlugin;

import java.io.File;

@SuppressWarnings("unused")
public final class AGMCore extends SpigotPlugin {
    public static AGMCore instance;

    @Override
    public int getBStatsID() {
        return 8250;
    }

    @Override
    public int getSpigotID() {
        return 83245;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        reloadConfig();

        File pluginFolder = getDataFolder();
        if (!pluginFolder.exists() && pluginFolder.mkdirs()) messenger.Debug("General", "Plugin folder wasn't found, A new one created");
        messenger.Info("§5Config§r has been loaded");
        messenger.Info("Plugin has been enabled successfully");
    }

    @Override
    public void onDisable() {
        messenger.Info("Plugin has been disabled");
    }
}
