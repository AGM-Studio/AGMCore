package me.ashenguard.agmcore;

import me.ashenguard.api.versions.Version;
import me.ashenguard.spigotapplication.SpigotPanel;

public class Panel extends SpigotPanel {
    public Panel() {
        super(83245, new Version(1, 2));
        this.addDependency(6245);
        this.setDescription("Installation:\n" +
                "Add the JAR file to your server plugins folder.\n\n" +
                "About this plugin:\n" +
                "This plugin is just an API library which will provide a lot of useful tools and libraries which can be used in other plugins. Nothing game changer will happen with AGMCore only.");
    }

    public static void main(String[] args) {
        launch();
    }
}
