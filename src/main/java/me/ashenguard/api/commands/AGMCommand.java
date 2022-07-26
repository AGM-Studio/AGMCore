package me.ashenguard.api.commands;

import me.ashenguard.api.commands.annotations.Execute;
import me.ashenguard.api.commands.annotations.PlayerOnly;
import me.ashenguard.api.commands.annotations.Subcommand;
import me.ashenguard.api.spigot.SpigotPlugin;
import me.ashenguard.utils.NestedMap;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public abstract class AGMCommand implements TabCompleter, CommandExecutor {
    protected final SpigotPlugin plugin;
    protected final String name;

    protected NestedMap<String, Executor> subcommands = new NestedMap<>();
    protected Executor executor = null;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0 && subcommands.hasNest(args[0])) {
            int arg = 1;
            NestedMap<String, Executor> map = subcommands.getNest(args[0]);
            while (args.length > arg && map.hasNest(args[arg])) map = map.getNest(args[arg++]);

            Executor executor = map.getValue(args[arg]);
            String[] newArgs = Arrays.copyOfRange(args, arg + 1, args.length);

            if (executor != null) executor.execute(sender, newArgs);
            else plugin.messenger.response(sender, String.format("Usage: %s", getUsage(arg, newArgs)));
        } else {
            if (executor != null) executor.execute(sender, args);
            else plugin.messenger.response(sender, String.format("Usage: %s", getUsage(0, args)));
        }
        return true;
    }

    private String getUsage(int arg, String[] args) {
        return null;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }

    protected AGMCommand(SpigotPlugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    public void register() {
        PluginCommand command = plugin.getCommand(name);
        if (command == null) throw new AGMCommandException("Command do not exists in the plugin");

        onRegister();

        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    private static void validate(Method method) {
        if (method.getParameterCount() != 2) throw new AGMCommandException("Only `CommandSender/Player` and `String[]` is allowed for a command");
        Class<?>[] parameters = method.getParameterTypes();
        if (method.isAnnotationPresent(PlayerOnly.class)) {
            if (Player.class.isAssignableFrom(parameters[0]) && parameters[1] != String[].class)
                throw new AGMCommandException("Only `Player` and `String[]` is allowed for a player only command");
        } else {
            if (CommandSender.class.isAssignableFrom(parameters[0]) && parameters[1] != String[].class)
                throw new AGMCommandException("Only `CommandSender` and `String[]` is allowed for a command");
        }
    }

    private void onRegister() {
        Method[] methods = getClass().getMethods();
        for (Method method: methods) {
            if (method.isAnnotationPresent(Subcommand.class)) {
                validate(method);

                String[] args = method.getAnnotation(Subcommand.class).value().split(" ");
                if (args.length == 0) throw new AGMCommandException("Unable to trace the commands");

                String last = args[args.length - 1];
                String[] subs = Arrays.copyOfRange(args, 0, args.length - 1);

                NestedMap<String, Executor> map = subcommands;
                for (String sub: subs) map = map.addNest(sub);

                if (map.getValue(last) != null) throw new AGMCommandException("Only one executor is allowed per command or subcommand");
                map.putValue(last, new Executor(this, method));
            }
            if (method.isAnnotationPresent(Execute.class)) {
                if (executor != null) throw new AGMCommandException("Only one executor is allowed per command or subcommand");
                executor = new Executor(this, method);
            }
        }
    }

    protected static class Executor {
        private final BiConsumer<CommandSender, String[]> consumer;

        protected Executor(AGMCommand command, Method method) {
            if (method.isAnnotationPresent(PlayerOnly.class)) {
                this.consumer = (sender, args) -> {
                    try {
                        if (sender instanceof Player)
                            method.invoke(command, sender, args);
                        else throw new AGMCommandException(command.plugin.translation.get("PlayerOnly", null), true);
                    } catch (AGMCommandException exception) {
                        if (exception.handle) command.plugin.messenger.response(sender, exception.getMessage());
                        else command.plugin.messenger.handleException(exception);
                    } catch (Throwable throwable) {
                        command.plugin.messenger.handleException(throwable);
                    }
                };
            } else {
                this.consumer = (sender, args) -> {
                    try {
                        method.invoke(command, sender, args);
                    } catch (AGMCommandException exception) {
                        if (exception.handle) command.plugin.messenger.response(sender, exception.getMessage());
                        else command.plugin.messenger.handleException(exception);
                    } catch (Throwable throwable) {
                        command.plugin.messenger.handleException(throwable);
                    }
                };
            }
        }
        public void execute(CommandSender sender, String[] args) {
            consumer.accept(sender, args);
        }
    }
}
