package me.ashenguard.agmcore.extension;

import com.google.common.collect.ImmutableList;
import me.ashenguard.agmcore.AGMCore;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public abstract class ExtensionCommand extends BukkitCommand {
    protected final AGMCore core = AGMCore.getInstance();

    protected final String name;

    protected ExtensionCommand(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
        this.name = name;
    }

    public PluginCommand getCommand() {
        if (isRegistered()) return Bukkit.getPluginCommand(getName());
        return null;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        try {
            return run(sender, commandLabel, args);
        } catch (Throwable throwable) {
            core.getLogger().log(Level.WARNING, String.format("Command %s for %s generated an exception", this.name, core.getDescription().getFullName()), throwable);
            core.messenger.response(sender, "§cWhile handling your request an unexpected error happened...§r");
            return true;
        }
    }

    public abstract boolean run(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args);

    public boolean register() {
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            commandMap.register(core.getName(), this);
            return true;
        } catch (Throwable exception) {
            core.messenger.handleException(exception);
            return false;
        }
    }

    @Override public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        List<String> values = onTabComplete(sender, this, alias, args);
        values.sort(String.CASE_INSENSITIVE_ORDER);
        return values;
    }

    protected List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) return ImmutableList.of();
        String lastWord = args[args.length - 1];
        return onTabComplete(sender, command, lastWord);
    }

    protected List<String> onTabComplete(CommandSender sender, Command command, String lastWord) {
        Player senderPlayer = sender instanceof Player ? (Player) sender : null;

        ArrayList<String> matchedPlayers = new ArrayList<>();
        for (Player player : sender.getServer().getOnlinePlayers()) {
            String name = player.getName();
            if ((senderPlayer == null || senderPlayer.canSee(player)) && StringUtil.startsWithIgnoreCase(name, lastWord)) {
                matchedPlayers.add(name);
            }
        }

        return matchedPlayers;
    }
}
