package me.ashenguard.api;

import me.ashenguard.api.spigot.SpigotPlugin;
import me.ashenguard.exceptions.AdvancedCommandException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;

@SuppressWarnings({"SameReturnValue", "unused", "ConstantConditions"})
public abstract class AdvancedCommand implements CommandExecutor, TabCompleter {
    protected final SpigotPlugin plugin;
    protected final String name;

    protected Predicate<String[]> playerRequired = (args) -> false;

    public AdvancedCommand(SpigotPlugin plugin, String name) {
        plugin.getCommand(name).setExecutor(this);
        plugin.getCommand(name).setTabCompleter(this);

        this.plugin = plugin;
        this.name = name;
    }

    public abstract void run(CommandSender sender, Command command, String label, String[] args);
    public abstract List<String> tabs(CommandSender sender, Command command, String alias, String[] args);

    private final List<AdvancedSubcommand> subcommands = new ArrayList<>();
    protected void addSubcommand(AdvancedSubcommand subcommand) {
        if (subcommands.contains(subcommand)) return;
        subcommands.add(subcommand);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        try {
            if (playerRequired.test(args))
                AdvancedCommandException.checkIsPlayer(sender, plugin.translation.get("PlayerOnly", null));
            if (args.length > 0) {
                AdvancedSubcommand subcommand = subcommands.stream().filter(s -> s.match(args[0])).findFirst().orElse(null);
                if (subcommand != null)
                    return subcommand.onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
            }
            run(sender, command, label, args);
        } catch (AdvancedCommandException exception) {
            if (exception.getMessage() != null && exception.getMessage().length() > 0)
                plugin.messenger.response(sender, exception.getMessage().split("\n"));
        } catch (Throwable throwable) {
            plugin.getLogger().log(Level.WARNING, String.format("Command %s for %s generated an exception", this.name, plugin.getDescription().getFullName()), throwable);
            plugin.messenger.response(sender, "§cWhile handling your request an unexpected error happened...§r");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> list = new ArrayList<>();
        List<String> tabs = tabs(sender, command, alias, args);

        if (tabs != null) list.addAll(tabs);
        if (args.length == 0 || args.length == 1) subcommands.forEach(subcommand -> list.addAll(subcommand.aliases));
        else subcommands.stream().filter(s -> s.match(args[0])).findFirst().ifPresent(subcommand -> list.addAll(subcommand.onTabComplete(sender, command, alias, Arrays.copyOfRange(args, 1, args.length))));

        String arg = args[args.length - 1];
        return list.stream().filter(tab -> tab.toLowerCase().startsWith(arg.toLowerCase())).toList();
    }
}
