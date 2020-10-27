package me.ashenguard.api;

import me.ashenguard.api.placeholderapi.PAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class Translator {
    private final PAPI papi;

    private final String notFound;

    private Configuration translation;

    public Translator(JavaPlugin plugin, PAPI papi) {
        this(plugin, papi, "§cERROR - NOT FOUND");
    }

    public Translator(JavaPlugin plugin, PAPI papi, String notFound) {
        this.papi = papi;

        translation = new Configuration(plugin, "language.yml");
        this.notFound = notFound;
    }

    public String get(String path) {
        return papi.translate(translation.getString(path, notFound));
    }

    public String get(String path, String def) {
        return papi.translate(translation.getString(path, def));
    }
}
