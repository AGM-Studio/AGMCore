package me.ashenguard.api.commands;

import me.ashenguard.api.commands.annotations.AGMArgumentRecommender;
import me.ashenguard.api.commands.annotations.AGMCommandHandler;
import me.ashenguard.api.commands.annotations.AGMSubcommandHandler;
import me.ashenguard.api.spigot.SpigotPlugin;
import me.ashenguard.utils.NestedMap;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public abstract class AGMCommand implements TabCompleter, CommandExecutor {
    protected final SpigotPlugin plugin;
    protected final String name;

    protected NestedMap<String, AGMCommandExecutor> subcommands = new NestedMap<>();
    protected final AGMCommandExecutor executor = new AGMCommandExecutor(this);

    public AGMCommandExecutor getExecutor(String[] args) {
        return executor.getExecutor(args);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        AGMCommandExecutor executor = getExecutor(args);
        try {
            if (executor != null) executor.execute(sender, Arrays.copyOfRange(args, executor.getDepth(), args.length));
        } catch (AGMCommandException exception) {
            if (exception.handle) this.plugin.messenger.response(sender, exception.getMessage());
            else this.plugin.messenger.handleException(String.format("While executing command \"/%s\" an unexpected error occurred", name), exception);
        } catch (Throwable throwable) {
            this.plugin.messenger.handleException(String.format("While executing command \"/%s\" an unexpected error occurred", name), throwable);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        AGMCommandExecutor executor = getExecutor(args);
        if (executor == null) return new ArrayList<>();

        String arg = args.length > 0 ? args[args.length - 1] : "";
        Set<String> subs = executor.subcommands.keySet();
        List<String> list = new ArrayList<>();
        if (subs.stream().anyMatch(sub -> sub.toLowerCase().startsWith(arg.toLowerCase()))) list.addAll(subs);
        else {
            List<String> tabs = executor.getTab(Arrays.copyOfRange(args, executor.getDepth(), args.length));
            if (tabs != null) list.addAll(tabs);
        }

        return list.stream().filter(tab -> tab.toLowerCase().startsWith(arg.toLowerCase())).sorted().toList();
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

    private void onRegister() {
        Method[] methods = this.getClass().getDeclaredMethods();
        for (Method method: methods) {
            if (method.isAnnotationPresent(AGMCommandHandler.class)) {
                this.executor.setConsumer(method);
            }
            if (method.isAnnotationPresent(AGMSubcommandHandler.class)) {
                String[] args = method.getAnnotation(AGMSubcommandHandler.class).value().split(" ");
                if (args.length == 0) throw new AGMCommandException("Unable to trace the commands");

                AGMCommandExecutor executor = this.executor;
                for (int i = 0; i < args.length; i++) {
                    executor.subcommands.putIfAbsent(args[i].toLowerCase(), new AGMCommandExecutor(this, i + 1));
                    executor = executor.subcommands.get(args[i].toLowerCase());
                }

                executor.setConsumer(method);
            }
            if (method.isAnnotationPresent(AGMArgumentRecommender.class)) {
                AGMArgumentRecommender annotation = method.getAnnotation(AGMArgumentRecommender.class);

                if ("".equals(annotation.value())) this.executor.setRecommender(annotation.arg(), method);
                else {
                    String[] args = annotation.value().split(" ");

                    AGMCommandExecutor executor = this.executor;
                    for (int i = 0; i < args.length; i++) {
                        executor.subcommands.putIfAbsent(args[i].toLowerCase(), new AGMCommandExecutor(this, i + 1));
                        executor = executor.subcommands.get(args[i].toLowerCase());
                    }

                    executor.setRecommender(annotation.arg(), method);
                }
            }
        }
    }

    public String getName() {
        return name;
    }
}
