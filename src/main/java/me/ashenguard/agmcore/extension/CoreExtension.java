package me.ashenguard.agmcore.extension;

import me.ashenguard.agmcore.AGMCore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;

public abstract class CoreExtension {
    protected AGMCore core = AGMCore.getInstance();

    protected File JAR = null;

    public abstract String getName();
    public abstract void onEnable();
    public abstract void onDisable();

    public InputStream getResource(String filename) {
        try {
            URLClassLoader classLoader = new URLClassLoader(new URL[]{JAR.toURI().toURL()});
            URL url = classLoader.getResource(filename);

            if (url == null) return null;

            URLConnection connection = url.openConnection();
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }
}
