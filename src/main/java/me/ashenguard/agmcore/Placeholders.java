package me.ashenguard.agmcore;

import me.ashenguard.lib.PlaytimeManager;
import me.ashenguard.api.placeholder.PHExtension;
import me.ashenguard.api.placeholder.Placeholder;
import org.bukkit.OfflinePlayer;

import java.util.concurrent.TimeUnit;

public class Placeholders extends PHExtension {
    public Placeholders() {
        super(AGMCore.getInstance());

        new Placeholder(this, s -> s.startsWith("Playtime"), s -> s.substring(8), this::getPlaytime);
    }

    private String getPlaytime(OfflinePlayer player, String value) {
        long playtime = PlaytimeManager.getPlaytime(player, value);
        if (playtime >= 0) return String.valueOf(playtime);
        if (value.equalsIgnoreCase("_TOTAL"))
            return String.format("%d day, %d:%d",
                    PlaytimeManager.getPlaytime(player, TimeUnit.DAYS),
                    PlaytimeManager.getPlaytime(player, TimeUnit.HOURS),
                    PlaytimeManager.getPlaytime(player, TimeUnit.MINUTES));
        return String.valueOf(PlaytimeManager.getPlaytime(player));
    }
}
