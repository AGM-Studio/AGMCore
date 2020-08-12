package me.ashenguard.api.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class PAPIExpansion extends PlaceholderExpansion {
    protected JavaPlugin plugin;

    public PAPIExpansion(JavaPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @NotNull
    @Override
    public String getAuthor(){
        return "Ashenguard";
    }

    @NotNull
    @Override
    public String getIdentifier(){
        return plugin.getName();
    }

    @NotNull
    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier){
        if(player == null) return null;


        if (identifier.equals("Version")) {
            return plugin.getDescription().getVersion();
        }

        if (identifier.equals("Authors")) {
            return "§6" + String.join("§7, §6", plugin.getDescription().getAuthors()) + "§r";
        }

        return null;
    }
}