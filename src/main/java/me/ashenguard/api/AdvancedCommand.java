package me.ashenguard.api;

import me.ashenguard.api.spigot.SpigotPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@SuppressWarnings({"SameReturnValue", "unused", "ConstantConditions"})
public abstract class AdvancedCommand implements CommandExecutor, TabCompleter {
    protected final SpigotPlugin plugin;
    protected final String name;
    protected final boolean playerOnly;
    protected final String playerOnlyMessage;

    public AdvancedCommand(SpigotPlugin plugin, String name, boolean playerOnly) {
        this(plugin, name, playerOnly, "§cError:§r This command can be executed by a player");
    }

    public AdvancedCommand(SpigotPlugin plugin, String name, boolean playerOnly, String playerOnlyMessage) {
        plugin.getCommand(name).setExecutor(this);
        plugin.getCommand(name).setTabCompleter(this);

        this.plugin = plugin;
        this.name = name;
        this.playerOnly = playerOnly;
        this.playerOnlyMessage = playerOnlyMessage;
    }

    public abstract void run(CommandSender sender, Command command, String label, String[] args);
    public abstract List<String> tabs(CommandSender sender, Command command, String alias, String[] args);
    protected boolean playerOnlyCondition(CommandSender sender, Command command, String label, String[] args) {
        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        try {
            if (command.getName().equalsIgnoreCase(name)) {
                if (playerOnly && playerOnlyCondition(sender, command, label, args))
                    if (sender instanceof Player) run(sender, command, label, args);
                    else plugin.messenger.response(sender, "§cError:§r This command can be executed by a player");
                else run(sender, command, label, args);
            }
        } catch (Throwable throwable) {
            plugin.getLogger().log(Level.WARNING, String.format("Command %s for %s generated an exception", this.name, plugin.getDescription().getFullName()), throwable);
            plugin.messenger.response(sender, "§cWhile handling your request an unexpected error happened...§r");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> list = tabs(sender, command, alias, args);
        return list == null ? new ArrayList<>() : list;
    }
}
