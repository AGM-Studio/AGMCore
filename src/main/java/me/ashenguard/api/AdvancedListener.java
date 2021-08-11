package me.ashenguard.api;

import me.ashenguard.api.spigot.SpigotPlugin;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;

import static org.bukkit.Bukkit.getServer;

public abstract class AdvancedListener implements Listener {
    private boolean registered = false;

    protected SpigotPlugin plugin;
    protected abstract void onRegister();

    public void register(SpigotPlugin plugin) {
        if (registered) return;

        this.plugin = plugin;
        getServer().getPluginManager().registerEvents(this, plugin);

        onRegister();
        registered = true;
    }

    public static void tryRegister(Class<? extends AdvancedListener> listener, SpigotPlugin plugin) {
        try {
            AdvancedListener instance = listener.getDeclaredConstructor().newInstance();
            instance.register(plugin);
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException exception) {
            plugin.messenger.handleException(exception);
        }
    }
    public static void tryRegister(Class<? extends AdvancedListener> listener, SpigotPlugin plugin, String excMessage) {
        try {
            AdvancedListener instance = listener.getDeclaredConstructor().newInstance();
            instance.register(plugin);
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException exception) {
            plugin.messenger.Warning(excMessage);
        }
    }
}
