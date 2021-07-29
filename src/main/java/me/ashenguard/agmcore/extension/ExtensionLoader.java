package me.ashenguard.agmcore.extension;

import me.ashenguard.agmcore.AGMCore;
import me.ashenguard.api.messenger.Messenger;
import me.ashenguard.api.utils.FileUtils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ExtensionLoader {
    private final static AGMCore CORE = AGMCore.getInstance();
    private final static Messenger MESSENGER = CORE.messenger;

    private final static File FOLDER = new File(CORE.getDataFolder(), "Extensions");
    static { if (!FOLDER.exists() && FOLDER.mkdirs()) MESSENGER.Debug("General", "Extension folder wasn't found, A new one created"); }

    public boolean registerExtension(CoreExtension extension, String filename) {
        if (extension == null) return false;

        extension.JAR = new File(FOLDER, filename);
        extension.onEnable();


        MESSENGER.Debug("Extensions", "Extension registered successfully", "Extension= §6" + extension.getName());
        return true;
    }

    public CoreExtension registerExtension(String fileName) {
        List<Class<?>> subs = FileUtils.getClasses(FOLDER, fileName, CoreExtension.class);
        MESSENGER.Debug("Extensions", String.format("Found %d subs for the extension", subs != null ? subs.size() : 0));
        if (subs == null || subs.isEmpty()) return null;

        CoreExtension extension = createInstance(subs.get(0));
        if (registerExtension(extension, fileName)) return extension;
        return null;
    }

    public HashMap<String, CoreExtension> registerAllExtensions() {
        HashMap<String, CoreExtension> list = new HashMap<>();
        for (String fileName: Arrays.stream(FOLDER.listFiles()).map(File::getName).filter(name -> name.endsWith(".jar")).collect(Collectors.toList()))
            try {
                MESSENGER.Debug("Extensions", "Found an extension, Registration has been started", "Filename= §6" + fileName);
                CoreExtension extension = registerExtension(fileName);
                list.put(extension.getName(), extension);
            } catch (Exception exception) {
                MESSENGER.Warning("Unable to register extension named \"" + fileName + "\"");
                MESSENGER.handleException(exception);
            }
        return list;
    }

    private CoreExtension createInstance(Class<?> clazz) {
        if (clazz == null) return null;
        if (!CoreExtension.class.isAssignableFrom(clazz)) return null;

        CoreExtension expansion = null;
        try {
            Constructor<?>[] constructors = clazz.getConstructors();
            if (constructors.length == 0) {
                expansion = (CoreExtension) clazz.getDeclaredConstructor().newInstance();
            } else {
                for (Constructor<?> ctor : constructors) {
                    if (ctor.getParameterTypes().length == 0) {
                        expansion = (CoreExtension) ctor.newInstance();
                        break;
                    }
                }
            }
        } catch (Throwable throwable) {
            MESSENGER.Warning("Failed to initialize extension from class: " + clazz.getName());
            MESSENGER.handleException(throwable);
        }

        return expansion;
    }
}
