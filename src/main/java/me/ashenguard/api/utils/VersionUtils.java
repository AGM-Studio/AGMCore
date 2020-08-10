package me.ashenguard.api.utils;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public enum VersionUtils {
    V1_16("1.16", 16, true),
    V1_15("1.15", 15, true),
    V1_14("1.14", 14, true),
    V1_13("1.13", 13, true),
    V1_12("1.12", 12, false),
    V1_11("1.11", 11, false),
    V1_10("1.10", 10, false),
    V1_9("1.9", 9, false),
    V1_8("1.8", 8, false);

    public final String version;
    public final int id;
    public final boolean newAPI;

    VersionUtils(String version, int id, boolean newAPI) {
        this.version = version;
        this.id = id;
        this.newAPI = newAPI;
    }

    public boolean isHigherThan(VersionUtils version) {
        return version.id < this.id;
    }
    public boolean isLowerThan(VersionUtils version) {
        return version.id > this.id;
    }
    
    public static boolean isLegacy (@NotNull JavaPlugin plugin) {
        String pluginVersion = plugin.getServer().getVersion();
        for (VersionUtils version: VersionUtils.values())
            if (pluginVersion.contains(version.version)) return false;

        return false;
    }
}

