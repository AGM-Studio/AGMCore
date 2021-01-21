package me.ashenguard.api;

import me.ashenguard.api.spigot.SpigotPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class PHExtension extends PlaceholderExpansion {
    protected final SpigotPlugin plugin;

    public PHExtension(SpigotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean register() {
        try {
            return super.register();
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getIdentifier() {
        return plugin.getName();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        if (player == null) return null;

        if (identifier.equals("Version")) return plugin.getDescription().getVersion();

        if (identifier.equals("Authors"))
            return "§6" + String.join("§7, §6", plugin.getDescription().getAuthors()) + "§r";

        return null;
    }
}