package me.ashenguard.api;

import me.ashenguard.api.messenger.PHManager;
import me.ashenguard.api.spigot.SpigotPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings({"unused"})
public class Translator {
    private final String notFound;
    private final HashMap<String, Configuration> translations = new HashMap<>();

    public Translator(SpigotPlugin plugin, List<String> defaultTranslations) {
        this(plugin, "§cERROR - NOT FOUND", defaultTranslations);
    }

    public Translator(SpigotPlugin plugin, String notFound, List<String> defaultTranslations) {
        File translationsFolder = new File(plugin.getDataFolder(), "Translations");
        if (!translationsFolder.exists() && translationsFolder.mkdirs()) plugin.messenger.Debug("General", "Translations folder wasn't found, A new one created");

        for (String translation: defaultTranslations) new Configuration(plugin, String.format("Translations/%s.yml", translation));
        for (File translationFile: translationsFolder.listFiles(((dir, name) -> name.endsWith(".yml")))) {
            String name = translationFile.getName();
            name = name.substring(0, name.length() - 4);
            translations.put(name, new Configuration(plugin, String.format("Translations/%s.yml", name)));
        }

        this.notFound = notFound;
    }

    public String get(String language, String path) {
        return get(language, path, notFound);
    }
    public String get(String language, String path, String def) {
        Configuration translation = translations.getOrDefault(language, null);
        if (translation == null) return def;
        return PHManager.translate(translation.getString(path, def));
    }
}
