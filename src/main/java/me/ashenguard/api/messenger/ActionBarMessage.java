package me.ashenguard.api.messenger;

import me.ashenguard.agmcore.AGMCore;
import me.ashenguard.api.spigot.SpigotPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public abstract class ActionBarMessage {
    private static ActionBarMessage instance = null;
    public static ActionBarMessage getInstance() {
        if (instance == null) {
            instance = new ActionBarMessage_API(AGMCore.getInstance());
        }
        return instance;
    }
    public static void setInstance(ActionBarMessage instance) {
        if (ActionBarMessage.instance != null) return;
        ActionBarMessage.instance = instance;
    }

    public abstract void sendActionBarMessage(SpigotPlugin plugin, Player player, String message);

    public static void sendActionBar(SpigotPlugin plugin, Player player, String message) {
        getInstance().sendActionBarMessage(plugin, player, message);
    }

    public static void sendActionBar(SpigotPlugin plugin, Player player, String message, int duration) {
        sendActionBar(plugin, player, message);

        if (duration >= 0)
            Bukkit.getScheduler().runTaskLater(plugin, () -> sendActionBar(plugin, player, ""), duration + 1);

        for (int i = duration; i > 40; i -= 40)
            Bukkit.getScheduler().runTaskLater(plugin, () -> sendActionBar(plugin, player, message), duration);
    }

    public static void sendActionBarToAllPlayers(SpigotPlugin plugin, String message) {
        sendActionBarToAllPlayers(plugin, message, -1);
    }

    public static void sendActionBarToAllPlayers(SpigotPlugin plugin, String message, int duration) {
        for (Player p : Bukkit.getOnlinePlayers()) sendActionBar(plugin, p, message, duration);
    }

    private final SpigotPlugin plugin;
    public ActionBarMessage(SpigotPlugin plugin) {
        this.plugin = plugin;
    }

    public void sendActionBar(Player player, String message) {
        sendActionBar(plugin, player, message);
    }

    public void sendActionBar(Player player, String message, int duration) {
        sendActionBar(plugin,player, message, duration);
    }

    public void sendActionBarToAllPlayers(String message) {
        sendActionBarToAllPlayers(plugin, message, -1);
    }

    public void sendActionBarToAllPlayers(String message, int duration) {
        for (Player p : Bukkit.getOnlinePlayers()) sendActionBar(plugin, p, message, duration);
    }
}
