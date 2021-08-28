package me.ashenguard.api.messenger;

import me.ashenguard.api.spigot.SpigotPlugin;
import me.ashenguard.api.spigot.SpigotResource;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

@SuppressWarnings("unused")
public class Messenger {
    public final SpigotPlugin plugin;
    public final File exceptionFolder;

    public boolean debugger;
    public HashMap<MessageMode, Boolean> inGameMessaging;
    public HashMap<String, Boolean> debugs;
    public String prefix;

    public Messenger(SpigotPlugin plugin) {
        this.plugin = plugin;
        setup(null);

        exceptionFolder = new File(plugin.getDataFolder(), "Exception");
        if (!exceptionFolder.exists() && exceptionFolder.mkdirs()) Debug("General", "Exception folder wasn't found, A new one created");
    }

    /**
     * @param config the {@link Configuration} that should configure the messenger
     */
    public void setup(Configuration config) {
        inGameMessaging = new HashMap<>();
        inGameMessaging.put(MessageMode.Info, config == null || config.getBoolean("InGameMessages.Info", true));
        inGameMessaging.put(MessageMode.Warning, config == null || config.getBoolean("InGameMessages.Warning", true));
        inGameMessaging.put(MessageMode.Debug, config == null || config.getBoolean("InGameMessages.Debug", true));

        this.debugger = config != null && config.getBoolean("Debug.Enable", false);
        this.prefix = config == null ? plugin.getName() : config.getString("Prefix", plugin.getName());

        debugs = new HashMap<>();
        if (config != null) {
            ConfigurationSection section = config.getConfigurationSection("Debug");
            if (section != null) for (String key : section.getKeys(false)) if (!key.equals("Enable")) debugs.put(key, section.getBoolean(key, true));
        }
    }

    /**
     * A method as a shortcut for {@link #sendMessage(MessageMode, CommandSender, String...) sendMessage} for {@link MessageMode#Debug}.
     * It also will check if {@link #debugger} is set to "True" and the type given is enabled.
     */
    public void Debug(String type, String... messages) {
        if (debugger && debugs.getOrDefault(type, true))
            sendMessage(MessageMode.Debug, Bukkit.getConsoleSender(), messages);
    }
    /**
     * A method as a shortcut for {@link #sendMessage(MessageMode, CommandSender, String...) sendMessage} for {@link MessageMode#Warning}.
     */
    public void Warning(String... messages) {
        sendMessage(MessageMode.Warning, Bukkit.getConsoleSender(), messages);
    }
    /**
     * A method as a shortcut for {@link #sendMessage(MessageMode, CommandSender, String...) sendMessage} for {@link MessageMode#Info}.
     */
    public void Info(String... messages) {
        sendMessage(MessageMode.Info, Bukkit.getConsoleSender(), messages);
    }
    /**
     * A method as a shortcut for {@link #sendMessage(MessageMode, CommandSender, String...) sendMessage} for {@link MessageMode#Empty} which will send message as normal without any formatting or etc.
     */
    public void send(CommandSender target, String... messages) {
        sendMessage(MessageMode.Empty, target, messages);
    }
    /**
     * A method as a shortcut for {@link #sendMessage(MessageMode, CommandSender, String...) sendMessage} for {@link MessageMode#Personal}.
     */
    public void response(CommandSender target, String... messages) {
        sendMessage(MessageMode.Personal, target, messages);
    }

    /**
     * The main method that will send the messages to their targets as well as formatting them.
     * 
     * @param mode The mode message is going to be sent
     * @param target The target messages will be sent to
     * @param messages The messages to be sent to the target
     */
    public void sendMessage(@NotNull MessageMode mode, CommandSender target, String... messages) {
        if (messages == null) return;
        boolean sendEveryone = (!(target instanceof Player) && (inGameMessaging.getOrDefault(mode, true) && !Arrays.asList(MessageMode.Personal, MessageMode.Operator, MessageMode.Empty).contains(mode)));
        Collection<? extends Player> players = sendEveryone ? Bukkit.getOnlinePlayers() : new ArrayList<>();
        MessageMode.Prefix prefix = mode.getPrefix(this);
        for (int i = 0; i < messages.length; i++) {
            String message = (i == 0 ? prefix.prefix : prefix.tab) + messages[i];
            target.sendMessage(message);
            if (sendEveryone)
                for (Player player:players)
                    if (mode.hasPermission(plugin, player))
                        player.sendMessage(message);
        }
    }

    /**
     * Forced {@link #updateNotification(CommandSender, boolean)}
     */
    public void updateNotification(CommandSender target) {
        updateNotification(target, true);
    }

    /**
     * It will capture the resource from spigot API and will check if it is up to date or not.
     */
    public void updateNotification(CommandSender target, boolean updates) {
        if (target == null || !target.isOp() || plugin == null) return;

        SpigotResource resource = new SpigotResource(plugin.getSpigotID());
        reminder(() -> {
            // Update Check
            if (updates && resource.version.isHigher(plugin.getVersion())) {
                send(target, "There is a §anew update§r available on SpigotMC");
                send(target, String.format("This version: §c%s§r", plugin.getVersion()));
                send(target, "SpigotMC version: §a%s§r", resource.version.toString(true));
            }
        });
    }

    /**
     * A 5 second delay to call the runnable given using {@link #reminder(Runnable, long)}
     */
    public void reminder(Runnable runnable) {
        reminder(runnable, 100L);
    }

    /**
     * A method using {@link org.bukkit.scheduler.BukkitScheduler#scheduleSyncDelayedTask(Plugin, Runnable, long)} to send a message with a delay
     */
    public void reminder(Runnable runnable, long delay) {
        if (plugin == null) return;
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, runnable, delay);
    }

    /**
     * A method that will save the exception in the folder in the plugin's folder
     */
    public void handleException(Throwable exception) {
        handleException(exception, exceptionFolder);
    }

    /**
     * A method that will save the exception in a custom folder given
     */
    public void handleException(Throwable exception, File exceptionFolder) {
        Warning("An error occurred");
        int count = 1;
        File file = new File(exceptionFolder, "Exception_1.warn");
        while (file.exists()) file = new File(exceptionFolder, String.format("Exception_%d.warn", ++count));
        try {
            PrintStream ps = new PrintStream(file); exception.printStackTrace(ps); ps.close();
            Warning("Exception saved as \"§cException_ " + count + ".warn§r\"");
        } catch (Exception ignored) {
            exception.printStackTrace();
        }
    }
}