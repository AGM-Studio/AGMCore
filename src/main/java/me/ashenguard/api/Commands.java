package me.ashenguard.api;

import me.ashenguard.api.messenger.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"SameReturnValue", "unused"})
public abstract class Commands implements CommandExecutor, TabCompleter {

    protected final JavaPlugin plugin;
    protected final String name;
    protected final boolean playerOnly;
    protected final String playerOnlyMessage;

    public Commands(JavaPlugin plugin, String name, boolean playerOnly) {
        this(plugin, name, playerOnly, "§cError:§r This command can be executed by a player");
    }

    public Commands(JavaPlugin plugin, String name, boolean playerOnly, String playerOnlyMessage) {
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
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase(name)) {
            if (playerOnly && playerOnlyCondition(sender, command, label, args))
                if (sender instanceof Player) run(sender, command, label, args);
                else Messenger.getInstance(plugin).send(sender, "§cError:§r This command can be executed by a player");
            else run(sender, command, label, args);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = tabs(sender, command, alias, args);
        return list == null ? new ArrayList<>() : list;
    }
}
