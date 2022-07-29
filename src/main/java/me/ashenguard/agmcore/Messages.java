package me.ashenguard.agmcore;

import me.ashenguard.api.messenger.Message;
import me.ashenguard.api.spigot.SpigotPlugin;

public enum Messages implements Message {
    PlayerOnly("§cThis command can only be executed by a player.");

    private final String path;
    private final String value;

    Messages(String path, String value) {
        this.path = path;
        this.value = value;
    }
    Messages(String value) {
        this.path = name();
        this.value = value;
    }

    @Override
    public String getMessage(SpigotPlugin plugin) {
        String message = plugin.translation.get(getPath(), null);
        return message != null ? message : getDefault();
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getDefault() {
        return value;
    }
}
