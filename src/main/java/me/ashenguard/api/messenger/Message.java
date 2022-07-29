package me.ashenguard.api.messenger;

import me.ashenguard.api.spigot.SpigotPlugin;

public interface Message {
    String getMessage(SpigotPlugin plugin);
    String getPath();
    String getDefault();
}
