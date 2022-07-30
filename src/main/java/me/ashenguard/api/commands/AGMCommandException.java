package me.ashenguard.api.commands;

import me.ashenguard.agmcore.AGMCore;
import me.ashenguard.api.messenger.Message;
import me.ashenguard.api.spigot.SpigotPlugin;
import me.ashenguard.api.utils.encoding.Ordinal;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.List;

public class AGMCommandException extends RuntimeException {
    private static String getMessage(SpigotPlugin plugin, String key, String def, Object... format) {
        String message = plugin.translation.get(key, null);
        if (message != null) return String.format(message, format);

        return String.format(AGMCore.getTranslation().get(key, def), format);
    }

    // Runtime exceptions
    public static AGMCommandException playerOnly(AGMCommand command) {
        String message = getMessage(command.plugin, "PlayerOnlyCommandError", "§cThis command can only be executed by a player.");
        return new AGMCommandException(message, true);
    }
    public static AGMCommandException missingArgument(AGMCommand command, Class<?> type, int argument) {
        String message = getMessage(command.plugin, "MissingArgumentError", "§cThis command requires an %s as %s argument.", type.getSimpleName(), Ordinal.to(argument));
        return new AGMCommandException(message, true);
    }
    public static AGMCommandException unexpectedError(AGMCommand command, Throwable cause) {
        String message = getMessage(command.plugin, "UnexpectedCommandError", "§cAn unexpected error happened while handling command \"§7/%s§c\"", command.name);
        return new AGMCommandException(message, cause, true);
    }

    // Initial exceptions
    public static AGMCommandException inaccessibleMethodError(AGMCommand command, Method method) {
        String message = String.format("Method \"%s\" is not accessible by command \"%s\"", method.getName(), command.getName());
        return new AGMCommandException(message);
    }
    public static AGMCommandException incompatibleMethodError(AGMCommand command, Method method) {
        String message = String.format("Method \"%s\" from command \"%s\" is not compatible", method.getName(), command.getName());
        return new AGMCommandException(message);
    }


    // Checks
    protected static void checkMethodAccess(AGMCommand command, Method method) {
        if (!method.getDeclaringClass().isAssignableFrom(command.getClass()) || !method.canAccess(command))
            throw inaccessibleMethodError(command, method);
    }
    protected static void checkMethodCompatibleRecommender(AGMCommand command, Method method) {
        if (method.getReturnType() != List.class) throw incompatibleMethodError(command, method);

        Class<?>[] types = method.getParameterTypes();
        if (types.length != 0 && (types.length != 1 || types[0] != String.class))
            throw incompatibleMethodError(command, method);
    }
    protected static void checkMethodCompatibleCommand(AGMCommand command, Method method) {
        Class<?>[] types = method.getParameterTypes();
        if (types.length == 0 || (!Player.class.isAssignableFrom(types[0]) && !CommandSender.class.isAssignableFrom(types[0])))
            throw incompatibleMethodError(command, method);
    }
    public static void check(AGMCommand command, boolean condition, Message message) {
        if (condition) return;

        throw new AGMCommandException(message.getMessage(command.plugin), true);
    }

    public final boolean handle;

    public AGMCommandException() {
        super();
        this.handle = false;
    }
    public AGMCommandException(String message) {
        super(message);
        this.handle = false;
    }
    public AGMCommandException(String message, Throwable cause) {
        super(message, cause);
        this.handle = false;
    }
    public AGMCommandException(Throwable cause) {
        this(cause.getMessage(), cause);
    }
    public AGMCommandException(boolean handle) {
        super();
        this.handle = handle;
    }
    public AGMCommandException(String message, boolean handle) {
        super(message);
        this.handle = handle;
    }
    public AGMCommandException(String message, Throwable cause, boolean handle) {
        super(message, cause);
        this.handle = handle;
    }
    public AGMCommandException(Throwable cause, boolean handle) {
        this(cause.getMessage(), cause, handle);
    }
}
