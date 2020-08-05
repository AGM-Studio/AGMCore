package me.ashenguard.api.placeholderapi;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class PAPI {
    public final boolean enable;
    private final JavaPlugin plugin;

    private PAPIExpansion papiExpansion;

    public PAPI(JavaPlugin plugin) {
        enable = plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
        this.plugin = plugin;
    }

    public void registerExpansion() {
        registerExpansion(new PAPIExpansion(plugin));
    }
    public void registerExpansion(PAPIExpansion expansion) {
        papiExpansion = expansion;
        papiExpansion.register();
    }

    public String translate(OfflinePlayer player, String string) {
        if (enable)
            return PlaceholderAPI.setPlaceholders(player, string);
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public List<String> translate(OfflinePlayer player, List<String> stringList) {
        if (enable)
            return PlaceholderAPI.setPlaceholders(player, stringList);
        List<String> result = new ArrayList<>();
        for (String string:stringList)
            result.add(ChatColor.translateAlternateColorCodes('&', string));
        return result;
    }

    public String translate(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public List<String> translate(List<String> stringList) {
        List<String> result = new ArrayList<>();
        for (String string:stringList)
            result.add(ChatColor.translateAlternateColorCodes('&', string));
        return result;
    }

    // ---- Getters ---- //
    public PAPIExpansion getPAPIExpansion() {
        return papiExpansion;
    }
}
