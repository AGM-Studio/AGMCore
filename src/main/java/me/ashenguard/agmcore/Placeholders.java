package me.ashenguard.agmcore;

import me.ashenguard.lib.statistics.Playtime;
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
        long playtime = Playtime.getPlaytime(player, value);
        if (playtime >= 0) return String.valueOf(playtime);
        if (value.equalsIgnoreCase("_TOTAL"))
            return String.format("%d day, %d:%d",
                    Playtime.getPlaytime(player, TimeUnit.DAYS),
                    Playtime.getPlaytime(player, TimeUnit.HOURS),
                    Playtime.getPlaytime(player, TimeUnit.MINUTES));
        return String.valueOf(Playtime.getPlaytime(player));
    }
}
