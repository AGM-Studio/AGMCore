package me.ashenguard.api.spigot;

import me.ashenguard.api.bstats.Metrics;
import me.ashenguard.api.messenger.Messenger;
import me.ashenguard.api.versions.MCVersion;
import me.ashenguard.api.versions.Version;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"CanBeFinal", "unused"})
public abstract class SpigotPlugin extends JavaPlugin implements Listener {
    /**
     * @return the BStats ID for metrics of the plugin on the https://bstats.org/
     */
    public abstract int getBStatsID();

    /**
     * @return the Spigot Resource ID of the plugin for version checks
     */
    public abstract int getSpigotID();

    /**
     * @return the name of all plugins that this plugin needs, In case of one being missing, plugin will get disabled.
     */
    public @NotNull List<String> getRequirements() {
        return new ArrayList<>();
    }

    /**
     * @return the name of all plugins that this plugin should have, In case of one being missing, plugin will only send a warning.
     */
    public @NotNull List<String> getSoftRequirements() {
        return new ArrayList<>();
    }

    /**
     * The default value for update checks, It will be replaced by the value in config.yml at "Check.PluginUpdates"
     */
    public boolean updateNotification = true;
    /**
     * The messenger of the plugin with built-in message formatting.
     */
    public final Messenger messenger = new Messenger(this);
    /**
     * The Bstats metrics, Will use the value returned by {@link #getBStatsID()}
     */
    public final Metrics metrics = new Metrics(this, getBStatsID());

    /**
     * The replace of {@link JavaPlugin#onEnable()} override
     */
    public void onPluginEnable() {}

    /**
     * The replace of {@link JavaPlugin#onDisable()} override
     */
    public void onPluginDisable() {}

    /**
     * Override of {@link JavaPlugin#onEnable()}, It will save default config as well as enabling {@link #messenger} and updating {@link #updateNotification}.
     * It also register the class as a {@link Listener} to enable {@link #onJoinUNEvent(PlayerJoinEvent) onJoinUNEvent}.
     *
     * Then it will call {@link #onPluginEnable()} for extra works before sending the enabling message.
     * It will after that check if there is a new version of this plugin using Spigot API and send it in console.
     */
    @Override public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        if (getConfig().contains("Check.PluginUpdates"))
            updateNotification = getConfig().getBoolean("Check.PluginUpdates");
        messenger.setup(getConfig());

        for (String name: getRequirements()) {
            Plugin plugin = getServer().getPluginManager().getPlugin(name);
            if (plugin == null || !plugin.isEnabled()) {
                messenger.Warning("Dependencies are not satisfied. Disabling plugin to stop further issues.", String.format("Missing plugin: %s", name));
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }

        for (String name: getSoftRequirements()) {
            Plugin plugin = getServer().getPluginManager().getPlugin(name);
            if (plugin == null || !plugin.isEnabled())
                messenger.Warning("This plugin will work fine without following plugin, But it is recommended to install this plugin as well", String.format("Required plugin: %s", name));
        }

        getServer().getPluginManager().registerEvents(this, this);
        onPluginEnable();

        if (!Bukkit.getPluginManager().isPluginEnabled(this)) return;

        messenger.Info("The plugin was successfully enabled.");
        messenger.updateNotification(getServer().getConsoleSender());
    }

    /**
     * Override of {@link JavaPlugin#onDisable()} with nothing special happening.
     *
     * It just will call {@link #onPluginEnable()} for some works before sending the disabling message.
     */
    @Override
    public void onDisable() {
        onPluginDisable();

        messenger.Info("The plugin is now disabled.");
    }

    /**
     * A simple method to reload the plugin by calling {@link #onDisable()} and {@link #onEnable()}.
     */
    public void reload() {
        onDisable();
        onEnable();
    }

    /**
     * @return "False" if the server is 1.13+ or "True" otherwise
     */
    public boolean isLegacy() {
        return MCVersion.isLegacy();
    }

    /**
     * @return the {@link Version} style of the plugin.
     */
    public Version getVersion() {
        return new Version(this.getDescription().getVersion());
    }

    /**
     * Anytime an operator joins the server, If the {@link #updateNotification} is set to "True", Will check if there is a new version of the plugin and warn them if there is.
     */
    @EventHandler
    public void onJoinUNEvent(PlayerJoinEvent event) {
        messenger.updateNotification(event.getPlayer(), updateNotification);
    }
}
