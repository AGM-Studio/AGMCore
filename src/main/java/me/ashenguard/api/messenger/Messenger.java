package me.ashenguard.api.messenger;

import me.ashenguard.api.spigot.SpigotPlugin;
import me.ashenguard.api.spigot.SpigotResource;
import me.ashenguard.api.versions.Version;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class Messenger {
    public final SpigotPlugin plugin;
    public final File exceptionFolder;

    public boolean debugger;
    public HashMap<MessageMode, Boolean> inGameMessaging;
    public HashMap<String, Boolean> debugs;
    public String prefix;

    private final List<String> criticalCache = new ArrayList<>();
    private final List<String> broadcastPlayersCache = new ArrayList<>();
    private List<String> broadcastMessageCache = new ArrayList<>();

    public Messenger(SpigotPlugin plugin) {
        this.plugin = plugin;
        setup(null);

        exceptionFolder = new File(plugin.getDataFolder(), "Exception");
        if (!exceptionFolder.exists() && exceptionFolder.mkdirs())
            debug("General", "Exception folder wasn't found, A new one created");
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
            if (section != null) for (String key : section.getKeys(false))
                if (!key.equals("Enable")) debugs.put(key, section.getBoolean(key, true));
        }
    }

    /**
     * A method as a shortcut for {@link #sendMessage(MessageMode, CommandSender, String...) sendMessage} for {@link MessageMode#Debug}.
     * It also will check if {@link #debugger} is set to "True" and the type given is enabled.
     */
    public void debug(String type, String... messages) {
        if (debugger && debugs.getOrDefault(type, true))
            sendMessage(MessageMode.Debug, Bukkit.getConsoleSender(), messages);
    }

    /**
     * @deprecated use {@link #debug(String, String...)}
     */
    public void Debug(String type, String... messages) {
        debug(type, messages);
    }

    /**
     * A method as a shortcut for {@link #sendMessage(MessageMode, CommandSender, String...) sendMessage} for {@link MessageMode#Warning}.
     */
    public void critical(String... messages) {
        sendMessage(MessageMode.Critical, Bukkit.getConsoleSender(), messages);
    }

    /**
     * @deprecated use {@link #critical(String...)}
     */
    public void Critical(String... messages) {
        critical(messages);
    }

    /**
     * A method as a shortcut for {@link #sendMessage(MessageMode, CommandSender, String...) sendMessage} for {@link MessageMode#Warning}.
     */
    public void warning(String... messages) {
        sendMessage(MessageMode.Warning, Bukkit.getConsoleSender(), messages);
    }

    /**
     * @deprecated use {@link #warning(String...)}
     */
    public void Warning(String... messages) {
        warning(messages);
    }

    /**
     * A method as a shortcut for {@link #sendMessage(MessageMode, CommandSender, String...) sendMessage} for {@link MessageMode#Info}.
     */
    public void info(String... messages) {
        sendMessage(MessageMode.Info, Bukkit.getConsoleSender(), messages);
    }

    /**
     * @deprecated use {@link #info(String...)}
     */
    public void Info(String... messages) {
        info(messages);
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
     * A method as a shortcut for {@link #sendMessage(MessageMode, CommandSender, String...) sendMessage} for {@link MessageMode#BroadCast} or .{@link MessageMode#CriticalBroadCast}
     */
    public void broadcast(boolean critical, String... messages) {
        sendMessage(critical ? MessageMode.CriticalBroadCast : MessageMode.BroadCast, Bukkit.getConsoleSender(), messages);
    }

    /**
     * A method as a shortcut for {@link #sendMessage(MessageMode, CommandSender, String...) sendMessage} for {@link MessageMode#BroadCast}
     */
    public void broadcast(String... messages) {
        sendMessage(MessageMode.BroadCast, Bukkit.getConsoleSender(), messages);
    }

    /**
     * The main method that will send the messages to their targets as well as formatting them.
     *
     * @param mode     The mode message is going to be sent
     * @param target   The target messages will be sent to
     * @param messages The messages to be sent to the target
     */
    public void sendMessage(@NotNull MessageMode mode, CommandSender target, String... messages) {
        if (messages == null) return;
        MessageMode.Prefix prefix = mode.getPrefix(this);
        List<String> newMessages = Arrays.stream(messages).map(message -> (message.equals(messages[0]) ? prefix.prefix : prefix.tab) + message).toList();

        if (mode == MessageMode.CriticalBroadCast) {
            broadcastMessageCache = newMessages;
            broadcastPlayersCache.clear();
        }

        Collection<? extends Player> players = mode.sendAll ? Bukkit.getOnlinePlayers().stream().filter(player -> mode.hasPermission(plugin, player)).toList() : new ArrayList<>();
        newMessages.forEach(message -> {
            target.sendMessage(target instanceof Player ? PlaceholderManager.translate((Player) target, message) : PlaceholderManager.translate(message));
            for (Player player : players) {
                player.sendMessage(PlaceholderManager.translate(player, message));
                if (mode == MessageMode.CriticalBroadCast)
                    broadcastPlayersCache.add(player.getUniqueId().toString());
            }

            if (mode == MessageMode.Critical && players.size() > 0)
                criticalCache.add(message);
        });
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
    public void updateNotification(@NotNull CommandSender target, boolean updates) {
        if (plugin == null || !plugin.isEnabled()) return;
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            Version version = SpigotResource.getVersion(this.plugin.getSpigotID());
            reminder(() -> {
                // Update Check
                if (updates && version.isHigher(plugin.getVersion())) {
                    response(target, "There is a §anew update§r available on SpigotMC");
                    response(target, String.format("This version: §c%s§r", plugin.getVersion()));
                    response(target, String.format("SpigotMC version: §a%s§r", version.toString(true)));
                }
            });
        });
    }

    /**
     * Will send all available caches to the player
     */
    public void sendCaches(@NotNull Player player) {
        if (!broadcastPlayersCache.contains(player.getUniqueId().toString())) {
            broadcastMessageCache.forEach(player::sendMessage);
            broadcastPlayersCache.add(player.getUniqueId().toString());
        }
        if (player.isOp()) {
            for (String message : criticalCache)
                player.sendMessage(message);
            criticalCache.clear();
        }
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
     * A method that will save the exception in the default folder with default "Exception" name.
     * A number will be added to the end to avoid overwriting.
     *
     * @return the number added to file name
     */
    public int handleException(Throwable throwable) {
        return handleException(throwable, exceptionFolder, throwable.getClass().getSimpleName());
    }

    /**
     * A method that will save the exception in the defined folder with default "Exception" name. If given file is not a directory default folder will be used.
     * A number will be added to the end to avoid overwriting.
     *
     * @return the number added to file name
     */
    public int handleException(Throwable throwable, File exceptionFolder) {
        return handleException(throwable, exceptionFolder, throwable.getClass().getSimpleName());
    }

    /**
     * A method that will save the exception in the default folder with defined name.
     * A number will be added to the end to avoid overwriting.
     *
     * @return the number added to file name
     */
    public int handleException(Throwable throwable, String filename) {
        return handleException(throwable, exceptionFolder, filename);
    }

    /**
     * A method that will save the exception in the defined folder with defined name. If given file is not a directory default folder will be used.
     * A number will be added to the end to avoid overwriting.
     *
     * @return the number added to file name
     */
    public int handleException(Throwable exception, File exceptionFolder, String filename) {
        return handleException("An unexpected error occurred", exception, exceptionFolder, filename);
    }

    /**
     * A method that will save the exception in the default folder with default "Exception" name.
     * A number will be added to the end to avoid overwriting.
     *
     * @return the number added to file name
     */
    public int handleException(String message, Throwable throwable) {
        return handleException(message, throwable, exceptionFolder, throwable.getClass().getSimpleName());
    }

    /**
     * A method that will save the exception in the defined folder with default "Exception" name. If given file is not a directory default folder will be used.
     * A number will be added to the end to avoid overwriting.
     *
     * @return the number added to file name
     */
    public int handleException(String message, Throwable throwable, File exceptionFolder) {
        return handleException(message, throwable, exceptionFolder, throwable.getClass().getSimpleName());
    }

    /**
     * A method that will save the exception in the default folder with defined name.
     * A number will be added to the end to avoid overwriting.
     *
     * @return the number added to file name
     */
    public int handleException(String message, Throwable throwable, String filename) {
        return handleException(message, throwable, exceptionFolder, filename);
    }

    /**
     * A method that will save the exception in the defined folder with defined name. If given file is not a directory default folder will be used.
     * A number will be added to the end to avoid overwriting.
     *
     * @return the number added to file name
     */
    public int handleException(String message, Throwable exception, File exceptionFolder, String filename) {
        try {
            if (!exceptionFolder.isDirectory()) exceptionFolder = this.exceptionFolder;
            int count = 1;
            File file = new File(exceptionFolder, String.format("%s_1.warn", filename));
            while (file.exists()) file = new File(exceptionFolder, String.format("%s_%d.warn", filename, ++count));

            PrintStream ps = new PrintStream(file);
            exception.printStackTrace(ps);
            ps.close();
            Warning(String.format("%s and was saved as \"§c%s_%d.warn§r\"", message, filename, count));
            if (!exceptionFolder.equals(this.exceptionFolder))
                Warning("Mentioned error has been saved in following folder", exceptionFolder.getAbsolutePath());
            return count;
        } catch (Exception saveException) {
            Warning(String.format("%s and was unable to save it due: %s", message, saveException.getMessage()));
            exception.printStackTrace();
            return -1;
        }
    }

    public static class Logger extends PluginLogger {
        private static final Pattern TASK_PATTERN = Pattern.compile("^Task #(\\d+) for (.*?) generated an exception$");
        private static final Pattern COMMAND_PATTERN = Pattern.compile("^Command (.*?) for (.*?) generated an exception$");

        public static void override(SpigotPlugin plugin) {
            try {
                Class<?> clazz = plugin.getClass();
                while (clazz != JavaPlugin.class) clazz = clazz.getSuperclass();

                Field field = clazz.getDeclaredField("logger");
                field.setAccessible(true);
                field.set(plugin, new Messenger.Logger(plugin, plugin.messenger));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        public Logger(@NotNull Plugin context, @NotNull Messenger messenger) {
            super(context);
            this.messenger = messenger;
        }

        private final Messenger messenger;
        private final Map<String, Integer> handledExceptions = new HashMap<>();

        private boolean handleTask(LogRecord logRecord, Throwable throwable) {
            Matcher matcher = TASK_PATTERN.matcher(logRecord.getMessage());
            if (matcher.find()) {
                int task = Integer.parseInt(matcher.group(1));
                String plugin = matcher.group(2);

                String pair = String.format("%s-%d", throwable.getClass().getName(), task);
                if (handledExceptions.containsKey(pair)) {
                    int file = handledExceptions.get(pair);
                    messenger.warning(String.format("Task #%d for %s generated \"§c%s_%d.warn§r\" again!", task, plugin, throwable.getClass().getSimpleName(), file));
                } else {
                    int file = messenger.handleException(logRecord.getMessage(), throwable, throwable.getClass().getSimpleName());
                    handledExceptions.put(pair, file);
                }
                return true;
            }
            return false;
        }

        private boolean handleCommand(LogRecord logRecord, Throwable throwable) {
            Matcher matcher = COMMAND_PATTERN.matcher(logRecord.getMessage());
            if (matcher.find()) {
                String command = matcher.group(1);
                String plugin = matcher.group(2);

                String pair = String.format("%s-%s", throwable.getClass().getName(), command);
                if (handledExceptions.containsKey(pair)) {
                    int file = handledExceptions.get(pair);
                    messenger.warning(String.format("Command %s for %s generated \"§c%s_%d.warn§r\" again!", command, plugin, throwable.getClass().getSimpleName(), file));
                } else {
                    int file = messenger.handleException(logRecord.getMessage(), throwable, throwable.getClass().getSimpleName());
                    handledExceptions.put(pair, file);
                }
                return true;
            }
            return false;
        }


        @Override
        public void log(@NotNull LogRecord logRecord) {
            if (logRecord.getThrown() != null) {
                Throwable throwable = logRecord.getThrown();

                if (handleTask(logRecord, throwable)) return;
                if (handleCommand(logRecord, throwable)) return;

                messenger.handleException(logRecord.getMessage(), throwable, throwable.getClass().getSimpleName());
                return;
            }

            if (Level.WARNING.equals(logRecord.getLevel())) messenger.warning(logRecord.getMessage());
            else if (Level.INFO.equals(logRecord.getLevel())) messenger.info(logRecord.getMessage());
            else super.log(logRecord);
        }
    }
}