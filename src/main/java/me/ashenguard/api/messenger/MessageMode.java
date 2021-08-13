package me.ashenguard.api.messenger;

import me.ashenguard.api.spigot.SpigotPlugin;
import org.bukkit.ChatColor;
import org.bukkit.permissions.Permissible;

import java.util.Collections;

@SuppressWarnings({"SameParameterValue", "unused"})
public enum MessageMode {
    Info(ChatColor.GREEN),
    Warning(ChatColor.RED),
    Debug(ChatColor.YELLOW),
    Operator(ChatColor.AQUA, "*"),
    Personal(ChatColor.LIGHT_PURPLE, null, ""),
    BroadCast(ChatColor.DARK_RED, null, null),
    Empty(ChatColor.RESET, null, null);

    public final String permission;
    private final ChatColor color;
    private final String prefix;

    MessageMode(ChatColor color) {
        this(color, "AUTO", "NAME");
    }
    MessageMode(ChatColor color, String permission) {
        this(color, permission, "NAME");
    }
    MessageMode(ChatColor color, String permission, String prefix) {
        this.color = color;

        if (permission == null) this.permission = null;
        else if (permission.equals("AUTO")) this.permission = "Messages." + name();
        else if (permission.equals("*")) this.permission = "*";
        else this.permission = "Messages." + permission;

        if (prefix != null && prefix.equals("NAME")) this.prefix = name();
        else this.prefix = prefix;
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
            this.tab = String.join("", Collections.nCopies(prefix.length(), " "));
        }
    }
}
