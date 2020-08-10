package me.ashenguard.api.messenger;

import me.ashenguard.api.SpigotUpdater;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;

public class Messenger {
    public static JavaPlugin plugin = null;
    public static File exceptionFolder = null;

    public static boolean debugger = false;
    public static HashMap<MessageMode, Boolean> inGameMessaging = new HashMap<>();
    public static HashMap<String, Boolean> debugs = new HashMap<>();
    public static String prefix = "Messenger";
    public static String getPrefix() {
        return prefix;
    }

    public static void setup (JavaPlugin plugin, HashMap<MessageMode, Boolean> inGameMessaging, boolean debugger, HashMap<String, Boolean> debugs, String prefix) {
        Messenger.plugin = plugin;
        Messenger.inGameMessaging = inGameMessaging;
        Messenger.debugger = debugger;
        Messenger.debugs = debugs;
        Messenger.prefix = prefix;

        exceptionFolder = new File(plugin.getDataFolder(), "Exception");
        if (!exceptionFolder.exists())
            if (exceptionFolder.mkdirs())
                Messenger.Debug("General", "Exception folder wasn't found, A new one created");
    }
    public static void setup(JavaPlugin plugin, FileConfiguration config) {
        Messenger.plugin = plugin;

        HashMap<MessageMode, Boolean> inGameMessaging = new HashMap<>();
        inGameMessaging.put(MessageMode.Info, config == null || config.getBoolean("InGameMessages.Info", true));
        inGameMessaging.put(MessageMode.Warning, config == null || config.getBoolean("InGameMessages.Warning", true));
        inGameMessaging.put(MessageMode.Debug, config == null || config.getBoolean("InGameMessages.Debug", true));

        boolean debugger = config != null && config.getBoolean("Debug.Enable", false);

        HashMap<String, Boolean> debugs = new HashMap<>();
        if (config != null) {
            ConfigurationSection section = config.getConfigurationSection("Debug");
            if (section != null) {
                for (String key : section.getKeys(false))
                    if (!key.equals("Enable")) debugs.put(key, section.getBoolean(key, true));
            }
        }

        String prefix = config == null ? Messenger.getPrefix() : config.getString("Prefix", Messenger.getPrefix());

        setup(plugin, inGameMessaging, debugger, debugs, prefix);
    }

    public static void Debug(String type, String... messages) {
        if (debugger && debugs.getOrDefault(type, true))
            sendMessage(MessageMode.Debug, Bukkit.getConsoleSender(), messages);
    }
    public static void Warning(String... messages) {
        sendMessage(MessageMode.Warning, Bukkit.getConsoleSender(), messages);
    }
    public static void Info(String... messages) {
        sendMessage(MessageMode.Info, Bukkit.getConsoleSender(), messages);
    }

    public static void send(CommandSender sender, String... messages) {
        sendMessage(MessageMode.Personal, sender, messages);
    }

    public static void sendMessage(MessageMode mode, CommandSender sender, String... messages) {
        if (messages == null) return;
        for (int i = 0; i < messages.length; i++) {
            String message = mode.getPrefix(i == 0) + messages[i];
            sender.sendMessage(message);
            if (sender instanceof Player) continue;
            sendAll(mode, message);
        }
    }
    public static void sendAll(MessageMode mode, String message) {
        if (inGameMessaging.getOrDefault(mode, true) && !(mode.equals(MessageMode.Operator) || mode.equals(MessageMode.Personal))) return;
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for (Player player:players)
            if (mode.hasPermission(player))
                player.sendMessage(message);
    }

    public static void updateNotification(CommandSender player, SpigotUpdater updater) {
        updateNotification(player, updater, true);
    }
    public static void updateNotification(CommandSender player, SpigotUpdater updater, boolean updates) {
        if (player == null || !player.isOp() || plugin == null) return;

        reminder(() -> {
            // Update Check
            if (updates && updater.newUpdate()) {
                send(player, "There is a §anew update§r available on SpigotMC");
                send(player, "This version: §c" + plugin.getDescription().getVersion() + "§r");
                send(player, "SpigotMC version: §a" + updater.getLatestVersion() + "§r");
            }
        });
    }

    public static void reminder(Runnable runnable) {
        reminder(runnable, 100L);
    }
    public static void reminder(Runnable runnable, long delay) {
        if (plugin == null) return;
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, runnable, delay);
    }

    public static void handleException(Exception exception) {
        handleException(exception, exceptionFolder);
    }
    public static void handleException(Exception exception, File exceptionFolder) {
        Messenger.Warning("An exception occurred");
        File file;
        int count = 0;
        do {
            count++;
            file = new File(exceptionFolder,"Exception_" + count + ".warn");
        } while (file.exists());
        try {
            PrintStream ps = new PrintStream(file);
            exception.printStackTrace(ps);
            ps.close();
            Messenger.Warning("Saved as \"§cException_ " + count + ".warn§r\"");
        } catch (FileNotFoundException ignored) {
            exception.printStackTrace();
        }
    }
}