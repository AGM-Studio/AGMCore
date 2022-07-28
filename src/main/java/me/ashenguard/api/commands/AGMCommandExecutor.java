package me.ashenguard.api.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

class AGMCommandExecutor {
    private final int depth;
    private final AGMCommand command;
    private BiConsumer<CommandSender, String[]> consumer;

    protected final Map<String, AGMCommandExecutor> subcommands = new HashMap<>();

    public AGMCommandExecutor(AGMCommand command) {
        this.depth = 0;
        this.command = command;
    }
    public AGMCommandExecutor(AGMCommand command, int depth) {
        this.depth = depth;
        this.command = command;
    }

    protected AGMCommandExecutor getExecutor(String[] args) {
        if (args.length > 0) {
            AGMCommandExecutor executor = subcommands.get(args[0].toLowerCase());
            if (executor != null) return executor.getExecutor(Arrays.copyOfRange(args, 1, args.length));
        }

        return this;
    }
    protected int getDepth() {
        return depth;
    }

    protected void setConsumer(Method method) {
        if (!method.getDeclaringClass().isAssignableFrom(command.getClass()))
            throw new AGMCommandException(String.format("Method \"%s\" do not belong to the command class", method.getName()));

        if (!method.canAccess(command)) {
            throw new AGMCommandException(String.format("Method \"%s\" is not accessible by command executor", method.getName()));
        }

        Class<?>[] types = method.getParameterTypes();
        if (types.length == 0) throw new AGMCommandException("The first argument of a command handler must be a `CommandSender` or `Player`");

        if (!Player.class.isAssignableFrom(types[0]) && !CommandSender.class.isAssignableFrom(types[0]))
            throw new AGMCommandException("The first argument of the method must be a `CommandSender` or `Player`");

        Predicate<CommandSender> isPlayer = Player.class.isAssignableFrom(types[0]) ? sender -> sender instanceof Player : sender -> true;

        final List<AGMCommandArgument> arguments = new ArrayList<>();
        for (int i = 1; i < types.length; i++) arguments.add(AGMCommandArgument.getType(types[i]));

        this.consumer = (sender, args) -> {
            if (!isPlayer.test(sender)) throw new AGMCommandException("Only a player can use this command", true);

            List<Object> values = new ArrayList<>();
            values.add(sender);
            for (int i = 0; i < arguments.size(); i++) {
                if (args.length > i) values.add(arguments.get(i).cast(args[i]));
                else {
                    try {
                        values.add(arguments.get(i).cast(null));
                    } catch (Throwable ignored) {
                        throw new AGMCommandException("Not enough arguments for the command", true);
                    }
                }
            }

            try {
                method.invoke(this.command, values.toArray());
            } catch (Throwable throwable) {
                this.command.plugin.messenger.response(sender, "Something went wrong while executing your command. If the issue happened again please inform an administrator");
                throw new AGMCommandException("Unable to execute command", throwable);
            }
        };
    }

    protected AGMCommandExecutor(AGMCommand command, Method method) {
        this.command = command;
        this.depth = 0;
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

    public void execute(CommandSender sender, String[] args) {
        consumer.accept(sender, args);
    }

    public List<String> getTab(String[] args) {
        return null;
    }
}
