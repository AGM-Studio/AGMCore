package me.ashenguard.api;

import me.ashenguard.api.spigot.SpigotPlugin;
import me.ashenguard.exceptions.InstanceAssertionError;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public abstract class AdvancedSubcommand {
    protected final String name;
    protected final boolean ignoreCase;
    protected final List<String> aliases = new ArrayList<>();

    protected final SpigotPlugin plugin;

    protected Predicate<String[]> playerRequired = (args) -> false;

    public AdvancedSubcommand(AdvancedCommand base, String name, boolean ignoreCase) {
        this(base.plugin, name, ignoreCase);
        base.addSubcommand(this);
    }
    public AdvancedSubcommand(AdvancedSubcommand base, String name, boolean ignoreCase) {
        this(base.plugin, name, ignoreCase);
        base.addSubcommand(this);
    }
    public AdvancedSubcommand(SpigotPlugin plugin, String name, boolean ignoreCase) {
        this.name = name;
        this.ignoreCase = ignoreCase;
        this.plugin = plugin;
        aliases.add(name);
    }

    public AdvancedSubcommand withAliases(String... aliases) {
        this.aliases.addAll(Arrays.asList(aliases));
        return this;
    }

    public boolean match(String arg) {
        if (ignoreCase) {
            for (String alias: aliases) if (alias.equalsIgnoreCase(arg)) return true;
        } else {
            for (String alias: aliases) if (alias.equals(arg)) return true;
        }
        return false;
    }

    public abstract void run(CommandSender sender, Command command, String label, String[] args);
    public abstract List<String> tabs(CommandSender sender, Command command, String alias, String[] args);

    private final List<AdvancedSubcommand> subcommands = new ArrayList<>();
    protected void addSubcommand(AdvancedSubcommand subcommand) {
        if (subcommands.contains(subcommand)) return;
        subcommands.add(subcommand);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (playerRequired.test(args))
            InstanceAssertionError.check(sender, Player.class, plugin.translation.get("PlayerOnly", null));
        if (args.length > 0) {
            AdvancedSubcommand subcommand = subcommands.stream().filter(s -> s.match(args[0])).findFirst().orElse(null);
            if (subcommand != null) {
                subcommand.onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
                return true;
            }
        }
        run(sender, command, label, args);
        return true;
    }

    public Collection<? extends String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        List<String> tabs = tabs(sender, command, alias, args);

        if (tabs != null) list.addAll(tabs);
        if (args.length == 0 || args.length == 1) subcommands.forEach(subcommand -> list.addAll(subcommand.aliases));
        else subcommands.stream().filter(s -> s.match(args[0])).findFirst().ifPresent(subcommand -> list.addAll(subcommand.onTabComplete(sender, command, alias, Arrays.copyOfRange(args, 1, args.length))));

        String arg = args[args.length - 1];
        return list.stream().filter(tab -> tab.toLowerCase().startsWith(arg.toLowerCase())).toList();
    }
}
