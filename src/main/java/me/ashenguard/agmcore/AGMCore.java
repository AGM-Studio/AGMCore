package me.ashenguard.agmcore;

import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import me.ashenguard.agmcore.extension.CoreExtension;
import me.ashenguard.agmcore.extension.ExtensionLoader;
import me.ashenguard.api.bstats.Metrics;
import me.ashenguard.api.gui.GUIManager;
import me.ashenguard.api.itemstack.ItemLibrary;
import me.ashenguard.api.messenger.Messenger;
import me.ashenguard.api.messenger.PlaceholderManager;
import me.ashenguard.api.placeholder.Placeholder;
import me.ashenguard.api.placeholder.PlaceholderExtension;
import me.ashenguard.api.spigot.SpigotPlugin;
import me.ashenguard.lib.hooks.VaultAPI;
import me.ashenguard.lib.statistics.Livetime;
import me.ashenguard.lib.statistics.Playtime;
import me.ashenguard.lib.statistics.Waketime;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public final class AGMCore extends SpigotPlugin {
    private static AGMCore instance;
    private static GUIManager guiManager;
    private static ItemLibrary itemLibrary;

    public static AGMCore getInstance() {
        return instance;
    }
    public static Messenger getMessenger() {
        return instance.messenger;
    }
    public static GUIManager getGUIManager() {
        return guiManager;
    }
    public static ItemLibrary getItemLibrary() {
        return itemLibrary;
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

    @Override
    public void onPluginEnable() {
        instance = this;

        guiManager = new GUIManager();
        itemLibrary = new ItemLibrary();
        if (PlaceholderManager.enable) new Placeholders().register();
        ExtensionLoader extensionLoader = new ExtensionLoader();
        extensions = extensionLoader.registerAllExtensions();

        Metrics.AdvancedPie extension_chart = new Metrics.AdvancedPie("extensions", () -> {
            Map<String, Integer> map = new HashMap<>();
            for (String extension: extensions.keySet()) map.put(extension, 1);
            return map;
        });
        metrics.addCustomChart(extension_chart);

        MinecraftVersion.getVersion();

        Bukkit.getScheduler().runTaskLater(this, VaultAPI::setup, 10);
    }

    @Override
    public void onPluginDisable() {
        for (CoreExtension extension: extensions.values()) extension.onDisable();
        AGMEvents.deactivateDayCycleEvent(true);
    }

    private static class Placeholders extends PlaceholderExtension {
        public Placeholders() {
            super(AGMCore.getInstance());

            new Placeholder(this, s -> s.startsWith("Playtime"), s -> s.substring(8), Playtime::getPlaceholderValue);
            new Placeholder(this, s -> s.startsWith("Livetime"), s -> s.substring(8), Livetime::getPlaceholderValue);
            new Placeholder(this, s -> s.startsWith("Waketime"), s -> s.substring(8), Waketime::getPlaceholderValue);

            AGMCore.getMessenger().debug("Placeholders", "Placeholders has been registered.");
        }
    }
}