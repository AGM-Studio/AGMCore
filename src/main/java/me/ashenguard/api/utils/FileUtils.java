package me.ashenguard.api.utils;

import me.ashenguard.api.messenger.Messenger;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

@SuppressWarnings({"unused", "ResultOfMethodCallIgnored"})
public class FileUtils {
    public static List<Class<?>> getClasses(File folder, Class<?> type) {
        return getClasses(folder, null, type);
    }
    public static List<Class<?>> getClasses(File folder, String fileName, Class<?> type) {
        List<Class<?>> list = new ArrayList<>();

        try {
            if (!folder.exists()) return list;

            final String finalName;
            if (fileName != null && fileName.endsWith(".jar")) finalName = fileName.substring(0, fileName.length() - 4);
            else finalName = fileName;

            FilenameFilter fileNameFilter = (dir, file) -> {
                boolean isJar = file.endsWith(".jar");
                String name = file.substring(0, file.length() - 4);
                if (finalName != null) return isJar && name.equalsIgnoreCase(finalName);
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

    public static boolean writeFile(File file, String string) {
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(string);
            writer.close();
            return true;
        } catch (IOException ignored) {
            return false;
        }
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
        return readStream(stream, null);
    }
    public static String readStream(InputStream stream, Messenger messenger) {
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = stream.read(buffer)) != -1) result.write(buffer, 0, length);
            result.close();
            return result.toString(StandardCharsets.UTF_8);
        } catch (Throwable throwable) {
            if (messenger != null) messenger.handleException(throwable);
            return "INPUT_STREAM_UNREADABLE";
        }
    }

    public static InputStream getResource(File JAR, String filename) {
        return getResource(JAR, filename, null);
    }
    public static InputStream getResource(File JAR, String filename, Messenger messenger) {
        try {
            URLClassLoader classLoader = new URLClassLoader(new URL[]{JAR.toURI().toURL()});
            URL url = classLoader.getResource(filename);

            if (url == null) return null;

            URLConnection connection = url.openConnection();
            return connection.getInputStream();
        } catch (IOException exception) {
            if (messenger != null) messenger.handleException(exception);
            return null;
        }
    }

    public static boolean mkdirs(File file) {
        if (file.isFile()) file = file.getParentFile();
        return file.mkdirs();
    }
}
