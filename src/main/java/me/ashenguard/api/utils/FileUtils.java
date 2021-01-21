package me.ashenguard.api.utils;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

@SuppressWarnings("unused")
public class FileUtils {
    public static List<Class<?>> getClasses(File folder, Class<?> type) {
        return getClasses(folder, null, type);
    }
    public static List<Class<?>> getClasses(File folder, String fileName, Class<?> type) {
        List<Class<?>> list = new ArrayList<>();

        try {
            if (!folder.exists()) return list;

            FilenameFilter fileNameFilter = (dir, name) -> {
                boolean isJar = name.endsWith(".jar");
                if (fileName != null) {
                    return isJar && name.substring(0, name.length() - 4)
                            .equalsIgnoreCase(fileName.substring(0, fileName.length() - 4));
                }

                return isJar;
            };

            File[] jars = folder.listFiles(fileNameFilter);
            if (jars == null) return list;

            for (File file : jars) {
                gather(file.toURI().toURL(), list, type);
            }

            return list;
        } catch (Throwable ignored) {}

        return list;
    }

    public static boolean writeFile(File file, String string) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(string);
            writer.close();
            return true;
        } catch (IOException ignored) {}
        return false;
    }
    public static boolean writeFile(File file, WebReader webReader) {
        return writeFile(file, webReader.read());
    }
    public static boolean writeFile(File file, InputStream stream) {
        try {
            return writeFile(file, new String(IOUtils.toByteArray(stream)));
        } catch (IOException ignored) {}
        return false;
    }

    public static String readStream(InputStream stream) {
        String string = "";
        try {
            return new String(IOUtils.toByteArray(stream));
        } catch (IOException ignored) {}
        return string;
    }

    private static void gather(URL jar, List<Class<?>> list, Class<?> clazz) {
        try (URLClassLoader cl = new URLClassLoader(new URL[]{jar}, clazz.getClassLoader());
             JarInputStream jis = new JarInputStream(jar.openStream())) {

            JarEntry entry;
            while ((entry = jis.getNextJarEntry()) != null) {
                String name = entry.getName();
                if (name == null || name.isEmpty()) continue;

                if (name.endsWith(".class")) {
                    name = name.substring(0, name.length() - 6).replace('/', '.');

                    Class<?> loaded = cl.loadClass(name);
                    if (clazz.isAssignableFrom(loaded)) list.add(loaded);
                }
            }
        } catch (Throwable ignored) {}
    }
}
