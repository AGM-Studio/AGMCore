package me.ashenguard.api.utils;

import org.bukkit.Bukkit;

public class Reflections {
    public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    public static Class<?> getClass(String name) {
        Class<?> cls = getClassFrom(String.format("org.bukkit.craftbukkit.%s.%s", VERSION, name));
        if (cls != null) return cls;

        cls = getClassFrom(String.format("org.bukkit.craftbukkit.%s", name));
        if (cls != null) return cls;

        cls = getClassFrom(String.format("net.minecraft.server.%s.%s", VERSION, name));
        if (cls != null) return cls;

        cls = getClassFrom(String.format("net.minecraft.server.%s", name));
        return cls;
    }

    public static Class<?> getClass(String... names) {
        for (String name: names) {
            Class<?> cls = getClass(name);
            if (cls != null) return cls;
        }
        return null;
    }

    private static Class<?> getClassFrom(String address) {
        try {
            return Class.forName(address);
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }
}
