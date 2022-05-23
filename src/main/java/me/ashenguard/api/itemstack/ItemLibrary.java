package me.ashenguard.api.itemstack;

import me.ashenguard.agmcore.AGMCore;
import me.ashenguard.api.Configuration;
import me.ashenguard.api.itemstack.advanced.AdvancedItemStack;
import me.ashenguard.api.spigot.SpigotPlugin;
import me.ashenguard.exceptions.IllegalFormatException;
import me.ashenguard.exceptions.PluginNotEnabled;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"ResultOfMethodCallIgnored", "unused"})
public class ItemLibrary {
    private static final SpigotPlugin CORE = AGMCore.getInstance();

    private final Map<NamespacedKey, AdvancedItemStack> itemMap = new HashMap<>();
    private final Map<NamespacedKey, NamespacedKey> keyMap = new HashMap<>();

    public ItemLibrary() {
        if (CORE == null || !CORE.isEnabled()) throw new PluginNotEnabled("AGMCore is not Enabled");

        Bukkit.getScheduler().runTaskLater(CORE, this::loadLibrary, 10);
    }

    public void loadLibrary() {
        File holder = new File(CORE.getDataFolder(), "GUI");
        if (!holder.exists()) {
            holder.mkdirs();
        }

        FilenameFilter filter = (dir, name) -> {
            String extension = name.substring(name.lastIndexOf("."));
            return extension.equalsIgnoreCase("yml") || extension.equalsIgnoreCase("yaml");
        };

        for (File file: holder.listFiles(filter)) {
            loadGUIConfig(file);
        }

        AGMCore.getMessenger().Debug("General", "§dItemLibrary§r has been loaded.");
    }

    public static void createLibraryFile(SpigotPlugin plugin, String filename, String resource) {
        if (!filename.endsWith(".yml") && !filename.endsWith(".yaml")) filename += ".yml";
        new Configuration(CORE, String.format("GUI/%s", filename), plugin.getResource(resource));
    }

    @SuppressWarnings("deprecation")
    private void loadGUIConfig(File file) {
        Configuration config = new Configuration(CORE, String.format("GUI/%s", file.getName()), false);
        String filename = file.getName().toLowerCase().substring(0, file.getName().lastIndexOf("."));
        Set<String> keys = config.getKeys(false);
        for (String key: keys) {
            try {
                if (config.isConfigurationSection(key)) {
                    AdvancedItemStack item = AdvancedItemStack.fromSection(config.getConfigurationSection(key));
                    if (item != null) itemMap.put(new NamespacedKey(filename, key), item);
                } else if (config.isString(key)) {
                    NamespacedKey namespace = NamespacedKey.fromString(key);
                    if (namespace != null) keyMap.put(new NamespacedKey(filename, key), namespace);
                } else {
                    Object obj = config.get(key);
                    throw new IllegalFormatException(String.format("Items in the ItemLibrary can only be a namespace as a reference or a section not a \"%s\"", obj == null ? null : obj.getClass().getSimpleName()));
                }
            } catch (Throwable throwable) {
                AGMCore.getMessenger().handleException(String.format("While trying to load item \"%s\" from \"%s\", an error occurred", key, filename), throwable);
            }
        }
    }

    public @Nullable AdvancedItemStack getItem(String string) {
        return getItem(NamespacedKey.fromString(string));
    }
    @SuppressWarnings("deprecation")
    public @Nullable AdvancedItemStack getItem(String parent, String key) {
        return getItem(new NamespacedKey(parent, key));
    }
    public @Nullable AdvancedItemStack getItem(SpigotPlugin plugin, String key) {
        return getItem(new NamespacedKey(plugin, key));
    }
    public @Nullable AdvancedItemStack getItem(NamespacedKey key) {
        AdvancedItemStack item = itemMap.getOrDefault(key, null);
        if (item != null) return item;

        NamespacedKey namespace = keyMap.getOrDefault(key, null);
        if (namespace != null && namespace != key) return getItem(namespace);

        return null;
    }

    public @NotNull AdvancedItemStack getNotNullItem(String string) {
        AdvancedItemStack item = getItem(string);
        return item == null ? AdvancedItemStack.nullItem() : item;
    }
    public @NotNull AdvancedItemStack getNotNullItem(String parent, String key) {
        AdvancedItemStack item = getItem(parent, key);
        return item == null ? AdvancedItemStack.nullItem() : item;
    }
    public @NotNull AdvancedItemStack getNotNullItem(SpigotPlugin plugin, String key) {
        AdvancedItemStack item = getItem(plugin, key);
        return item == null ? AdvancedItemStack.nullItem() : item;
    }
    public @NotNull AdvancedItemStack getNotNullItem(NamespacedKey key) {
        AdvancedItemStack item = getItem(key);
        return item == null ? AdvancedItemStack.nullItem() : item;
    }
}
