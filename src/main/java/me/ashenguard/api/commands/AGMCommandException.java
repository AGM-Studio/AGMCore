package me.ashenguard.api.commands;

import me.ashenguard.agmcore.AGMCore;
import me.ashenguard.api.spigot.SpigotPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

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
    public static void checkMethodAccess(AGMCommand command, Method method) {
        if (!method.getDeclaringClass().isAssignableFrom(command.getClass()) || !method.canAccess(command))
            throw inaccessibleMethodError(command, method);

        Class<?>[] types = method.getParameterTypes();
        if (types.length == 0 || (!Player.class.isAssignableFrom(types[0]) && !CommandSender.class.isAssignableFrom(types[0])))
            throw incompatibleMethodError(command, method);
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
