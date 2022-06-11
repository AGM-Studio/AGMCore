package me.ashenguard.api.messenger;

import me.ashenguard.api.placeholder.Placeholder;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PlaceholderManager {
    public static final boolean enable = Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;

    public static String translate(OfflinePlayer player, @NotNull String string) {
        return enable ? PlaceholderAPI.setPlaceholders(player, string) : translate(string);
    }
    public static List<String> translate(OfflinePlayer player, @NotNull List<String> stringList) {
        return enable ? PlaceholderAPI.setPlaceholders(player, stringList) : translate(stringList);
    }
    public static String translate(@NotNull String string) {
        return enable ? PlaceholderAPI.setPlaceholders(null, string) : ChatColor.translateAlternateColorCodes('&', string);
    }
    public static List<String> translate(@NotNull List<String> stringList) {
        return enable ? PlaceholderAPI.setPlaceholders(null, stringList) : stringList.stream().map(PlaceholderManager::translate).collect(Collectors.toList());
    }
    public static String translate(OfflinePlayer player, @NotNull String string, Collection<Placeholder> placeholders) {
        for (Placeholder placeholder: placeholders) string = placeholder.apply(string, player);
        return translate(player, string);
    }
    public static List<String> translate(OfflinePlayer player, @NotNull List<String> stringList, Collection<Placeholder> placeholders) {
        stringList = stringList.stream().map(string -> {
            for (Placeholder placeholder: placeholders) string = placeholder.apply(string, player);
            return string;
        }).collect(Collectors.toList());
        return translate(player, stringList);
    }
    public static String translate(@NotNull String string, Collection<Placeholder> placeholders) {
        return translate(null, string, placeholders);
    }
    public static List<String> translate(@NotNull List<String> stringList, Collection<Placeholder> placeholders) {
        return translate(null, stringList, placeholders);
    }
}