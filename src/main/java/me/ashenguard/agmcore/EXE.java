package me.ashenguard.agmcore;

import me.ashenguard.api.application.EXEWindow;
import me.ashenguard.api.application.PLDependency;
import me.ashenguard.api.utils.Version;

public class EXE extends EXEWindow {

    public static void main(String[] args) {
        new EXE();
        launch(args);
    }

    public EXE() {
        super("AGMCore", 83245, new Version(1, 2));
        dependencies.add(new PLDependency("PlaceholderAPI", 6245));

        discord = "https://discord.gg/6exsySK";
    }
}
