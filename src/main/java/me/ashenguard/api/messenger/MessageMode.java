package me.ashenguard.api.messenger;

import org.bukkit.ChatColor;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public enum MessageMode {
    Info(ChatColor.GREEN),
    Warning(ChatColor.RED),
    Debug(ChatColor.YELLOW),
    Personal(ChatColor.LIGHT_PURPLE, null, null),
    Operator(ChatColor.AQUA, "*"),
    BroadCast(ChatColor.DARK_RED, null, null);

    public final String permission;
    private final ChatColor color;
    private final String prefix;

    MessageMode(ChatColor color) {
        this.color = color;
        this.permission = "Messages." + name();
        this.prefix = name();
    }
    MessageMode(ChatColor color, String permission) {
        this.color = color;
        if (permission == null) this.permission = null;
        else this.permission = "Messages." + permission;
        this.prefix = name();
    }
    MessageMode(ChatColor color, String permission, String prefix) {
        this.color = color;
        if (permission == null) this.permission = null;
        else this.permission = "Messages." + permission;
        this.prefix = prefix;
    }

    public boolean hasPermission(JavaPlugin plugin, Permissible permissible) {
        return permission == null || (permission.equals("*") ? permissible.isOp() : permissible.hasPermission(plugin.getName() + "." + permission));
    }

    @NotNull
    public String getPrefix(JavaPlugin plugin) {
        return String.format("§7[%s]§r %s", Messenger.getInstance(plugin).prefix, prefix == null ? "" : color + prefix + "§r ");
    }
    @NotNull
    public String getSpace(JavaPlugin plugin) {
        String prefix = ChatColor.stripColor(getPrefix(plugin));
        return String.join("", Collections.nCopies(prefix.length(), " "));
    }
    @NotNull
    public String getPrefix(JavaPlugin plugin, boolean first) {
        return first ? getPrefix(plugin) : getSpace(plugin);
    }
}
