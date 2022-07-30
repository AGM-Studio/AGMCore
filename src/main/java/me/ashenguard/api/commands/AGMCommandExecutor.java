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

    private BiConsumer<CommandSender, String[]> consumer = null;
    private List<AGMCommandArgument> arguments = new ArrayList<>();

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
        AGMCommandException.checkMethodAccess(command, method);
        AGMCommandException.checkMethodCompatibleCommand(command, method);

        Class<?>[] types = method.getParameterTypes();
        Predicate<CommandSender> isPlayer = Player.class.isAssignableFrom(types[0]) ? sender -> sender instanceof Player : sender -> true;

        arguments = new ArrayList<>();
        for (int i = 1; i < types.length; i++) arguments.add(AGMCommandArgument.from(command, types[i], i));

        this.consumer = (sender, args) -> {
            if (!isPlayer.test(sender)) throw AGMCommandException.playerOnly(command);

            List<Object> values = new ArrayList<>();
            for (int i = 0; i < arguments.size(); i++) {
                if (args.length > i) values.add(arguments.get(i).cast(args[i]));
                else values.add(arguments.get(i).getDefault());
            }

            try {
                values.add(0, sender);
                method.invoke(this.command, values.toArray());
            } catch (Throwable throwable) {
                this.command.plugin.messenger.response(sender, "Something went wrong while executing your command. If the issue happened again please inform an administrator");
                throw AGMCommandException.unexpectedError(command, throwable);
            }
        };
    }

    @SuppressWarnings("unchecked")
    protected void setRecommender(int argument, Method method) {
        AGMCommandException.checkMethodAccess(command, method);
        AGMCommandException.checkMethodCompatibleRecommender(command, method);

        arguments.get(argument).setRecommender(s -> {
            try {
                return (List<String>) method.invoke(this.command, s);
            } catch (Throwable ignored1) {
                try {
                    return (List<String>) method.invoke(this.command);
                } catch (Throwable ignored2) {
                    // It will be ignored so no need to specify a message
                    throw new AGMCommandException();
                }
            }
        });
    }

    public void execute(CommandSender sender, String[] args) {
        if (consumer == null) return;
        consumer.accept(sender, args);
    }

    public List<String> getTab(String[] args) {
        if (args.length == 0) return arguments.get(0).recommend("");
        if (args.length > arguments.size()) return null;
        return arguments.get(args.length - 1).recommend(args[args.length - 1]);
    }
}
