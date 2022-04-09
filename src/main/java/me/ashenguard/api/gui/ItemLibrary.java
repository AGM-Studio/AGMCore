package me.ashenguard.api.gui;

import me.ashenguard.agmcore.AGMCore;
import me.ashenguard.exceptions.PluginNotEnabled;

import java.io.File;
import java.io.FilenameFilter;

public class ItemLibrary {

    public ItemLibrary() {
        if (AGMCore.getInstance() == null || !AGMCore.getInstance().isEnabled()) throw new PluginNotEnabled("AGMCore is not Enabled");

        File holder = new File(AGMCore.getInstance().getDataFolder(), "GUI/ItemLibrary");
        if (!holder.exists()){
            //noinspection ResultOfMethodCallIgnored
            holder.mkdirs();
        }
        FilenameFilter filter = (dir, name) -> {
            String extension = name.substring(name.lastIndexOf("."));
            return extension.equalsIgnoreCase("yml") || extension.equalsIgnoreCase("yaml");
        };

        for (File file: holder.listFiles(filter)) {
            loadGUIConfig(file);
        }
    }

    private void loadGUIConfig(File file) {

    }
}
