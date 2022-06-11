package me.ashenguard.api.placeholder;

import me.ashenguard.agmcore.AGMCore;
import me.ashenguard.api.spigot.SpigotPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class PlaceholderExtension extends PlaceholderExpansion {
    protected final SpigotPlugin plugin;
    protected final List<Placeholder> placeholderList = new ArrayList<>();

    public void addPlaceholder(Placeholder placeholder) {
        placeholderList.add(placeholder);
    }

    public PlaceholderExtension(SpigotPlugin plugin) {
        this.plugin = plugin;
        new Placeholder(this, "Version", ((player, name) -> getVersion()));
        new Placeholder(this, "Authors", ((player, name) -> getAuthor()));
    }

    @Override public boolean register() {
        try {
            return super.register();
        } catch (Exception ignored) {
            return false;
        }
    }
    @Override public boolean persist() {
        return true;
    }
    @Override public boolean canRegister() {
        return true;
    }
    @Override public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }
    @Override public @NotNull String getIdentifier() {
        return plugin.getName();
    }
    @Override public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }
    @Override public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        AGMCore.getMessenger().Info("REQUEST", identifier);
        for (Placeholder placeholder: placeholderList)
            try {
                if (placeholder.isValid(identifier)) return placeholder.getValue(player, identifier);
            } catch (Throwable ignored) {}
        return null;
    }
}