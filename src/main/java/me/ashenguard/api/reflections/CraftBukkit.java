package me.ashenguard.api.reflections;

import org.bukkit.Bukkit;

public class CraftBukkit {
    public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    public static Class<?> getClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + VERSION + "." + name);
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }
}
