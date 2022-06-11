package me.ashenguard.api.messenger;

import me.ashenguard.api.spigot.SpigotPlugin;
import org.bukkit.ChatColor;
import org.bukkit.permissions.Permissible;

import java.util.Collections;

@SuppressWarnings({"SameParameterValue", "unused"})
public enum MessageMode {
    /**
     * Info message, Sent to console and people with info perm; Usually contains important messages.
     */
    Info(ChatColor.GREEN),
    /**
     * Warning message, Sent to console and people with warning perm; Usually contains important messages about issues.
     */
    Warning(ChatColor.RED),
    /**
     * Critical message, Sent to console and people with warning perm; Same as warning, But it will be always sent to at least one operator, If none is online, It will be kept till one login
     */
    Critical(ChatColor.DARK_RED, "Warning"),
    /**
     * Debug message, Sent to console and people with debug perm; Useless messages just for debugging.
     */
    Debug(ChatColor.YELLOW),
    /**
     * Operator message, Sent to console and only operators;
     */
    Operator(ChatColor.AQUA, "*"),
    /**
     * Personal message, Sent to a target; It will have the plugin prefix
     */
    Personal(ChatColor.LIGHT_PURPLE, null, "", false),
    /**
     * Broadcast message, Sent to all online players; It will have no prefix
     */
    BroadCast(ChatColor.DARK_RED, null, null),
    /**
     * Critical broadcast message, Sent to all online players; It will have no prefix; If a player is offline the message will be kept for them until they login;
     */
    CriticalBroadCast(ChatColor.DARK_RED, null, null),
    /**
     * Simple message, Sent to a target; It won't have any prefix
     */
    Empty(ChatColor.RESET, null, null, false);

    public final String permission;
    public final boolean sendAll;
    private final ChatColor color;
    private final String prefix;

    MessageMode(ChatColor color) {
        this(color, "AUTO", "NAME");
    }
    MessageMode(ChatColor color, String permission) {
        this(color, permission, "NAME");
    }
    MessageMode(ChatColor color, String permission, String prefix) {
        this(color, permission, prefix, true);
    }
    MessageMode(ChatColor color, String permission, String prefix, boolean sendAll) {
        this.color = color;

        if (permission == null) this.permission = null;
        else if (permission.equals("AUTO")) this.permission = "Messages." + name();
        else if (permission.equals("*")) this.permission = "*";
        else this.permission = "Messages." + permission;

        if (prefix != null && prefix.equals("NAME")) this.prefix = name();
        else this.prefix = prefix;

        this.sendAll = sendAll;
    }

    public boolean hasPermission(SpigotPlugin plugin, Permissible permissible) {
        return permission == null || (permission.equals("*") ? permissible.isOp() : permissible.hasPermission(plugin.getName() + "." + permission));
    }

    public Prefix getPrefix(Messenger messenger) {
        if (prefix == null) return new Prefix(null);
        String prefix = String.format("§7[%s%s§7] %s§r", color, messenger.prefix, this.prefix.isBlank() ? "" : color + this.prefix + "§7 - ");
        return new Prefix(prefix);
    }

    protected static class Prefix {
        String prefix = "";
        String tab = "";

        Prefix(String prefix) {
            if (prefix == null) return;
            this.prefix = prefix;
            this.tab = String.join("", Collections.nCopies(ChatColor.stripColor(prefix).length(), " "));
        }
    }
}
