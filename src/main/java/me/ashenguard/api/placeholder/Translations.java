package me.ashenguard.api.placeholder;

import me.ashenguard.api.Configuration;
import me.ashenguard.api.spigot.SpigotPlugin;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class Translations {
    private final Map<String, String> translations = new HashMap<>();

    public Translations(SpigotPlugin plugin, boolean separated) {
        translations.put("Prefix", plugin.messenger.prefix);

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("Translations");
        if (section != null) for (String key: section.getKeys(true)) {
            String value = section.getString(key, null);
            if (value != null) translations.put(key, value);
        }

        if (!separated) return;
        Configuration config = new Configuration(plugin, "Translations.yml", true);
        for (String key: config.getKeys(true)) {
            String value = config.getString(key, null);
            if (value != null) translations.put(key, value);
        }
    }

    public String get(String key, String def) {
        return translations.getOrDefault(key, def);
    }
    public String get(String key) {
        return get(key, String.format("%s-Missing", key));
    }
}
