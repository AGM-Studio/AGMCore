package me.ashenguard.api.placeholder;

import me.ashenguard.api.Configuration;
import me.ashenguard.api.spigot.SpigotPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class Translations {
    private final Map<String, String> translations = new HashMap<>();
    private final SpigotPlugin plugin;

    public Translations(SpigotPlugin plugin, boolean separated) {
        this.plugin = plugin;
        this.translations.put("Prefix", plugin.messenger.prefix);

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("Translations");
        if (section != null) for (String key: section.getKeys(true)) {
            String value = section.getString(key, null);
            if (value != null) translations.put(key, value);
        }

        if (!separated) return;
        Configuration config = new Configuration(plugin, "translations.yml", true);
        for (String key: config.getKeys(true)) {
            String value = config.getString(key, null);
            if (value != null) translations.put(key, value);
        }
    }

    public void info(String key) {
        String[] messages = get(key, String.format("INFO MESSAGE NOT FOUND - %s", key)).split("\n");
        plugin.messenger.info(messages);
    }
    public void critical(String key) {
        String[] messages = get(key, String.format("CRITICAL MESSAGE NOT FOUND - %s", key)).split("\n");
        plugin.messenger.critical(messages);
    }
    public void warning(String key) {
        String[] messages = get(key, String.format("WARNING MESSAGE NOT FOUND - %s", key)).split("\n");
        plugin.messenger.warning(messages);
    }
    public void debug(String type, String key) {
        String[] messages = get(key, String.format("DEBUG (%s) MESSAGE NOT FOUND - %s", type, key)).split("\n");
        plugin.messenger.debug(type, messages);
    }

    public void send(CommandSender target, String key) {
        String[] messages = get(key, String.format("SEND MESSAGE NOT FOUND - %s", key)).split("\n");
        plugin.messenger.send(target, messages);
    }
    public void response(CommandSender target, String key) {
        String[] messages = get(key, String.format("WARNING MESSAGE NOT FOUND - %s", key)).split("\n");
        plugin.messenger.response(target, messages);
    }


    public String get(String key, String def) {
        return translations.getOrDefault(key, def);
    }
    public String get(String key) {
        return get(key, String.format("%s-Missing", key));
    }
}
