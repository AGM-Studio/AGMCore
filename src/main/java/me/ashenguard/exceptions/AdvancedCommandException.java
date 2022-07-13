package me.ashenguard.exceptions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdvancedCommandException extends RuntimeException {
    public AdvancedCommandException() {
        super();
    }
    public AdvancedCommandException(String message) {
        super(message);
    }
    public AdvancedCommandException(String message, Throwable cause) {
        super(message, cause);
    }
    public AdvancedCommandException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    public static void check(boolean bool) {
        if (!bool) throw new AdvancedCommandException();
    }
    public static void check(boolean bool, String message) {
        if (!bool) throw new AdvancedCommandException(message);
    }
    public static void checkIsPlayer(CommandSender sender) {
        if (sender instanceof Player) return;
        throw new AdvancedCommandException();
    }
    public static void checkIsPlayer(CommandSender sender, String message) {
        if (sender instanceof Player) return;
        throw new AdvancedCommandException(message);
    }
    public static void fromException(Throwable throwable) {
        throw new AdvancedCommandException(throwable);
    }
    public static void fromException(Throwable throwable, String message) {
        throw new AdvancedCommandException(message, throwable);
    }
}
