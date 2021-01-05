package me.ashenguard.api.application;

import javafx.scene.image.ImageView;
import me.ashenguard.api.WebReader;
import me.ashenguard.api.utils.Version;

import java.util.List;


public class PLDependency {
    public static PLDependency find(List<PLDependency> list, String name) {
        for (PLDependency dependency: list) if (dependency.name.equals(name)) return dependency;
        return null;
    }

    private final String name;
    private final String url;
    private final ImageView logo;
    private final String version;
    private final boolean linked;

    public PLDependency(String name, String url, String logo, String version) {
        this.name = name;
        this.url = url;
        this.version = version;
        this.linked = url != null;

        if (logo != null) {
            this.logo = new ImageView(logo);
            this.logo.setPreserveRatio(true);

            double width = this.logo.getImage().getWidth(), height = this.logo.getImage().getHeight();
            double ratio = width / height;
            if (ratio < 1) {
                this.logo.setFitWidth(30 * ratio);
                this.logo.setFitHeight(30);
            } else {
                this.logo.setFitWidth(30);
                this.logo.setFitHeight(30 * ratio);
            }
        }
        else this.logo = new ImageView();
    }

    public PLDependency(String name, String url, String logo, Version version) {
        this(name, url, logo, version.toString(true));
    }

    public PLDependency(String name, int spigotID, String version) {
        this(name, "https://www.spigotmc.org/resources/" + spigotID + "/", "https://www.spigotmc.org/data/resource_icons/" + (spigotID / 1000) + "/" + spigotID + ".jpg", version);
    }

    public PLDependency(String name, int spigotID, Version version) {
        this(name, spigotID, version.toString(true));
    }

    public PLDependency(String name, int spigotID) {
        this(name, spigotID, new WebReader("https://api.spigotmc.org/legacy/update.php?resource=" + spigotID).read());
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        if (version == null) return "Latest";
        return version;
    }

    public ImageView getLogo() {
        return logo;
    }

    public boolean isLinked() {
        return linked;
    }
}
