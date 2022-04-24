package me.ashenguard.agmcore;

import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import me.ashenguard.agmcore.extension.CoreExtension;
import me.ashenguard.agmcore.extension.ExtensionLoader;
import me.ashenguard.api.bstats.Metrics;
import me.ashenguard.api.messenger.Messenger;
import me.ashenguard.api.messenger.PHManager;
import me.ashenguard.api.placeholder.PHExtension;
import me.ashenguard.api.spigot.SpigotPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public final class AGMCore extends SpigotPlugin {
    private static AGMCore instance;
    private static PHExtension phExtension;

    public static AGMCore getInstance() {
        return instance;
    }
    public static Messenger getMessenger() {
        return instance.messenger;
    }
    public static PHExtension getPHExtension() {
        return phExtension;
    }

    @Override
    public @NotNull List<String> getSoftRequirements() {
        return Arrays.asList("PlaceholderAPI", "ProtocolLib", "Citizens");
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

        if (PHManager.enable) phExtension = new Placeholders();
        ExtensionLoader extensionLoader = new ExtensionLoader();
        extensions = extensionLoader.registerAllExtensions();

        Metrics.AdvancedPie extension_chart = new Metrics.AdvancedPie("extensions", () -> {
            Map<String, Integer> map = new HashMap<>();
            for (String extension: extensions.keySet()) map.put(extension, 1);
            return map;
        });
        metrics.addCustomChart(extension_chart);

        MinecraftVersion.getVersion();
    }

    @Override
    public void onPluginDisable() {
        for (CoreExtension extension: extensions.values()) extension.onDisable();
        AGMEvents.deactivateDayCycleEvent(true);
        messenger.Info("Plugin has been disabled");
    }

    private static class Placeholders extends PHExtension {
        public Placeholders() {
            super(AGMCore.getInstance());
        }
    }
}